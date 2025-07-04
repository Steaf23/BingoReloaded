package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.Extension;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerRespawnManager
{
    private final Map<UUID, DeadPlayer> deadPlayers;

	public PlayerRespawnManager(Extension extension, int respawnPeriodSeconds) {
        this.deadPlayers = new HashMap<>();
        //TODO: Maybe only have the task running if there are dead players?
		extension.runTaskTimer(0, BingoReloaded.ONE_SECOND, () -> {
			for (var p : new HashSet<>(deadPlayers.keySet())) {
				DeadPlayer player = deadPlayers.get(p);
				if (System.currentTimeMillis() > player.deathTime + respawnPeriodSeconds * 1000L) {
					deadPlayers.remove(p);
				}
			}
		});
	}

    private record DeadPlayer(WorldPosition deathLocation, Long deathTime)
    {
    }

    public void addPlayer(UUID playerId, WorldPosition deathLocation) {
        deadPlayers.put(playerId, new DeadPlayer(deathLocation, System.currentTimeMillis()));
    }

    public Optional<WorldPosition> removeDeadPlayer(UUID playerId) {
        DeadPlayer player = deadPlayers.remove(playerId);
        if (player == null) {
            return Optional.empty();
        }

        return Optional.of(player.deathLocation);
    }
}