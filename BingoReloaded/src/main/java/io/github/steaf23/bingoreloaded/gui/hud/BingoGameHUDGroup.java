package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.SoloTeamManager;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.placeholder.BingoPlaceholderFormatter;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUD;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUDGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BingoGameHUDGroup extends PlayerHUDGroup
{
    // map of team IDs and their scores
    private final Map<String, Integer> teamScores;
    private final BingoSession session;
    private final boolean showPlayerNames;
    private final ScoreboardData.SidebarTemplate template;

    private final BingoPlaceholderFormatter formatter;

    public BingoGameHUDGroup(HUDRegistry registry, BingoSession session, boolean showPlayerNames) {
        super(registry);

        this.session = session;
        this.teamScores = new HashMap<>();
        this.showPlayerNames = showPlayerNames;
        this.template = new ScoreboardData().loadTemplate("game", registeredFields);
        this.formatter = new BingoPlaceholderFormatter();
    }

    public void updateWinScore(BingoSettings settings) {
        String score = "-";
        switch (settings.mode()) {
            case HOTSWAP -> {
                if (settings.enableCountdown()) {
                    break;
                }

                score = Integer.toString(settings.hotswapGoal());
            }
            case REGULAR -> score = "-----";
            case COMPLETE, LOCKOUT -> score = Integer.toString(settings.size().fullCardSize);
        }
        registeredFields.put("win_goal", score);
        updateVisible();
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
                || teamManager.getTeamCount() + teamManager.getParticipantCount() > spaceLeft
                || teamManager instanceof SoloTeamManager;

        String format = formatter.getTeamFullFormat();
        teamManager.getActiveTeams().getTeams().stream()
                .sorted(Comparator.comparingInt(BingoTeam::getCompleteCount).reversed())
                .forEach(team -> {
                    String teamScoreLine = "" + BingoPlaceholderFormatter.createLegacyTextFromMessage(format, team.getColor().toString(), team.getName()) + ChatColor.RESET +
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

    public void setup(BingoSettings settings) {
        this.teamScores.clear();
        updateTeamScores();
        updateWinScore(settings);
    }

    @Override
    protected PlayerHUD createHUDForPlayer(Player player) {
        return new TemplatedPlayerHUD(player, "Team Score", template);
    }
}
