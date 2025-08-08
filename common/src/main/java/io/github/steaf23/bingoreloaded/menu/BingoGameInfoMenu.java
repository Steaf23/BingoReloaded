package io.github.steaf23.bingoreloaded.menu;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.menu.InfoMenu;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.SoloTeamManager;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingoGameInfoMenu extends InfoMenu {

	// map of team IDs and their scores
	private final Map<String, Integer> teamScores;
	private final BingoSession session;

	private static final Component PLAYER_PREFIX = ComponentUtils.MINI_BUILDER.deserialize("<gray><bold> ┗ </bold></gray><white>");

	public BingoGameInfoMenu(BingoSession session) {
		this.session = session;
		this.teamScores = new HashMap<>();
	}

	public void updateWinScore(BingoSettings settings) {
		String goal = "-";

		switch (settings.mode()) {
			case HOTSWAP -> {
				if (!settings.useScoreAsWinCondition()) {
					break;
				}

				goal = Integer.toString(settings.hotswapGoal());
			}
			case COMPLETE -> {
				if (!settings.useScoreAsWinCondition()) {
					break;
				}

				goal = Integer.toString(settings.completeGoal());
			}
			case REGULAR -> goal = "-----";
			case LOCKOUT -> goal = Integer.toString(settings.size().fullCardSize);
		}
		addField("win_goal", Component.text(goal));
	}

	public void updateTeamScores() {
		if (!session.isRunning())
			return;

		for (BingoTeam t : session.teamManager.getActiveTeams()) {
			teamScores.put(t.getIdentifier(), t.getCompleteCount());
		}

		List<Component> teamInfo = new ArrayList<>();
		List<Component> teamInfoCondensed = new ArrayList<>();

		TeamManager teamManager = session.teamManager;

		teamManager.getActiveTeams().getTeams().stream()
				.sorted(Comparator.comparingInt(BingoTeam::getCompleteCount).reversed())
				.forEach(team -> {
					Component teamScore;
					// In the case of the solo teams, we want to show the player names instead of the team prefix (since that is just an icon)
					if (teamManager instanceof SoloTeamManager) {
						teamScore = team.getColoredName();
					} else {
						teamScore = team.getPrefix();
					}
					teamScore = teamScore.append(Component.text(": ", NamedTextColor.WHITE)
							.append(Component.text(teamScores.get(team.getIdentifier())).decorate(TextDecoration.BOLD)));

					teamInfo.add(teamScore);
					teamInfoCondensed.add(teamScore);

					for (BingoParticipant player : team.getMembers()) {
						teamInfo.add(PLAYER_PREFIX.append(player.getDisplayName()));
					}
				});

		addField("team_info", teamInfo.toArray(Component[]::new));
		addField("team_info_condensed", teamInfoCondensed.toArray(Component[]::new));
	}

	public void setup(BingoSettings settings) {
		this.teamScores.clear();
		updateTeamScores();
		updateWinScore(settings);
	}
}
