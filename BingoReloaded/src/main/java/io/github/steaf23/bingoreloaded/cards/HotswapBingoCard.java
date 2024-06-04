package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.event.BingoPlaySoundEvent;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gui.inventory.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import io.github.steaf23.bingoreloaded.tasks.tracker.TaskProgressTracker;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.*;
import java.util.function.Supplier;

public class HotswapBingoCard extends BingoCard
{
    private final int winningScore;
    private final GameTimer taskTimer;

    private final Random randomExpiryProvider;
    private final int minExpirationTime;
    private final int maxExpirationTime;
    private final int recoveryTimeSeconds;

    private final List<HotswapTaskHolder> taskHolders;
    private Supplier<BingoTask> bingoTaskGenerator;

    private final List<BingoTask> completedTasks;
    private final BingoCardData cardData;
    // Used to call the play sound event for expiring tasks
    private final BingoGame game;

    // List used to draw random tasks from
    private final List<TaskData> randomTasks;

    public HotswapBingoCard(MenuBoard menuBoard, CardSize size, BingoGame game, TaskProgressTracker progressTracker, int winningScore, ConfigData.HotswapConfig config) {
        super(new HotswapCardMenu(menuBoard, size, BingoTranslation.CARD_TITLE.translate()), size, progressTracker);
        this.taskTimer = game.getTimer();
        this.randomExpiryProvider = new Random();
        this.taskHolders = new ArrayList<>();
        this.completedTasks = new ArrayList<>();
        this.bingoTaskGenerator = () -> null;
        this.cardData = new BingoCardData();
        this.game = game;
        this.minExpirationTime = config.minimumExpiration();
        this.maxExpirationTime = config.maximumExpiration();
        this.recoveryTimeSeconds = config.recoveryTime();
        this.randomTasks = new ArrayList<>();
        taskTimer.addNotifier(this::updateTaskExpiration);

        boolean countdownEnabled = game.getSettings().enableCountdown();
        this.winningScore = countdownEnabled ? -1 : winningScore;

        String[] description = new String[]{};
        if (countdownEnabled) {
            description = BingoTranslation.INFO_HOTSWAP_COUNTDOWN.translate(String.valueOf(game.getSettings().countdownDuration())).split("\\n");
        } else {
            description = BingoTranslation.INFO_HOTSWAP_DESC.translate(String.valueOf(winningScore)).split("\\n");
        }
        menu.setInfo(BingoTranslation.INFO_HOTSWAP_NAME.translate(),
                description);
    }

    @Override
    public boolean hasBingo(BingoTeam team) {
        if (winningScore == -1) {
            return false;
        }
        return getCompleteCount(team) == winningScore;
    }

    // Lockout cards cannot be copied since it should be the same instance for every player.
    @Override
    public HotswapBingoCard copy() {
        return this;
    }

    /**
     * Due to the way hotswap works, it is not possible to include statistics/advancements for technical reasons
     * @param cardName
     * @param seed
     * @param withAdvancements
     * @param withStatistics
     */
    @Override
    public void generateCard(String cardName, int seed, boolean withAdvancements, boolean withStatistics) {
        super.generateCard(cardName, seed, withAdvancements, withStatistics);

        if (seed != 0) {
            randomExpiryProvider.setSeed(seed);
        }

        bingoTaskGenerator = () -> {
            if (randomTasks.isEmpty()) {
                randomTasks.addAll(cardData.getAllTasks(cardName, withStatistics, withAdvancements));
                Collections.shuffle(randomTasks, randomExpiryProvider);
            }
            if (randomTasks.isEmpty()) {
                return new BingoTask(new ItemTask(Material.DIRT, 1));
            }

            return new BingoTask(randomTasks.remove(randomTasks.size() - 1));
        };
    }

