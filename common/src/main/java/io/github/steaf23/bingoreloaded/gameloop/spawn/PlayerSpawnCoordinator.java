package io.github.steaf23.bingoreloaded.gameloop.spawn;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.spawn.strategy.SpawnStrategy;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.world.BlockHelper;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.TeamContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerSpawnCoordinator {

	private final SpawnStrategy strategy;
	private final ServerSoftware server;
	private final int maxTeleportDistance;

	private List<SpawnStrategy.SpawnSite> sites = new ArrayList<>();

	public PlayerSpawnCoordinator(ServerSoftware server, SpawnStrategy strategy, int maxTeleportDistance) {
		this.strategy = strategy;
		this.server = server;
		this.maxTeleportDistance = maxTeleportDistance;
	}

	public void teleportPlayersToStart(BingoSession session, TeamContainer teams, int platformLifetime) {
		sites = strategy.getSpawnSites(new SpawnStrategy.Context(session, maxTeleportDistance, new Random()), teams);
		for (SpawnStrategy.SpawnSite site : sites) {
			site.players().forEach(p -> {
				teleportPlayerToStart(p, site.position(), 4);
			});
			BingoGame.spawnPlatform(site.position(), 5, true);
			server.runTask(platformLifetime, _ ->
					BingoGame.removePlatform(site.position(), 5));
		}
	}

	private void teleportPlayerToStart(BingoParticipant participant, WorldPosition to, int spread) {
		if (participant.sessionPlayer().isEmpty())
			return;
		PlayerHandle player = participant.sessionPlayer().get();

		WorldPosition playerLocation = BlockHelper.getRandomPosWithinRange(to, spread, spread);
		playerLocation.moveYBlocks(5);
		player.teleportAsync(playerLocation);

		WorldPosition spawnLocation = to.clone().moveYBlocks(2);
		player.setRespawnPoint(spawnLocation, true);
//		playerSpawnPoints.put(player.uniqueId(), spawnLocation);
	}
}
