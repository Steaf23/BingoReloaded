package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoParticipantJoinEvent;
import io.github.steaf23.bingoreloaded.event.BingoParticipantLeaveEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.InfoScoreboard;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class BingoScoreboard
{
    private final Scoreboard teamBoard;
    private final InfoScoreboard visualBoard;
    private final Objective taskObjective;
    private final BingoSession session;
    private final boolean showPlayer;

    public BingoScoreboard(BingoSession session, boolean showPlayer)
    {
        this.session = session;
        this.showPlayer = showPlayer;
        this.teamBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.visualBoard = new InfoScoreboard("" + ChatColor.ITALIC + ChatColor.UNDERLINE + BingoTranslation.SCOREBOARD_TITLE.translate(), teamBoard);

        this.taskObjective = teamBoard.registerNewObjective("item_count", "bingo_item_count");

        reset();
    }

    public void updateTeamScores()
    {
        if (!session.isRunning())
            return;

        BingoReloaded.scheduleTask(task ->
        {
            Objective objective = teamBoard.getObjective("item_count");
            if (objective == null)
                return;

            for (BingoTeam t : session.teamManager.getActiveTeams())
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

        TeamManager teamManager = session.teamManager;

        boolean condensedDisplay = !showPlayer
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
                for (BingoParticipant player : team.getMembers())
                {
                    String playerLine = "" + ChatColor.GRAY + ChatColor.BOLD + " ┗ " + ChatColor.RESET + player.getDisplayName();
                    visualBoard.setLineText(lineIndex, playerLine);
                    lineIndex += 1;
                }
            }
        }

        for (BingoParticipant p : teamManager.getParticipants())
        {
            if (p instanceof BingoPlayer bingoPlayer)
                updatePlayerScoreboard(bingoPlayer);
        }
    }

    public void reset()
    {
        BingoReloaded.scheduleTask(task -> {
            for (String entry : teamBoard.getEntries())
            {
                teamBoard.resetScores(entry);
            }

            for (BingoParticipant p : session.teamManager.getParticipants())
            {
                if (p.gamePlayer().isPresent())
                    visualBoard.clearPlayerBoard(p.gamePlayer().get());
            }

            updateTeamScores();
        });
    }

    public Scoreboard getTeamBoard()
    {
        return teamBoard;
    }

    public void handlePlayerJoin(final BingoParticipantJoinEvent event)
    {
        if (event.participant instanceof BingoPlayer player)
        {
            updatePlayerScoreboard(player);
        }
    }

    public void handlePlayerLeave(final BingoParticipantLeaveEvent event)
    {
        if (event.participant instanceof BingoPlayer player)
        {
            updatePlayerScoreboard(player);
        }
    }

    private void updatePlayerScoreboard(BingoPlayer player)
    {
        if (player.gamePlayer().isPresent())
        {
            if (player.getSession().isRunning())
                visualBoard.applyToPlayer(player.gamePlayer().get());
        }
        else if (player.asOnlinePlayer().isPresent() && player.asOnlinePlayer().get().getScoreboard().equals(teamBoard))
        {
            visualBoard.clearPlayerBoard(player.asOnlinePlayer().get());
        }
    }
}
