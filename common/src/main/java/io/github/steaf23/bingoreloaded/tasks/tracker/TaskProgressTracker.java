package io.github.steaf23.bingoreloaded.tasks.tracker;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.event.EventResults;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.DebugLogger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import org.jetbrains.annotations.NotNull;
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

    private final ServerSoftware platform;
    private final BingoGame game;
    private final Map<GameTask, List<TaskProgress>> progressMap;
    private final StatisticTracker statisticTracker;

    public TaskProgressTracker(ServerSoftware platform, @NotNull BingoGame game) {
        this.platform = platform;
        this.game = game;
        this.progressMap = new HashMap<>();
        this.statisticTracker = new StatisticTracker();
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
                    player.removeAdvancementProgress(advancementTask.advancement());
                    DebugLogger.addLog("Revoking advancement " + advancementTask.advancement().key().value() + " for player " + player.playerName());
                });
            } else if (type == TaskData.TaskType.STATISTIC) {
                StatisticTask statisticTask = (StatisticTask) task.data;
                // travel statistics are counted * 1000
//                if (statisticTask.statistic().getCategory() == BingoStatistic.StatisticCategory.TRAVEL)
//                {
//                    finalCount *= 1000;
//                }

                // the stat tracker will reset progress to 0 for every statistic added.
                statisticTracker.addStatistic(statisticTask, participant, this::onBingoStatisticCompleted);
            }
            // No progress to reset for item tasks

            // add task to progress tracker
            progressMap.get(task).add(new TaskProgress(participant, finalCount));
        }
    }

    public void handlePlayerAdvancementDone(PlayerHandle player, AdvancementHandle advancement) {
        BingoParticipant participant = getValidParticipant(player);
        if (participant == null) {
            return;
        }

        if (game.getDeathMatchTask() != null)
            return;

        if (!advancement.key().value().startsWith("recipes"))
            DebugLogger.addLog("Advancement " + advancement.key().value() + " completed by " + player.playerName());

        updateProgressFromEvent(participant, (task, progress) -> {
            if (task.taskType() != TaskData.TaskType.ADVANCEMENT) {
                return false;
            }
            AdvancementTask data = (AdvancementTask) task.data;

            if (!data.advancement().key().equals(advancement.key())) {
                return false;
            }

            progress.addProgress(1);
            DebugLogger.addLog("Completed task " + advancement.key().value() + " completed by player " + participant.getName());
            return tryCompleteTask(task, progress);
        });
    }

    public void onBingoStatisticCompleted(final StatisticProgress completedStat) {
        BingoParticipant participant = getValidParticipant(completedStat.getParticipant());
        if (participant == null) {
            return;
        }

        if (game.getDeathMatchTask() != null)
            return;

        updateProgressFromEvent(participant, (task, progress) -> {
            if (task.taskType() != TaskData.TaskType.STATISTIC) {
                return false;
            }
            StatisticHandle statistic = completedStat.getStatistic();
            StatisticTask data = (StatisticTask) task.data;

            if (!data.statistic().equals(statistic)) {
                return false;
            }

            progress.setProgress(data.getRequiredAmount());
            return tryCompleteTask(task, progress);
        });
    }

    public void handlePlayerStatIncrement(PlayerHandle player, StatisticHandle statistic, int newValue) {

        BingoParticipant participant = game.getTeamManager().getPlayerAsParticipant(player);
        if (participant == null || participant.sessionPlayer().isEmpty())
            return;

        statisticTracker.handleStatisticIncrement(participant, statistic, newValue, game);
    }

    private StackHandle completeItemSlot(StackHandle item, BingoParticipant participant) {
        if (participant == null || participant.getTeam() == null) {
            return item;
        }

        if (participant.getTeam().outOfTheGame) {
            return item;
        }

        GameTask deathMatchTask = game.getDeathMatchTask();
        if (deathMatchTask != null) {
            if (item.type().equals(deathMatchTask.material())) {
                deathMatchTask.complete(participant, game.getGameTime());
                game.onDeathmatchTaskComplete(participant, deathMatchTask);
            }
            return item;
        }

        updateProgressFromEvent(participant, (task, progress) -> {
            if (task.taskType() != TaskData.TaskType.ITEM) {
                return false;
            }
            ItemTask data = (ItemTask) task.data;
            if (!data.itemType().equals(item.type()) || data.count() > item.amount()) {
                return false;
            }

            progress.setProgress(item.amount());
            if (!tryCompleteTask(task, progress)) {
                return false;
            }

            participant.sessionPlayer().ifPresent(player -> {
                if (game.getConfig().getOptionValue(BingoOptions.REMOVE_TASK_ITEMS)) {
                    item.setAmount(item.amount() - data.getRequiredAmount());
                }
            });

            return true;
        });
        return item;
    }

    public void handleInventoryClicked(PlayerHandle player, StackHandle itemOnCursor) {
        BingoParticipant participant = getValidParticipant(player);
        if (participant == null) {
            return;
        }

        //FIXME: REFACTOR check if this condition is not already covered by the condition below.
//        // player tries to grab item in inventory normally
//        if (event.getSlotType() == InventoryType.SlotType.RESULT && event.getClick() != ClickType.SHIFT_LEFT) {
//            BingoReloaded.scheduleTask(task -> {
//                ItemStack resultStack = player.getItemOnCursor();
//                completeItemSlot(resultStack, participant);
//            });
//            return;
//        }

        platform.runTask(task -> {
            // Other contents are updated, so we want to check the full inventory for task items..
            for (StackHandle stack : player.inventory().contents()) {
                if (stack != null) {
                    completeItemSlot(stack, participant);
                }
            }

            // Sometimes item changes are not recorded instantly for some reason, so double check if the cursor item can be completed as a task.
            completeItemSlot(itemOnCursor, participant);
        });
    }

    public EventResult<EventResults.PlayerPickupResult> handlePlayerPickupItem(PlayerHandle player, StackHandle stack, WorldPosition itemLocation) {

        BingoParticipant participant = getValidParticipant(player);
        if (participant == null) {
            return new EventResult<>(false, null);
        }

        int amount = stack.amount();
        stack = completeItemSlot(stack, participant);
        boolean cancel = false;
        boolean removeItem = false;
        if (amount != stack.amount()) {
            cancel = true;
            StackHandle resultStack = stack.clone();

            if (resultStack.type().isAir() || resultStack.amount() <= 0) {
                return EventResults.playerPickupResult(true, false, true, stack);
            }

            removeItem = true;

            platform.runTask(task -> {
                participant.sessionPlayer().ifPresent(p -> p.world().dropItem(resultStack, itemLocation));
            });
        }

        return EventResults.playerPickupResult(cancel, removeItem, false, null);
    }

    public void handlePlayerDroppedItem(PlayerHandle player, StackHandle stack) {
        BingoParticipant participant = getValidParticipant(player);
        if (participant == null) {
            return;
        }

        //FIXME: REFACTOR remove wrap
        StackWrapped wrapped = new StackWrapped(stack);
        platform.runTask(task -> {
            StackHandle internal = wrapped.stack();
            internal = completeItemSlot(stack, participant);
        });
    }

    private record StackWrapped(StackHandle stack) {
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
        if (player == null) {
            return false;
        }

        if (!(player.getSession().getPhase() instanceof BingoGame)) {
            return false;
        }

        if (!task.complete(player, game.getGameTime()))
            return false;

        if (player.getTeam() == null) {
            ConsoleMessenger.bug("Player " + player.getName() + " is not in a valid team!", this);
        }
        player.getTeam().getCard().ifPresent(card ->
                card.onTaskCompleted(player, task, game.getGameTime()));

        game.onBingoTaskCompleted(player, task);
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
