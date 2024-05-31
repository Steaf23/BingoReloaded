package io.github.steaf23.bingoreloaded.tasks.tracker;

import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.BingoTeamContainer;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticTracker
{
    private final List<StatisticProgress> statistics;

    public StatisticTracker()
    {
        this.statistics = new ArrayList<>();
    }

    public void start(BingoTeamContainer teams)
    {
        for (BingoTeam team : teams)
        {
            for (BingoTask task : team.card.getTasks())
            {
                if (task.type != BingoTask.TaskType.STATISTIC)
                    continue;

                StatisticTask statTask = (StatisticTask)task.data;

                for (BingoParticipant participant : team.getMembers())
                {
                    addStatistic(statTask, participant);
                }
            }
        }
    }

    public double getProgressLeft(BingoPlayer player, BingoStatistic statistic)
    {
        List<StatisticProgress> statProgress = statistics.stream().filter(progress ->
                progress.getPlayerId().equals(player) && progress.getStatistic().equals(statistic)).collect(Collectors.toList());

        if (statProgress.size() != 1)
            return Double.MAX_VALUE;

        return statProgress.get(0).getProgressLeft();
    }

    public void addStatistic(StatisticTask statTask, BingoParticipant participant) {
        if (statistics.stream().anyMatch(progress ->
                progress.getPlayerId().equals(participant) && progress.getStatistic().equals(statTask.statistic())))
            return;

        statistics.add(new StatisticProgress(statTask.statistic(), participant.getId(), statTask.count()));
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
        if (player == null || !player.sessionPlayer().isPresent())
            return;

        BingoTeam team = player.getTeam();
        if (team == null)
            return;

        BingoStatistic stat = new BingoStatistic(event.getStatistic(), event.getEntityType(), event.getMaterial());

        List<StatisticProgress> matchingStatistic = statistics.stream().filter(progress ->
                progress.getPlayerId().equals(player) && progress.getStatistic().equals(stat)).collect(Collectors.toList());
        if (matchingStatistic.size() == 1)
        {
            matchingStatistic.get(0).setProgress(event.getNewValue());
        }

        statistics.removeIf(StatisticProgress::done);
    }
}
