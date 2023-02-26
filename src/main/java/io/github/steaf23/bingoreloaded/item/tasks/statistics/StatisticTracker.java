package io.github.steaf23.bingoreloaded.item.tasks.statistics;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGameManager;
import io.github.steaf23.bingoreloaded.event.BingoStatisticCompletedEvent;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.item.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.item.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.*;

public class StatisticTracker
{
    private final List<StatisticProgress> statistics;
    private final String worldName;

    public StatisticTracker(String worldName)
    {
        this.statistics = new ArrayList<>();
        this.worldName = worldName;
    }

    public void start(Set<BingoTeam> teams)
    {
        for (BingoTeam team : teams)
        {
            for (BingoTask task : team.card.tasks)
            {
                if (task.type != BingoTask.TaskType.STATISTIC)
                    continue;

                StatisticTask statTask = (StatisticTask)task.data;

                for (BingoPlayer player : team.getPlayers())
                {
                    if (statistics.stream().anyMatch(progress ->
                            progress.player.equals(player) && progress.statistic.equals(statTask.statistic())))
                        continue;

                    statistics.add(new StatisticProgress(statTask.statistic(), player, statTask.count()));
                }
            }
        }
    }

    public double getProgressLeft(BingoPlayer player, BingoStatistic statistic)
    {
        List<StatisticProgress> statProgress = statistics.stream().filter(progress ->
                progress.player.equals(player) && progress.statistic.equals(statistic)).toList();

        if (statProgress.size() != 1)
            return Double.MAX_VALUE;

        return statProgress.get(0).progressLeft;
    }

    public void updateProgress()
    {
        statistics.forEach(StatisticProgress::updatePeriodicProgress);
        statistics.removeIf(progress -> progress.progressLeft <= 0);
    }

    public void reset()
    {
        statistics.clear();
    }

    public void handleStatisticIncrement(final PlayerStatisticIncrementEvent event)
    {
        BingoGame game = BingoGameManager.get().getActiveGame(worldName);
        if (game == null)
            return;

        BingoPlayer player = game.getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || !player.gamePlayer().isPresent())
            return;

        BingoTeam team = player.getTeam();
        if (team == null)
            return;

        BingoStatistic stat = new BingoStatistic(event.getStatistic(), event.getEntityType(), event.getMaterial());

        List<StatisticProgress> matchingStatistic = statistics.stream().filter(progress ->
                progress.player.equals(player) && progress.statistic.equals(stat)).toList();
        if (matchingStatistic.size() == 1)
        {
            matchingStatistic.get(0).setProgress(event.getNewValue());
        }

        statistics.removeIf(progress -> progress.progressLeft <= 0);
    }
}
