package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.menu.MenuBoard;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HotswapBingoCard extends BingoCard
{
    private final int winningScore;
    private final GameTimer taskTimer;

    private final Random randomExpiryProvider;
    private final int minExpirationTime = 1;
    private final int maxExpirationTime = 1;
    private final Map<Integer, Integer> taskExpiration;

    public HotswapBingoCard(MenuBoard menuBoard, CardSize size) {
        this(menuBoard, size, -1);
    }

    public HotswapBingoCard(MenuBoard menuBoard, CardSize size, int winningScore) {
        super(new HotswapCardMenu(menuBoard, size, BingoTranslation.CARD_TITLE.translate()), size);
        this.winningScore = winningScore;
        this.taskTimer = new CounterTimer();
        this.randomExpiryProvider = new Random();
        this.taskExpiration = new HashMap<>();
        taskTimer.setNotifier(this::updateTaskExpiration);
        taskTimer.start();
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

    @Override
    public void generateCard(String cardName, int seed, boolean withAdvancements, boolean withStatistics) {
        super.generateCard(cardName, seed, withAdvancements, withStatistics);

        if (seed != 0) {
            randomExpiryProvider.setSeed(seed);
        }

        for (int i = 0; i < tasks.size(); i++) {
            this.taskExpiration.put(i, randomExpiryProvider.nextInt(minExpirationTime * 60, (maxExpirationTime + 1) * 60));
        }
    }

    public void updateTaskExpiration(long timeElapsed) {
        for (int i = 0; i < tasks.size(); i++) {
            BingoTask task = tasks.get(i);
            int current = taskExpiration.get(i);
            int newExpiration = current - 1;
            if (newExpiration == 0) {
                expireItem(i);
                continue;
            }
            taskExpiration.put(i, newExpiration);
        }

        ((HotswapCardMenu)menu).updateTasks(tasks);
        ((HotswapCardMenu)menu).updateTaskExpiration(taskExpiration);
    }

    public void expireItem(int slotIndex) {
        tasks.get(slotIndex).setVoided(true);
        this.taskExpiration.put(slotIndex, randomExpiryProvider.nextInt(minExpirationTime * 60, (maxExpirationTime + 1) * 60));
    }
}
