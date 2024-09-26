package io.github.steaf23.bingoreloaded.tasks.tracker;

import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.ArrayList;
import java.util.List;

public class StatisticTracker
{
    private final List<StatisticProgress> statistics;

    public StatisticTracker()
    {
        this.statistics = new ArrayList<>();
    }

    public double getProgressLeft(BingoPlayer player, BingoStatistic statistic)
    {
        List<StatisticProgress> statProgress = statistics.stream().filter(progress ->
                progress.getParticipant().equals(player) && progress.getStatistic().equals(statistic)).toList();

        if (statProgress.size() != 1)
            return Double.MAX_VALUE;

        return statProgress.getFirst().getProgressLeft();
    }

    public void addStatistic(StatisticTask statTask, BingoParticipant participant) {
        if (statistics.stream().anyMatch(progress ->
                progress.getParticipant().equals(participant) && progress.getStatistic().equals(statTask.statistic())))
            return;

        setPlayerStatistic(statTask.statistic(), participant, 0);
        statistics.add(new StatisticProgress(statTask.statistic(), participant, statTask.count()));
    }

    public void removeStatistic(StatisticTask task) {
        statistics.removeIf(progress -> progress.getStatistic().equals(task.statistic()));
    }

    public void updateProgress()
    {
        statistics.forEach(StatisticProgress::updatePeriodicProgress);
        statistics.removeIf(StatisticProgress::done);
    }

    public void reset()
    {
        statistics.clear();
    }

    public void handleStatisticIncrement(final PlayerStatisticIncrementEvent event, final BingoGame game)
    {
        if (game == null)
            return;

        BingoParticipant player = game.getTeamManager().getPlayerAsParticipant(event.getPlayer());
        if (player == null || player.sessionPlayer().isEmpty())
            return;

        BingoTeam team = player.getTeam();
        if (team == null)
            return;

        BingoStatistic stat = new BingoStatistic(event.getStatistic(), event.getEntityType(), event.getMaterial());

        List<StatisticProgress> matchingStatistic = statistics.stream().filter(progress ->
                progress.getParticipant().equals(player) && progress.getStatistic().equals(stat)).toList();
        if (matchingStatistic.size() == 1)
        {
            matchingStatistic.getFirst().setProgress(event.getNewValue());
        }

        statistics.removeIf(StatisticProgress::done);
    }

    public void setPlayerStatistic(BingoStatistic statistic, BingoParticipant player, int value)
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
}
