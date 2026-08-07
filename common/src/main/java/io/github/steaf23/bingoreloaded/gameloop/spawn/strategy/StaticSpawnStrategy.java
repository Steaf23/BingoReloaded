package io.github.steaf23.bingoreloaded.gameloop.spawn.strategy;

import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.world.BlockHelper;
import io.github.steaf23.bingoreloaded.player.team.TeamContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record StaticSpawnStrategy(@NotNull WorldPosition position) implements SpawnStrategy {

	@Override
	public List<SpawnSite> getSpawnSites(Context context, TeamContainer teams) {
		return List.of(new SpawnSite(
				new WorldPosition(position.world(), position.x(), BlockHelper.getHighestBlockYAtPos(position), position.z()),
				teams.getAllOnlineParticipants()));
	}
}
