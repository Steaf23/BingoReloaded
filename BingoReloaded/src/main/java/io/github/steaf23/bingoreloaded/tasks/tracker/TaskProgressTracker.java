package io.github.steaf23.bingoreloaded.tasks.tracker;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.event.BingoDeathmatchTaskCompletedEvent;
import io.github.steaf23.bingoreloaded.event.BingoStatisticCompletedEvent;
import io.github.steaf23.bingoreloaded.event.BingoTaskProgressCompletedEvent;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public class TaskProgressTracker
{
    public class TaskProgress
    {
        private final BingoParticipant participant;
        private final int progressStart;
        private int progressLeft;

        public TaskProgress(BingoParticipant participant, int progressAmount) {
            this.participant = participant;
            this.progressStart = progressAmount;
            this.progressLeft = progressStart;
        }

        public boolean isDone() {
            return progressLeft == 0;
        }

        public void addProgress(int amount) {
            progressLeft = Math.max(0, progressLeft - amount);
        }

        public void setProgress(int amount) {
            progressLeft = Math.max(0, progressStart - amount);
        }
    }

    private final BingoGame game;
    private final Map<BingoTask, List<TaskProgress>> progressMap;
    private final StatisticTracker statisticTracker;

    public TaskProgressTracker(BingoGame game) {
        this.game = game;
        this.progressMap = new HashMap<>();
        this.statisticTracker = new StatisticTracker();
    }

    public void startTrackingTask(BingoTask task) {
        progressMap.put(task, new ArrayList<>());
        for (BingoParticipant participant : game.getTeamManager().getParticipants()) {
            // only track progress if the participant has to complete the task.
            BingoCard card = participant.getTeam().getCard();
            if (card == null || !card.getTasks().contains(task)) {
                continue;
            }

            int finalCount = task.getCount();

            // reset any progress already made beforehand
            if (task.type == BingoTask.TaskType.ADVANCEMENT) {
                // revoke advancement from player
                AdvancementTask advancementTask = (AdvancementTask) task.data;
                participant.sessionPlayer().ifPresent(player -> {
                    AdvancementProgress progress = player.getAdvancementProgress(advancementTask.advancement());
                    progress.getAwardedCriteria().forEach(progress::revokeCriteria);
                });
            } else if (task.type == BingoTask.TaskType.STATISTIC) {
                StatisticTask statisticTask = (StatisticTask) task.data;
                // travel statistics are counted * 1000
//                if (statisticTask.statistic().getCategory() == BingoStatistic.StatisticCategory.TRAVEL)
//                {
//                    finalCount *= 1000;
//                }

                // the stat tracker will reset progress to 0 for every statistic added.
                statisticTracker.addStatistic(statisticTask, participant);
            } else {
                // No progress to reset for item tasks
            }

            // add task to progress tracker
            progressMap.get(task).add(new TaskProgress(participant, finalCount));
        }
    }

    public void handlePlayerAdvancementDone(final PlayerAdvancementDoneEvent event) {
        BingoParticipant participant = getValidParticipant(event.getPlayer());
        if (participant == null) {
            return;
        }

        if (game.getDeathMatchTask() != null)
            return;

        updateProgressFromEvent(participant, (task, progress) -> {
            if (task.type != BingoTask.TaskType.ADVANCEMENT) {
                return false;
            }
            AdvancementTask data = (AdvancementTask) task.data;

            if (!data.advancement().getKey().equals(event.getAdvancement().getKey())) {
                return false;
            }

            progress.addProgress(1);
            return tryCompleteTask(task, progress);
        });
    }

    public void handleBingoStatisticCompleted(final BingoStatisticCompletedEvent event) {
        BingoParticipant participant = getValidParticipant(event.getParticipant());
        if (participant == null) {
            return;
        }

        if (game.getDeathMatchTask() != null)
            return;

        updateProgressFromEvent(participant, (task, progress) -> {
            if (task.type != BingoTask.TaskType.STATISTIC) {
                return false;
            }
            BingoStatistic statistic = event.getStatistic();
            StatisticTask data = (StatisticTask) task.data;

            if (!data.statistic().equals(statistic)) {
                return false;
            }

            progress.setProgress(data.getCount());
            return tryCompleteTask(task, progress);
        });
    }

    public void handlePlayerStatIncrement(final PlayerStatisticIncrementEvent event) {
        statisticTracker.handleStatisticIncrement(event, game);
    }

    private ItemStack completeItemSlot(ItemStack item, BingoParticipant participant) {
        if (participant == null) {
            return item;
        }

        if (participant.getTeam().outOfTheGame) {
            return item;
        }

        BingoTask deathMatchTask = game.getDeathMatchTask();
        if (deathMatchTask != null) {
            if (item.getType().equals(deathMatchTask.material)) {
                deathMatchTask.complete(participant, game.getGameTime());
                var slotEvent = new BingoDeathmatchTaskCompletedEvent(participant.getSession(), deathMatchTask);
                Bukkit.getPluginManager().callEvent(slotEvent);
            }
            return item;
        }

        Set<BingoTask> tasksToRemove = new HashSet<>();
        for (BingoTask task : progressMap.keySet()) {
            List<TaskProgress> progressList = progressMap.get(task);
            for (TaskProgress progress : progressMap.get(task)) {
                if (!progress.participant.equals(participant)) {
                    continue;
                }

                if (task.type != BingoTask.TaskType.ITEM) {
                    continue;
                }
                ItemTask data = (ItemTask) task.data;
                if (!data.material().equals(item.getType()) || data.count() > item.getAmount()) {
                    continue;
                }

                progress.setProgress(item.getAmount());
                if (!tryCompleteTask(task, progress)) {
                    continue;
                }

                if (participant.sessionPlayer().isPresent()) {
                    if (game.getConfig().removeTaskItems) {
                        item.setAmount(item.getAmount() - data.getCount());
                    }
                    participant.sessionPlayer().get().updateInventory();
                }

                tasksToRemove.add(task);
            }
        }

        tasksToRemove.forEach(t -> progressMap.remove(t));

        updateProgressFromEvent(participant, (task, progress) -> {
            if (task.type != BingoTask.TaskType.ITEM) {
                return false;
            }
            ItemTask data = (ItemTask) task.data;
            if (!data.material().equals(item.getType()) || data.count() > item.getAmount()) {
                return false;
            }

            progress.setProgress(item.getAmount());
            if (!tryCompleteTask(task, progress)) {
                return false;
            }

            participant.sessionPlayer().ifPresent(player -> {
                if (game.getConfig().removeTaskItems) {
                    item.setAmount(item.getAmount() - data.getCount());
                }
            });

            return true;
        });
        return item;
    }

    public void handleInventoryClicked(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        BingoParticipant participant = getValidParticipant(player);
        if (participant == null) {
            return;
        }

        // player tries to grab item in inventory normally
        if (event.getSlotType() == InventoryType.SlotType.RESULT && event.getClick() != ClickType.SHIFT_LEFT) {
            BingoReloaded.scheduleTask(task -> {
                ItemStack resultStack = player.getItemOnCursor();
                completeItemSlot(resultStack, participant);
            });
            return;
        }

        BingoReloaded.scheduleTask(task -> {
            // Other contents are updated, so we want to check the full inventory for task items..
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack != null) {
                    stack = completeItemSlot(stack, participant);
                }
            }

            // Sometimes item changes are not recorded instantly for some reason, so double check if the cursor item can be completed as a task.
            ItemStack stack = player.getItemOnCursor();
            stack = completeItemSlot(stack, participant);
        });
    }

    public void handlePlayerPickupItem(final EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        BingoParticipant participant = getValidParticipant(player);
        if (participant == null) {
            return;
        }

        ItemStack stack = event.getItem().getItemStack();
        int amount = stack.getAmount();
        ItemStack oldStack = stack;
        stack = completeItemSlot(stack, participant);
        if (amount != stack.getAmount()) {
            event.setCancelled(true);
            ItemStack resultStack = stack.clone();
            event.getItem().setItemStack(stack);

            if (resultStack.getType() == Material.AIR || resultStack.getAmount() <= 0) {
                return;
            }

            BingoReloaded.scheduleTask(task -> {
                participant.sessionPlayer().ifPresent(p -> p.getWorld().dropItem(event.getItem().getLocation(), resultStack));
                event.getItem().remove();
            });
        }
    }

    public void handlePlayerDroppedItem(final PlayerDropItemEvent event) {
        BingoParticipant participant = getValidParticipant(event.getPlayer());
        if (participant == null) {
            return;
        }

        BingoReloaded.scheduleTask(task -> {
            ItemStack stack = event.getItemDrop().getItemStack();
            stack = completeItemSlot(stack, participant);
        });
    }

    public void updateStatisticProgress() {
        statisticTracker.updateProgress();
    }

    public void removeTask(BingoTask task) {
        progressMap.remove(task);
        if (task.type == BingoTask.TaskType.STATISTIC) {
            statisticTracker.removeStatistic((StatisticTask) task.data);
        }
    }

    private boolean tryCompleteTask(BingoTask task, TaskProgress progress) {
        if (!progress.isDone()) {
            return false;
        }

        BingoParticipant player = progress.participant;
        if (!(player.getSession().getPhase() instanceof BingoGame game)) {
            return false;
        }

        if (!task.complete(player, game.getGameTime()))
            return false;

        if (player.getTeam() != null) {
            player.getTeam().getCard().handleTaskCompleted(player, task, game.getGameTime());
        }

        var progressCompletedEvent = new BingoTaskProgressCompletedEvent(player.getSession(), task);
        Bukkit.getPluginManager().callEvent(progressCompletedEvent);
        return true;
    }

    private @Nullable BingoParticipant getValidParticipant(@Nullable BingoParticipant participant) {
        return getValidParticipant(participant.sessionPlayer().orElseGet(null));
    }

    private @Nullable BingoParticipant getValidParticipant(@Nullable Player player) {
        if (player == null) {
            return null;
        }
        if (!game.getSession().hasPlayer(player)) {
            return null;
        }
        BingoParticipant participant = game.getTeamManager().getPlayerAsParticipant(player);
        if (participant == null)
            return null;

        if (participant.getTeam().outOfTheGame)
            return null;

        return participant;
    }

    public void setPlayerStatistic(BingoStatistic statistic, BingoParticipant player, int value)
    {
        if (player.sessionPlayer().isEmpty())
            return;

        Player gamePlayer = player.sessionPlayer().get();

        if (statistic.hasMaterialComponent())
        {
            gamePlayer.setStatistic(statistic.stat(), statistic.materialType(), value);
        }
        else if (statistic.hasEntityComponent())
        {
            gamePlayer.setStatistic(statistic.stat(), statistic.entityType(), value);
        }
        else
        {
            gamePlayer.setStatistic(statistic.stat(), value);
        }
    }

    /**
     * update progress for given participant about the task given by the updateFunction, using the task's existing progress
     * When the update function returns true the task is considered completed and will be removed from the tracker.
     * @param participant
     * @param updateFunction
     */
    private void updateProgressFromEvent(BingoParticipant participant, BiFunction<BingoTask, TaskProgress, Boolean> updateFunction) {
        Set<BingoTask> tasksToRemove = new HashSet<>();
        for (BingoTask task : progressMap.keySet()) {
            List<TaskProgress> progressList = progressMap.get(task);
            for (TaskProgress progress : progressMap.get(task)) {
                if (!progress.participant.equals(participant)) {
                    continue;
                }

                if (updateFunction.apply(task, progress)) {
                    tasksToRemove.add(task);
                }
            }
        }

        tasksToRemove.forEach(t -> progressMap.remove(t));
    }
}
