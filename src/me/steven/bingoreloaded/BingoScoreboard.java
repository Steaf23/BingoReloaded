package me.steven.bingoreloaded;

import me.steven.bingoreloaded.player.BingoTeam;
import me.steven.bingoreloaded.player.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class BingoScoreboard
{
    //TODO: make a list of all the boards, swap them around, and just present the first one ;)

    private final List<Scoreboard> boards;
    private final Scoreboard itemCountBoard;
    private final Scoreboard gameTimeBoard;
    private final TeamManager teamManager;

    public BingoScoreboard(BingoGame game)
    {
        this.boards = new ArrayList<>();
        this.itemCountBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.gameTimeBoard = Bukkit.getScoreboardManager().getNewScoreboard();

        this.boards.add(itemCountBoard);
        this.boards.add(gameTimeBoard);

        this.teamManager = new TeamManager(game, itemCountBoard);

        Objective timeObjective = gameTimeBoard.registerNewObjective("time", "time_since_start", "Game Time");
        timeObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Objective itemObjective = itemCountBoard.registerNewObjective("item_count", "bingo_item_count", "Collected Items");
        itemObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        new BukkitRunnable() {
            @Override
            public void run()
            {
                if (game.isGameInProgress())
                {
                    switchBoards();
                }
            }
        }.runTaskTimer(BingoReloaded.getPlugin(BingoReloaded.class), 0, 20 * 5);
    }

    public void updateItemCount()
    {
        Objective objective = itemCountBoard.getObjective("item_count");
        if (objective == null)
            return;

        for (BingoTeam t : teamManager.getActiveTeams())
        {
            Score score = objective.getScore(t.getColor() + t.getName());
            score.setScore(t.card.getCompleteCount(t));
        }

        for (Player p : teamManager.getParticipants())
        {
            setPlayerBoard(p);
        }
    }

    public void updateGameTime(GameTimer timer)
    {
        Objective objective = gameTimeBoard.getObjective("time");
        if (objective == null)
            return;

        gameTimeBoard.resetScores(ChatColor.AQUA + "Time: " + ChatColor.WHITE + GameTimer.getTimeAsString(timer.getTime()));
        Score score = objective.getScore(ChatColor.AQUA + "Time: " + ChatColor.WHITE + GameTimer.getTimeAsString(timer.getTime() + 1));
        score.setScore(-1);
        for (Player player : teamManager.getParticipants())
        {
            setPlayerBoard(player);
        }
    }

    public void resetBoards()
    {
        for (String entry : itemCountBoard.getEntries())
        {
            itemCountBoard.resetScores(entry);
        }

        for (String entry : gameTimeBoard.getEntries())
        {
            gameTimeBoard.resetScores(entry);
        }
    }

    private void setPlayerBoard(Player player)
    {
        player.setScoreboard(boards.get(0));
    }

    public TeamManager getTeamManager()
    {
        return teamManager;
    }

    private void switchBoards()
    {
        Scoreboard tmp = boards.get(0);
        boards.set(0, boards.get(1));
        boards.set(1, tmp);
    }
}
