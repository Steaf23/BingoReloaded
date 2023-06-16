package io.github.steaf23.bingoreloaded.data.recoverydata;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.tasks.statistics.StatisticTracker;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;

public class RecoveryData {
    private BingoCard bingoCard;
    private GameTimer timer;
    private BingoSettings settings;
    private StatisticTracker statisticTracker;

    public RecoveryData(BingoCard bingoCard, GameTimer timer, BingoSettings settings, StatisticTracker statisticTracker) {
        this.bingoCard = bingoCard;
        this.timer = timer;
        this.settings = settings;
        this.statisticTracker = statisticTracker;
    }

    public BingoCard getBingoCard() {
        return bingoCard;
    }

    public GameTimer getTimer() {
        return timer;
    }

    public BingoSettings getSettings() {
        return settings;
    }

    public StatisticTracker getStatisticTracker() {
        return statisticTracker;
    }

    public boolean hasNull() {
        return bingoCard == null || timer == null || settings == null;
    }
}
