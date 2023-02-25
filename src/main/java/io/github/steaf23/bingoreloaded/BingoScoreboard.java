package io.github.steaf23.bingoreloaded;


import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.event.BingoPlayerJoinEvent;
import io.github.steaf23.bingoreloaded.event.BingoPlayerLeaveEvent;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class BingoScoreboard
{
    private final Scoreboard teamBoard;
    private final InfoScoreboard visualBoard;
    private final TeamManager teamManager;
    private final Objective taskObjective;

    public String worldName;

    public BingoScoreboard(String worldName)
    {
        this.teamBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.visualBoard = new InfoScoreboard("" + ChatColor.ITALIC + ChatColor.UNDERLINE + TranslationData.translate("menu.completed"), teamBoard);

        this.teamManager = new TeamManager(teamBoard, worldName);
        this.worldName = worldName;

        this.taskObjective = teamBoard.registerNewObjective("item_count", "bingo_item_count");

        reset();
    }

    public void updateTeamScores()
    {
        if (!BingoGameManager.get().isGameWorldActive(worldName))
            return;

        BingoReloaded.scheduleTask(task ->
        {
            Objective objective = teamBoard.getObjective("item_count");
            if (objective == null)
                return;

            for (BingoTeam t : teamManager.getActiveTeams())
            {
                if (t.card != null)
                {
                    objective.getScore("" + t.getColor().chatColor).setScore(t.card.getCompleteCount(t));
                }
            }
            updateVisual();
        });
    }

    public void updateVisual()
    {
        visualBoard.clearDisplay();

        boolean condensedDisplay = !ConfigData.instance.showPlayerInScoreboard
                || teamManager.getActiveTeams().size() + teamManager.getParticipants().size() > 13;

        visualBoard.setLineText(0, " ");
        int lineIndex = 1;
        for (BingoTeam team : teamManager.getActiveTeams())
        {
            String teamScoreLine = "" + ChatColor.DARK_RED + "[" + team.getColoredName().asLegacyString() + ChatColor.DARK_RED + "]" +
                    ChatColor.WHITE + ": " + ChatColor.BOLD + taskObjective.getScore("" + team.getColor().chatColor).getScore();
            visualBoard.setLineText(lineIndex, teamScoreLine);
            lineIndex += 1;

            if (!condensedDisplay)
            {
                for (BingoPlayer player : team.getPlayers())
                {
                    String playerLine = "" + ChatColor.GRAY + ChatColor.BOLD + " ┗ " + ChatColor.RESET + player.displayName();
                    visualBoard.setLineText(lineIndex, playerLine);
                    lineIndex += 1;
                }
            }
        }

        for (BingoPlayer p : teamManager.getParticipants())
        {
            updatePlayerScoreboard(p);
        }
    }

    public void reset()
    {
        BingoReloaded.scheduleTask(task -> {
            for (String entry : teamBoard.getEntries())
            {
                teamBoard.resetScores(entry);
            }

            for (BingoPlayer p : teamManager.getParticipants())
            {
                if (p.gamePlayer().isPresent())
                    visualBoard.clearPlayerBoard(p.gamePlayer().get());
            }

            updateTeamScores();
        });
    }

    public TeamManager getTeamManager()
    {
        return teamManager;
    }

    public void handlePlayerJoin(final BingoPlayerJoinEvent event)
    {
        Message.log("Player " + event.player.asOnlinePlayer().get().getDisplayName() + " joined the game", worldName);

        updatePlayerScoreboard(event.player);
    }

    public void handlePlayerLeave(final BingoPlayerLeaveEvent event)
    {
        Message.log("Player " + event.player.asOnlinePlayer().get().getDisplayName() + " left the game", worldName);

        updatePlayerScoreboard(event.player);
    }

    private void updatePlayerScoreboard(BingoPlayer player)
    {
        if (player.gamePlayer().isPresent())
        {
            visualBoard.updatePlayerBoard(player.gamePlayer().get());
        }
        else if (player.asOnlinePlayer().isPresent() && player.asOnlinePlayer().get().getScoreboard().equals(teamBoard))
        {
            visualBoard.clearPlayerBoard(player.asOnlinePlayer().get());
        }
    }
}
