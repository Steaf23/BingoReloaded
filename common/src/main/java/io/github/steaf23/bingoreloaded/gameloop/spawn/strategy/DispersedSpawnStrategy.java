package io.github.steaf23.bingoreloaded.gameloop.spawn.strategy;

import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.TeamContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DispersedSpawnStrategy implements SpawnStrategy {

	@Override
	public List<SpawnSite> getSpawnSites(Context context, TeamContainer teams) {
		List<SpawnSite> result = new ArrayList<>();
		for (BingoParticipant participant : teams.getAllOnlineParticipants()) {
			result.add(new SpawnSite(
					context.randomPosWithinDistance(false),
					Set.of(participant)));
		}
		return result;
	}
}
