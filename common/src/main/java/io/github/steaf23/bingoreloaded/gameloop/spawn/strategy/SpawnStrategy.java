package io.github.steaf23.bingoreloaded.gameloop.spawn.strategy;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.world.BlockHelper;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.TeamContainer;

import java.util.List;
import java.util.Random;
import java.util.Set;

public interface SpawnStrategy {

	record Context(BingoSession session,
	               int maxSpawnDistance,
	               Random random) {

		public WorldPosition randomPosWithinDistance(boolean allowOceans) {
			WorldPosition randomPosition = BlockHelper.getRandomPosWithinRange(new WorldPosition(world(), 0.0D, 0.0D, 0.0D), maxSpawnDistance, maxSpawnDistance);
			WorldPosition location = new WorldPosition(world(), randomPosition.x(), BlockHelper.getHighestBlockYAtPos(randomPosition), randomPosition.z());

			if (allowOceans) {
				return location;
			}

			//find a not-ocean biome to teleport to
			while (BingoGame.isOceanBiome(world().biomeAtPos(location))) {
				randomPosition = BlockHelper.getRandomPosWithinRange(new WorldPosition(world(), 0.0D, 0.0D, 0.0D), maxSpawnDistance, maxSpawnDistance);
				location = new WorldPosition(world(), randomPosition.x(), BlockHelper.getHighestBlockYAtPos(randomPosition), randomPosition.z());
			}

			return location;
		}

		public WorldHandle world() {
			return session.getOverworld();
		}
	}

	record SpawnSite(WorldPosition position, Set<BingoParticipant> players) {

	}

	List<SpawnSite> getSpawnSites(Context context, TeamContainer teams);
}
