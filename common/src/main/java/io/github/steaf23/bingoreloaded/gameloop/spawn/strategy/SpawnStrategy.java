package io.github.steaf23.bingoreloaded.gameloop.spawn.strategy;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.world.BlockBuilder;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.TeamContainer;

import java.util.List;
import java.util.Random;
import java.util.Set;

public interface SpawnStrategy {

	record Context(BingoSession session,
	               int maxSpawnDistance,
	               Random random) {

		public GlobalPosition randomPosWithinDistance(boolean allowOceans) {
			GlobalPosition randomPosition = BlockBuilder.getRandomPosWithinRange(new GlobalPosition(world(), 0.0D, 0.0D, 0.0D), maxSpawnDistance, maxSpawnDistance);
			GlobalPosition location = world().highestBlockAt(randomPosition);

			if (allowOceans) {
				return location;
			}

			//find a not-ocean biome to teleport to
			while (BingoGame.isOceanBiome(world().biomeAtPos(location))) {
				randomPosition = BlockBuilder.getRandomPosWithinRange(new GlobalPosition(world(), 0.0D, 0.0D, 0.0D), maxSpawnDistance, maxSpawnDistance);
				location = world().highestBlockAt(randomPosition);
			}

			return location;
		}

		public WorldHandle world() {
			return session.getOverworld();
		}
	}

	record SpawnSite(GlobalPosition position, Set<BingoParticipant> players) {

	}

	List<SpawnSite> getSpawnSites(Context context, TeamContainer teams);
}
