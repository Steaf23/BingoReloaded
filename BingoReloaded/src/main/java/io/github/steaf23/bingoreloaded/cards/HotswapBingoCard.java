package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoPlaySoundEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.menu.MenuBoard;
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
    private final int minExpirationTime = 1;
    private final int maxExpirationTime = 1;
    private final int recoveryTimeSeconds = 5;

    private final List<HotswapTaskHolder> taskHolders;
    private Supplier<BingoTask> bingoTaskGenerator;

    private final List<BingoTask> completedTasks;
    private final BingoCardData cardData;
    // Used to call the play sound event for expiring tasks
    private final BingoSession session;

    public HotswapBingoCard(MenuBoard menuBoard, CardSize size, GameTimer timer, BingoSession session) {
        this(menuBoard, size, timer, session, -1);
    }

    public HotswapBingoCard(MenuBoard menuBoard, CardSize size, GameTimer timer, BingoSession session, int winningScore) {
        super(new HotswapCardMenu(menuBoard, size, BingoTranslation.CARD_TITLE.translate()), size);
        this.winningScore = winningScore;
        this.taskTimer = timer;
        this.randomExpiryProvider = new Random();
        this.taskHolders = new ArrayList<>();
        this.completedTasks = new ArrayList<>();
        this.bingoTaskGenerator = () -> null;
        this.cardData = new BingoCardData();
        this.session = session;
        taskTimer.addNotifier(this::updateTaskExpiration);
        menu.setInfo(BingoTranslation.INFO_HOTSWAP_NAME.translate(),
                BingoTranslation.INFO_HOTSWAP_DESC.translate(String.valueOf(winningScore)).split("\\n"));
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
        super.generateCard(cardName, seed, false, false);

        if (seed != 0) {
            randomExpiryProvider.setSeed(seed);
        }

        bingoTaskGenerator = () -> {
            return new BingoTask(cardData.getRandomItemTask(cardName, randomExpiryProvider));
        };
    }

    @Override
    public void setTasks(List<BingoTask> tasks) {
        taskHolders.clear();
        for (BingoTask task : tasks) {
            int expirationTime = randomExpiryProvider.nextInt(minExpirationTime, (maxExpirationTime + 1)) * 60;
            taskHolders.add(new HotswapTaskHolder(task, expirationTime, recoveryTimeSeconds));
        }
        ((HotswapCardMenu)menu).updateTaskHolders(taskHolders);
    }

    @Override
    public boolean tryCompleteTask(BingoParticipant player, BingoTask task, long timeSeconds) {
        boolean success = super.tryCompleteTask(player, task, timeSeconds);
        if (success) {
            completedTasks.add(task);
        }
        return success;
    }

    @Override
    public List<BingoTask> getTasks() {
        return taskHolders.stream().map(holder -> holder.task).toList();
    }

    public void updateTaskExpiration(long timeElapsed) {
        int idx = 0;
        boolean taskExpired = false;
        boolean taskRecovered = false;
        for (HotswapTaskHolder holder : taskHolders) {
            holder.currentTime -= 1;
            if (!holder.isRecovering() && holder.task.isCompleted()) { // start recovering item when it's been completed
                holder.startRecovering();
                continue;
            }
            if (holder.currentTime <= 0) {
                if (holder.isRecovering()) {
                    taskRecovered = true;
                    // Recovery finished, replace task with a new one.
                    BingoTask newTask = bingoTaskGenerator.get();
                    if (newTask == null) {
                        Message.error("Cannot generate new task for hot-swap, (Please report!)");
                    }
                    int expirationTime = randomExpiryProvider.nextInt(minExpirationTime, (maxExpirationTime + 1)) * 60;
                    taskHolders.set(idx, new HotswapTaskHolder(newTask, expirationTime, recoveryTimeSeconds));
                } else {
                    taskExpired = true;
                    holder.task.setVoided(true);
                    holder.startRecovering();
                }
            }
            idx++;
        }

        if (taskExpired) {
            var event = new BingoPlaySoundEvent(session, Sound.ITEM_FIRECHARGE_USE);
            Bukkit.getPluginManager().callEvent(event);
        }
        if (taskRecovered) {
            var event = new BingoPlaySoundEvent(session, Sound.ENTITY_PLAYER_LEVELUP);
            Bukkit.getPluginManager().callEvent(event);
        }
        ((HotswapCardMenu)menu).updateTaskHolders(taskHolders);
    }

    @Override
    public int getCompleteCount(BingoTeam team) {
        return (int) completedTasks.stream().filter(task -> task.getCompletedBy().isPresent() && task.getCompletedBy().get().getTeam().equals(team)).count();
    }
}
