package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.SessionMember;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUD;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUDGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BingoGameHUDGroup extends PlayerHUDGroup implements SessionMember
{
    // map of team IDs and their scores
    private final Map<String, Integer> teamScores;
    private final BingoSession session;
    private final boolean showPlayerNames;
    private final ScoreboardData.SidebarTemplate template;

    public BingoGameHUDGroup(HUDRegistry registry, BingoSession session, boolean showPlayerNames) {
        super(registry);

        this.session = session;
        this.teamScores = new HashMap<>();
        this.showPlayerNames = showPlayerNames;
        this.template = new ScoreboardData().loadTemplate("game", registeredFields);
    }

    public void updateTeamScores()
    {
        if (!session.isRunning())
            return;

        for (BingoTeam t : session.teamManager.getActiveTeams())
        {
            if (t.getCard() != null)
            {
                teamScores.put(t.getIdentifier(), t.getCard().getCompleteCount(t));
            }
        }

        StringBuilder teamInfoString = new StringBuilder();

        TeamManager teamManager = session.teamManager;

        // try to save space on the sidebar
        int spaceLeft = 15 - template.lines().length;
        boolean condensedDisplay = !showPlayerNames
                || teamManager.getTeamCount() + teamManager.getParticipantCount() > spaceLeft;

        teamManager.getActiveTeams().getTeams().stream()
                .sorted(Comparator.comparingInt(BingoTeam::getCompleteCount).reversed())
                .forEach(team -> {
                    String teamScoreLine = "" + team.getColoredName().toLegacyText() + ChatColor.RESET +
                            ChatColor.WHITE + ": " + ChatColor.BOLD + teamScores.get(team.getIdentifier());
                    teamInfoString.append(teamScoreLine);
                    teamInfoString.append("\n");

                    if (!condensedDisplay)
                    {
                        for (BingoParticipant player : team.getMembers())
                        {
                            String playerLine = "" + ChatColor.GRAY + ChatColor.BOLD + " ┗ " + ChatColor.RESET + player.getDisplayName();
                            teamInfoString.append(playerLine);
                            teamInfoString.append("\n");
                        }
                    }
                });

        registeredFields.put("team_info", teamInfoString.toString());
        updateVisible();
    }

    @Override
    public @Nullable BingoSession getSession() {
        return session;
    }

    @Override
    public void setup() {
        this.teamScores.clear();
        updateTeamScores();
    }

    @Override
    protected PlayerHUD createHUDForPlayer(Player player) {
        return new TemplatedPlayerHUD(player, "Team Score", template);
    }
}
