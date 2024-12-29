package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoReloaded;
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
        //TODO: Maybe only have the task running if there are dead players?
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                for (var p : deadPlayers.keySet()) {
                    DeadPlayer player = deadPlayers.get(p);
                    if (System.currentTimeMillis() > player.deathTime + respawnPeriodSeconds * 1000L) {
                        deadPlayers.remove(p);
                    }
                }
            } catch (ConcurrentModificationException e) {
                //why idk
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