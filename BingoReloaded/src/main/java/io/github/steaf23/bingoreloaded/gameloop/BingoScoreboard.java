package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import io.github.steaf23.easymenulib.scoreboard.SidebarHUD;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BingoScoreboard implements SessionMember
{
    private final Scoreboard teamBoard;
    private final SidebarHUD hud;
    private final Objective taskObjective;
    private final BingoSession session;
    private final boolean showPlayer;

    public BingoScoreboard(HUDRegistry registry, BingoSession session, boolean showPlayer)
    {
        this.session = session;
        this.showPlayer = showPlayer;
        this.teamBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.hud = new SidebarHUD(registry, "" + ChatColor.ITALIC + ChatColor.UNDERLINE + BingoTranslation.GAME_SCOREBOARD_TITLE.translate());

        this.taskObjective = teamBoard.registerNewObjective("item_count", Criteria.DUMMY, "item_count");

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
                    objective.getScore(t.getIdentifier()).setScore(t.card.getCompleteCount(t));
                }
            }
            updateVisual();
        });
    }

    public void updateVisual()
    {
        hud.clear();

        TeamManager teamManager = session.teamManager;

        boolean condensedDisplay = !showPlayer
                || teamManager.getTeamCount() + teamManager.getParticipants().size() > 13;

        hud.setText(0, " ");
        int lineIndex = 1;
        for (BingoTeam team : teamManager.getActiveTeams())
        {
            String teamScoreLine = "" + ChatColor.DARK_RED + "[" + team.getColoredName().toLegacyText() + ChatColor.DARK_RED + "]" +
                    ChatColor.WHITE + ": " + ChatColor.BOLD + taskObjective.getScore(team.getIdentifier()).getScore();
            hud.setText(lineIndex, teamScoreLine);
            lineIndex += 1;

            if (!condensedDisplay)
            {
                for (BingoParticipant player : team.getMembers())
                {
                    String playerLine = "" + ChatColor.GRAY + ChatColor.BOLD + " ┗ " + ChatColor.RESET + player.getDisplayName();
                    hud.setText(lineIndex, playerLine);
                    lineIndex += 1;
                }
            }
        }

        for (BingoParticipant p : teamManager.getParticipants())
        {
            if (p instanceof BingoPlayer bingoPlayer)
                bingoPlayer.sessionPlayer().ifPresent(hud::subscribePlayer);
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
                if (p instanceof BingoPlayer bingoPlayer)
                {
                    bingoPlayer.sessionPlayer().ifPresent(hud::unsubscribePlayer);
                }
            }

            updateTeamScores();
        });
    }

    public Scoreboard getTeamBoard()
    {
        return teamBoard;
    }

    public void handlePlayerJoin(final PlayerJoinedSessionWorldEvent event)
    {
        hud.subscribePlayer(event.getPlayer());
    }

    public void handlePlayerLeave(final PlayerLeftSessionWorldEvent event)
    {
        hud.unsubscribePlayer(event.getPlayer());
    }

    @Override
    public @Nullable BingoSession getSession() {
        return session;
    }

    @Override
    public void setup() {
        reset();
    }
}
