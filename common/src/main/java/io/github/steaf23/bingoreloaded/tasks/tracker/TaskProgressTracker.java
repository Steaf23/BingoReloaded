package io.github.steaf23.bingoreloaded.tasks.tracker;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.event.BingoDeathmatchTaskCompletedEvent;
import io.github.steaf23.bingoreloaded.event.BingoStatisticCompletedEvent;
import io.github.steaf23.bingoreloaded.event.BingoTaskProgressCompletedEvent;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class TaskProgressTracker
{
    public static class TaskProgress
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
    private final Map<GameTask, List<TaskProgress>> progressMap;
    private final StatisticTracker statisticTracker;

    public TaskProgressTracker(BingoGame game) {
        this.game = game;
        this.progressMap = new HashMap<>();
        this.statisticTracker = new StatisticTracker();
        if (!game.getConfig().getOptionValue(BingoOptions.DISABLE_ADVANCEMENTS)) {
            ConsoleMessenger.log("Revoking all advancements from participants...");
            DebugLogger.addLog("Revoking all advancements");

            List<PlayerHandle> allPlayers = new ArrayList<>();

            for (BingoParticipant participant : game.getTeamManager().getParticipants()) {
                 participant.sessionPlayer().ifPresent(allPlayers::add);
            }

            Bukkit.advancementIterator().forEachRemaining(advancement -> {
                for (PlayerHandle p : allPlayers) {
                    AdvancementProgress progress = p.getAdvancementProgress(advancement);
                    for (String criteria : advancement.getCriteria()) {
                        progress.revokeCriteria(criteria);
                    }
                }
            });
        }
    }

    public void startTrackingTask(GameTask task) {
        progressMap.put(task, new ArrayList<>());
        for (BingoParticipant participant : game.getTeamManager().getParticipants()) {
            // only track progress if the participant has to complete the task.
            Optional<TaskCard> card = participant.getTeam().getCard();
            if (card.isEmpty() || !card.get().getTasks().contains(task)) {
                continue;
            }

            int finalCount = task.data.getRequiredAmount();
            TaskData.TaskType type = task.data.getType();

            // reset any progress already made beforehand
            if (type == TaskData.TaskType.ADVANCEMENT) {
                // revoke advancement from player
                AdvancementTask advancementTask = (AdvancementTask) task.data;
                participant.sessionPlayer().ifPresent(player -> {
                    AdvancementProgress progress = player.getAdvancementProgress(advancementTask.advancement());
                    progress.getAwardedCriteria().forEach(progress::revokeCriteria);
                    DebugLogger.addLog("Revoking advancement " + advancementTask.advancement().getKey().getKey() + " for player " + player.getName());
                });
            } else if (type == TaskData.TaskType.STATISTIC) {
                StatisticTask statisticTask = (StatisticTask) task.data;
                // travel statistics are counted * 1000
//                if (statisticTask.statistic().getCategory() == BingoStatistic.StatisticCategory.TRAVEL)
//                {
//                    finalCount *= 1000;
//                }

                // the stat tracker will reset progress to 0 for every statistic added.
                statisticTracker.addStatistic(statisticTask, participant);
            }
            // No progress to reset for item tasks

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

        if (!event.getAdvancement().getKey().getKey().startsWith("recipes"))
            DebugLogger.addLog("Advancement " + event.getAdvancement().getKey().getKey() + " completed by " + event.getPlayer().getName());

        updateProgressFromEvent(participant, (task, progress) -> {
            if (task.taskType() != TaskData.TaskType.ADVANCEMENT) {
                return false;
            }
            AdvancementTask data = (AdvancementTask) task.data;

            if (!data.advancement().getKey().equals(event.getAdvancement().getKey())) {
                return false;
            }

            progress.addProgress(1);
            DebugLogger.addLog("Completed task " + event.getAdvancement().getKey().getKey() + " completed by player " + participant.getName());
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
            if (task.taskType() != TaskData.TaskType.STATISTIC) {
                return false;
            }
            BingoStatistic statistic = event.getStatistic();
            StatisticTask data = (StatisticTask) task.data;

            if (!data.statistic().equals(statistic)) {
                return false;
            }

            progress.setProgress(data.getRequiredAmount());
            return tryCompleteTask(task, progress);
        });
    }

    public void handlePlayerStatIncrement(final PlayerStatisticIncrementEvent event) {
        statisticTracker.handleStatisticIncrement(event, game);
    }

    private ItemStack completeItemSlot(ItemStack item, BingoParticipant participant) {
        if (participant == null || participant.getTeam() == null) {
            return item;
        }

        if (participant.getTeam().outOfTheGame) {
            return item;
        }

        GameTask deathMatchTask = game.getDeathMatchTask();
        if (deathMatchTask != null) {
            if (item.getType().equals(deathMatchTask.material())) {
                deathMatchTask.complete(participant, game.getGameTime());
                var slotEvent = new BingoDeathmatchTaskCompletedEvent(participant.getSession(), deathMatchTask);
                Bukkit.getPluginManager().callEvent(slotEvent);
            }
            return item;
        }

        Set<GameTask> tasksToRemove = new HashSet<>();
        for (GameTask task : progressMap.keySet()) {
            for (TaskProgress progress : progressMap.get(task)) {
                if (!progress.participant.equals(participant)) {
                    continue;
                }

                if (task.taskType() != TaskData.TaskType.ITEM) {
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
                    if (game.getConfig().getOptionValue(BingoOptions.REMOVE_TASK_ITEMS)) {
                        item.setAmount(item.getAmount() - data.getRequiredAmount());
                    }
                    participant.sessionPlayer().get().updateInventory();
                }

                tasksToRemove.add(task);
            }
        }

        tasksToRemove.forEach(progressMap::remove);

        updateProgressFromEvent(participant, (task, progress) -> {
            if (task.taskType() != TaskData.TaskType.ITEM) {
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
                if (game.getConfig().getOptionValue(BingoOptions.REMOVE_TASK_ITEMS)) {
                    item.setAmount(item.getAmount() - data.getRequiredAmount());
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
                    completeItemSlot(stack, participant);
                }
            }

            // Sometimes item changes are not recorded instantly for some reason, so double check if the cursor item can be completed as a task.
            ItemStack stack = player.getItemOnCursor();
            completeItemSlot(stack, participant);
        });
    }

    public void handlePlayerPickupItem(final EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof PlayerHandle player)) {
            return;
        }

        BingoParticipant participant = getValidParticipant(player);
        if (participant == null) {
            return;
        }

        ItemStack stack = event.getItem().getItemStack();
        int amount = stack.getAmount();
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

    public void removeTask(GameTask task) {
        progressMap.remove(task);
        if (task.taskType() == TaskData.TaskType.STATISTIC) {
            statisticTracker.removeStatistic((StatisticTask) task.data);
        }
    }

    private boolean tryCompleteTask(GameTask task, TaskProgress progress) {
        if (!progress.isDone()) {
            return false;
        }

        BingoParticipant player = progress.participant;
        if (!(player.getSession().getPhase() instanceof BingoGame)) {
            return false;
        }

        if (!task.complete(player, game.getGameTime()))
            return false;

        if (player.getTeam() == null) {
            ConsoleMessenger.bug("Player " + player.getName() + " is not in a valid team!", this);
        }
        player.getTeam().getCard().ifPresent(card ->
                card.handleTaskCompleted(player, task, game.getGameTime()));

        var progressCompletedEvent = new BingoTaskProgressCompletedEvent(player.getSession(), task);
        Bukkit.getPluginManager().callEvent(progressCompletedEvent);
        return true;
    }

    private @Nullable BingoParticipant getValidParticipant(@Nullable BingoParticipant participant) {
        return getValidParticipant(participant != null ? participant.sessionPlayer().orElse(null) : null);
    }

    private @Nullable BingoParticipant getValidParticipant(@Nullable PlayerHandle player) {
        if (player == null) {
            return null;
        }
        if (!game.getSession().hasPlayer(player)) {
            return null;
        }
        BingoParticipant participant = game.getTeamManager().getPlayerAsParticipant(player);
        if (participant == null || participant.getTeam() == null || participant.getTeam().outOfTheGame)
            return null;

        return participant;
    }

    /**
     * update progress for given participant about the task given by the updateFunction, using the task's existing progress
     * When the update function returns true the task is considered completed and will be removed from the tracker.
     */
    private void updateProgressFromEvent(BingoParticipant participant, BiFunction<GameTask, TaskProgress, Boolean> updateFunction) {
        Set<GameTask> tasksToRemove = new HashSet<>();
        for (GameTask task : progressMap.keySet()) {
            for (TaskProgress progress : progressMap.get(task)) {
                if (!progress.participant.equals(participant)) {
                    continue;
                }

                if (updateFunction.apply(task, progress)) {
                    tasksToRemove.add(task);
                }
            }
        }

        tasksToRemove.forEach(progressMap::remove);
    }
}
