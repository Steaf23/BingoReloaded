package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PlayerRespawnManager
{
    private final Map<UUID, DeadPlayer> deadPlayers;
    private final BukkitTask task;

    public PlayerRespawnManager(Plugin plugin, int respawnPeriodSeconds) {
        this.deadPlayers = new HashMap<>();
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (var p : deadPlayers.keySet()) {
                DeadPlayer player = deadPlayers.get(p);
                if (System.currentTimeMillis() > player.deathTime + respawnPeriodSeconds * 1000) {
                    deadPlayers.remove(p);
                }
            }
        }, 0, BingoReloaded.ONE_SECOND);
    }

    private record DeadPlayer(Location deathLocation, Long deathTime)
    {
    }

    public void addPlayer(UUID playerId, Location deathLocation) {
        deadPlayers.put(playerId, new DeadPlayer(deathLocation, System.currentTimeMillis()));
    }

    public Optional<Location> removeDeadPlayer(UUID playerId) {
        DeadPlayer player = deadPlayers.remove(playerId);
        if (player == null) {
            return Optional.empty();
        }

        return Optional.of(player.deathLocation);
    }
}