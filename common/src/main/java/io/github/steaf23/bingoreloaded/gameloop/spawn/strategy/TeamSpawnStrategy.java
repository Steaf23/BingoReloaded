package io.github.steaf23.bingoreloaded.gameloop.spawn.strategy;

import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamContainer;

import java.util.ArrayList;
import java.util.List;

public class TeamSpawnStrategy implements SpawnStrategy {

	@Override
	public List<SpawnSite> getSpawnSites(Context context, TeamContainer teams) {
		List<SpawnSite> result = new ArrayList<>();
		for (BingoTeam team : teams) {
			result.add(new SpawnSite(
					context.randomPosWithinDistance(false),
					team.getMembers()));
		}
		return result;
	}
}
