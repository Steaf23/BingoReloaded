package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.MinecraftExtension;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerRespawnManager
{
    private final Map<UUID, DeadPlayer> deadPlayers;
    private final BukkitTask task;

    public PlayerRespawnManager(MinecraftExtension extension, int respawnPeriodSeconds) {
        this.deadPlayers = new HashMap<>();
        //TODO: Maybe only have the task running if there are dead players?
        this.task = Bukkit.getScheduler().runTaskTimer(extension, () -> {
            for (var p : new HashSet<>(deadPlayers.keySet())) {
                DeadPlayer player = deadPlayers.get(p);
                if (System.currentTimeMillis() > player.deathTime + respawnPeriodSeconds * 1000L) {
                    deadPlayers.remove(p);
                }
            }
        }, 0, BingoReloaded.ONE_SECOND);
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