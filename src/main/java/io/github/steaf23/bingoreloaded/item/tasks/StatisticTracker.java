package io.github.steaf23.bingoreloaded.item.tasks;

import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.Statistic;

import java.util.*;

public class StatisticTracker
{
    private Map<BingoPlayer, Map<BingoStatistic, Integer>> playerStatistics;
    private final String worldName;

    public StatisticTracker(String worldName)
    {
        this.playerStatistics = new HashMap<>();
        this.worldName = worldName;
    }

    public void initialize(Set<BingoTeam> teams)
    {
        for (BingoTeam team : teams)
        {
            for (BingoPlayer player : team.players)
            {
                playerStatistics.put(player, new HashMap<>());
            }
        }
    }

    public int getProgress(BingoPlayer player, BingoStatistic statistic)
    {
        if (playerStatistics.get(player).containsKey(statistic))
        {
            return playerStatistics.get(player).get(statistic);
        }

        return Integer.MAX_VALUE;
    }

    public void updateProgress()
    {
        playerStatistics.forEach((player, statList) ->
        {
            statList.forEach( (stat, progress) ->
            {
                int current = getPlayerStatistic(player, stat);
                int newProgress = current - previous;
            });
        });
    }

    public void reset()
    {
        playerStatistics.clear();
    }

    private int getPlayerStatistic(BingoPlayer player, BingoStatistic statistic)
    {
        return 0;
    }
}