    @Override
    public void setTasks(List<BingoTask> tasks) {
        taskHolders.clear();
        for (BingoTask task : tasks) {
            int expirationTime = randomExpiryProvider.nextInt(minExpirationTime * 60, (maxExpirationTime * 60) + 1);
            taskHolders.add(new HotswapTaskHolder(task, expirationTime, recoveryTimeSeconds));
        }
        ((HotswapCardMenu)menu).updateTaskHolders(taskHolders);
    }

    @Override
    public void handleTaskCompleted(BingoParticipant player, BingoTask task, long timeSeconds) {
        completedTasks.add(task);
    }

    @Override
    public List<BingoTask> getTasks() {
        return taskHolders.stream().map(holder -> holder.task).toList();
    }

    public void updateTaskExpiration(long timeElapsed) {
        int idx = 0;
        int taskExpiredCount = 0;
        int taskRecoveredCount = 0;
        BingoTask lastExpiredTask = null;
        BingoTask lastRecoverdTask = null;
        for (HotswapTaskHolder holder : taskHolders) {
            holder.currentTime -= 1;
            if (!holder.isRecovering() && holder.task.isCompleted()) { // start recovering item when it's been completed
                holder.startRecovering();
                continue;
            }
            if (holder.currentTime <= 0) {
                if (holder.isRecovering()) {
                    taskRecoveredCount++;
                    // Recovery finished, replace task with a new one.
                    BingoTask newTask = bingoTaskGenerator.get();
                    if (newTask == null) {
                        Message.error("Cannot generate new task for hot-swap, (Please report!)");
                    }
                    lastRecoverdTask = newTask;
                    int expirationTime = randomExpiryProvider.nextInt(minExpirationTime, (maxExpirationTime + 1)) * 60;
                    taskHolders.set(idx, new HotswapTaskHolder(newTask, expirationTime, recoveryTimeSeconds));
                    progressTracker.startTrackingTask(newTask);
                } else {
                    taskExpiredCount++;
                    lastExpiredTask = holder.task;
                    holder.task.setVoided(true);
                    holder.startRecovering();
                    progressTracker.removeTask(holder.task);
                }
            }
            idx++;
        }

        if (taskExpiredCount > 0) {
            var event = new BingoPlaySoundEvent(game.getSession(), Sound.ITEM_FIRECHARGE_USE);
            Bukkit.getPluginManager().callEvent(event);

            if (taskExpiredCount == 1) {
                BingoTask taskToSend = lastExpiredTask;
                game.getActionBar().requestMessage(p ->
                                new ComponentBuilder().bold(true).append(taskToSend.data.getName()).append(new TextComponent(" Expired")).color(ChatColor.of("#e85e21")).build()
                        , 1, 3);
            }
            else {
                game.getActionBar().requestMessage(p -> new ComponentBuilder().bold(true).append("Multiple Tasks Expired").color(ChatColor.of("#e85e21")).build(), 1, 3);
            }
        }
        if (taskRecoveredCount > 0) {
            var event = new BingoPlaySoundEvent(game.getSession(), Sound.ENTITY_PLAYER_LEVELUP);
            Bukkit.getPluginManager().callEvent(event);

            if (taskRecoveredCount == 1) {
                BingoTask taskToSend = lastRecoverdTask;
                game.getActionBar().requestMessage(p ->
                                new ComponentBuilder().bold(true).append(taskToSend.data.getName()).append(new TextComponent(" Added")).color(ChatColor.of("#5cb1ff")).build()
                        , 2, 3);
            }
            else {
                game.getActionBar().requestMessage(p -> new ComponentBuilder().bold(true).append("Added Multiple New Tasks").color(ChatColor.of("#5cb1ff")).build(), 2, 3);
            }
        }
        ((HotswapCardMenu)menu).updateTaskHolders(taskHolders);
    }

    @Override
    public int getCompleteCount(BingoTeam team) {
        return (int) completedTasks.stream().filter(task -> task.getCompletedBy().isPresent() && task.getCompletedBy().get().getTeam().equals(team)).count();
    }
}
