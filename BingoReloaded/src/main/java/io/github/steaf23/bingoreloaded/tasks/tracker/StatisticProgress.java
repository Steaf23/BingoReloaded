package io.github.steaf23.bingoreloaded.tasks.tracker;

import io.github.steaf23.bingoreloaded.event.BingoStatisticCompletedEvent;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatisticProgress
{
    private final BingoStatistic statistic;
    private final UUID playerId;
    private int progressLeft;

    private int previousGlobalProgress;

    public StatisticProgress(BingoStatistic statistic, UUID player, int targetScore)
    {
        this.statistic = statistic;
        this.playerId = player;
        this.progressLeft = targetScore;
        if (statistic.getCategory() == BingoStatistic.StatisticCategory.TRAVEL)
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
    public boolean updatePeriodicProgress()
    {
        if (statistic.getsUpdatedWithIncrementEvent())
            return false;

        int newProgress = getParticipantTotalScore();

        return setProgress(newProgress);
    }

    public boolean setProgress(int newProgress)
    {
        int progressDelta = newProgress - previousGlobalProgress;

        progressLeft -= Math.max(0, progressDelta);

        previousGlobalProgress = newProgress;

        return done();
    }

    public int getParticipantTotalScore()
    {
        Player gamePlayer = Bukkit.getPlayer(playerId);
        if (gamePlayer == null) {
            return 0;
        }

        int value = 0;
        if (statistic.hasMaterialComponent())
        {
            value = gamePlayer.getStatistic(statistic.stat(), statistic.materialType());
        }
        else if (statistic.hasEntityComponent())
        {
            value = gamePlayer.getStatistic(statistic.stat(), statistic.entityType());
        }
        else
        {
            value = gamePlayer.getStatistic(statistic.stat());
        }
        return value;
    }

    public BingoStatistic getStatistic() {
        return statistic;
    }

    public int getProgressLeft() {
        return progressLeft;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
