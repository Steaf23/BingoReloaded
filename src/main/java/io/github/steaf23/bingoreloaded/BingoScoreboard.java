package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class BingoScoreboard
{
    private final Scoreboard itemCountBoard;
    private final TeamManager teamManager;

    public BingoScoreboard(BingoGame game)
    {
        this.itemCountBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.teamManager = new TeamManager(game, itemCountBoard);

        Objective itemObjective = itemCountBoard.registerNewObjective("item_count", "bingo_item_count", "Collected Items");
        itemObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        resetBoards();
    }

    public void updateItemCount()
    {
        Objective objective = itemCountBoard.getObjective("item_count");
        if (objective == null)
            return;

        for (BingoTeam t : teamManager.getActiveTeams())
        {
            if (t.card != null)
            {
                objective.getScore("" + t.getColor()).setScore(t.card.getCompleteCount(t));
            }
        }

        for (Player p : teamManager.getParticipants())
        {
            p.setScoreboard(itemCountBoard);
        }
    }

    public void updateGameTime(GameTimer timer)
    {
        for (Player player : teamManager.getParticipants())
        {
            BingoReloaded.showPlayerActionMessage("" + ChatColor.AQUA + ChatColor.BOLD + " Game Time: " +
                    ChatColor.WHITE + GameTimer.getTimeAsString(timer.getTime()), player);
        }
    }

    public void resetBoards()
    {
        for (String entry : itemCountBoard.getEntries())
        {
            itemCountBoard.resetScores(entry);
        }

        for (Player p : teamManager.getParticipants())
        {
            Message.log("Clearing scoreboard for " + p.getDisplayName());
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public TeamManager getTeamManager()
    {
        return teamManager;
    }
}
