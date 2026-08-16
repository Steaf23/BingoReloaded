package io.github.steaf23.bingoreloaded.gameloop.spawn.strategy;

import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.player.team.TeamContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record StaticSpawnStrategy(@NotNull GlobalPosition position) implements SpawnStrategy {

	@Override
	public List<SpawnSite> getSpawnSites(Context context, TeamContainer teams) {
		return List.of(new SpawnSite(
				context.world().highestBlockAt(position),
				teams.getAllOnlineParticipants()));
	}
}
