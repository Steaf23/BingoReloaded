package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import io.github.steaf23.bingoreloaded.util.GameTimer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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

    // TODO: implement worldName for the scoreboard?
    public String worldName;

    public BingoScoreboard(String worldName)
    {
        this.itemCountBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.teamManager = new TeamManager(itemCountBoard, worldName);

        Objective itemObjective = itemCountBoard.registerNewObjective("item_count", "bingo_item_count", TranslationData.translate("menu.completed"));
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

    public void resetBoards()
    {
        for (String entry : itemCountBoard.getEntries())
        {
            itemCountBoard.resetScores(entry);
        }

        for (Player p : teamManager.getParticipants())
        {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public TeamManager getTeamManager()
    {
        return teamManager;
    }
}
