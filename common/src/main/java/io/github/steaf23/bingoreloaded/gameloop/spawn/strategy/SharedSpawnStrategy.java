package io.github.steaf23.bingoreloaded.gameloop.spawn.strategy;

import io.github.steaf23.bingoreloaded.player.team.TeamContainer;

import java.util.List;

public class SharedSpawnStrategy implements SpawnStrategy {

	@Override
	public List<SpawnSite> getSpawnSites(Context context, TeamContainer teams) {
		return List.of(new SpawnSite(
				context.randomPosWithinDistance(false),
				teams.getAllOnlineParticipants()));
	}
}
