package io.github.steaf23.bingoreloaded.tasks.tracker;

import io.github.steaf23.bingoreloaded.lib.api.StatisticDefinition;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;

import java.util.function.Consumer;

public class StatisticProgress
{
    private final StatisticDefinition statistic;
    private final BingoParticipant player;
    private int progressLeft;

    private int previousGlobalProgress;

    private final Consumer<StatisticProgress> progressCompletedCallback;

    public StatisticProgress(StatisticDefinition statistic, BingoParticipant player, int targetScore, Consumer<StatisticProgress> progressCompletedCallback)
    {
        this.statistic = statistic;
        this.player = player;
        this.progressLeft = targetScore;
		this.progressCompletedCallback = progressCompletedCallback;
		if (statistic.type().getCategory() == StatisticType.StatisticCategory.TRAVEL)
        {
            progressLeft *= 1000;
        }

        this.previousGlobalProgress = 0;

//        setPlayerTotalScore(0);
    }

    public boolean done()
    {
        return progressLeft <= 0;
    }

    /**
     * Updates the progress for statistics that don't get updated with the default Increment event
     */
    public void updatePeriodicProgress()
    {
        if (statistic.type().getsUpdatedAutomatically())
            return;

        int newProgress = getParticipantTotalScore();
        setProgress(newProgress);
    }

    public void setProgress(int newProgress)
    {
        int progressDelta = newProgress - previousGlobalProgress;

        progressLeft -= Math.max(0, progressDelta);

        previousGlobalProgress = newProgress;

        if (done()) {
            progressCompletedCallback.accept(this);
        }
    }

    public int getParticipantTotalScore()
    {
        PlayerHandle gamePlayer = player.sessionPlayer().orElse(null);
        if (gamePlayer == null) {
            return 0;
        }

        int value;
        if (statistic.hasItemType())
        {
            value = gamePlayer.getStatisticValue(statistic.type(), statistic.itemType());
        }
        else if (statistic.hasEntity())
        {
            value = gamePlayer.getStatisticValue(statistic.type(), statistic.entityType());
        }
        else
        {
            value = gamePlayer.getStatisticValue(statistic.type());
        }
        return value;
    }

    public StatisticDefinition getStatistic() {
        return statistic;
    }

    public int getProgressLeft() {
        return progressLeft;
    }

    public BingoParticipant getParticipant() {
        return player;
    }
}
