package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class BingoScoreboard
{
    private final Scoreboard itemCountBoard;
    private final TeamManager teamManager;

    public String worldName;

    public BingoScoreboard(String worldName)
    {
        this.itemCountBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.teamManager = new TeamManager(itemCountBoard, worldName);
        this.worldName = worldName;

        Objective itemObjective = itemCountBoard.registerNewObjective("item_count", "bingo_item_count", TranslationData.translate("menu.completed"));
        itemObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        reset();
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
                objective.getScore("" + t.getColor().chatColor).setScore(t.card.getCompleteCount(t));
            }
        }

        for (BingoPlayer p : teamManager.getParticipants())
        {
            if (p.isInBingoWorld(worldName))
                p.player().setScoreboard(itemCountBoard);
        }
    }

    public void reset()
    {
        for (String entry : itemCountBoard.getEntries())
        {
            itemCountBoard.resetScores(entry);
        }

        for (BingoPlayer p : teamManager.getParticipants())
        {
            if (p.isInBingoWorld(worldName))
                p.player().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    public TeamManager getTeamManager()
    {
        return teamManager;
    }
}
