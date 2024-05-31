package io.github.steaf23.bingoreloaded.tasks.tracker;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.event.BingoTaskProgressCompletedEvent;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.*;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.util.ExtraMath;
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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private Map<BingoTask, List<TaskProgress>> progressMap;

    public TaskProgressTracker(BingoGame game) {
        this.game = game;
        this.progressMap = new HashMap<>();
    }

    public void startTrackingTask(BingoTask task) {
        progressMap.put(task, new ArrayList<>());
        for (BingoParticipant participant : game.getTeamManager().getParticipants()) {
            // only track progress if the participant has to complete the task.
            if (!participant.getTeam().card.getTasks().contains(task)) {
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
                // travel statistics are counted * 1000
                StatisticTask statisticTask = (StatisticTask) task.data;
                if (statisticTask.statistic().getCategory() == BingoStatistic.StatisticCategory.TRAVEL)
                {
                    finalCount *= 1000;
                }

                // reset statistic to 0 for player
                setPlayerStatistic(statisticTask.statistic(), participant, 0);
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

    public void handlePlayerStatIncrement(final PlayerStatisticIncrementEvent event) {
        BingoParticipant participant = getValidParticipant(event.getPlayer());
        if (participant == null) {
            return;
        }

        if (game.getDeathMatchTask() != null)
            return;

        updateProgressFromEvent(participant, (task, progress) -> {
            if (task.type != BingoTask.TaskType.STATISTIC) {
                return false;
            }
            BingoStatistic statistic = new BingoStatistic(event.getStatistic(), event.getEntityType(), event.getMaterial());
            StatisticTask data = (StatisticTask) task.data;

            if (!data.statistic().equals(statistic) ||
                    data.getCount() != event.getNewValue()) {
                return false;
            }

            progress.addProgress(data.getCount());
            return tryCompleteTask(task, progress);
        });
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
                var slotEvent = new BingoCardTaskCompleteEvent(deathMatchTask, participant, true);
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
        //TODO: update progress of periodic statistic tasks..
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
            player.getTeam().card.handleTaskCompleted(player, task, game.getGameTime());
        }

        var progressCompletedEvent = new BingoTaskProgressCompletedEvent(player.getSession(), task);
        Bukkit.getPluginManager().callEvent(progressCompletedEvent);
        return true;
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
