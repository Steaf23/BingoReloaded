package io.github.steaf23.bingoreloaded.tasks.statistics;

import io.github.steaf23.bingoreloaded.event.BingoStatisticCompletedEvent;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatisticProgress
{
    final BingoStatistic statistic;
    final BingoPlayer player;
    public int progressLeft;

    public int previousGlobalProgress;

    public StatisticProgress(BingoStatistic statistic, BingoPlayer player, int targetScore)
    {
        this.statistic = statistic;
        this.player = player;
        this.progressLeft = targetScore;
        if (statistic.getCategory() == BingoStatistic.StatisticCategory.TRAVEL)
        {
            progressLeft *= 1000;
        }

        this.previousGlobalProgress = 0;

        setPlayerTotalScore(0);
    }

    public StatisticProgress(BingoStatistic statistic, BingoPlayer player, int progressLeft, int previousGlobalProgress)
    {
        this.statistic = statistic;
        this.player = player;
        this.progressLeft = progressLeft;
        this.previousGlobalProgress = previousGlobalProgress;
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
        if (statistic.isStatisticProcessed())
            return;

        if (!player.sessionPlayer().isPresent())
            return;

        int newProgress = getPlayerTotalScore();

        setProgress(newProgress);
    }

    public void setProgress(int newProgress)
    {
        int progressDelta = newProgress - previousGlobalProgress;

        progressLeft -= Math.max(0, progressDelta);

        previousGlobalProgress = newProgress;

        if (done())
        {
            var event = new BingoStatisticCompletedEvent(statistic, player);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public int getPlayerTotalScore()
    {
        if (player.sessionPlayer().isEmpty())
            return 0;

        Player gamePlayer = player.sessionPlayer().get();

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

    public void setPlayerTotalScore(int value)
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

    public BingoPlayer getPlayer() {
        return player;
    }

    public BingoStatistic getStatistic() {
        return statistic;
    }
}
