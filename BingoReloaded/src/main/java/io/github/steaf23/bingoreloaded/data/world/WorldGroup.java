package io.github.steaf23.bingoreloaded.data.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A world group represents a group of 3 worlds, world_name, world_name_nether and world_name_the_end
 * These worlds are saved in the plugin's data folder under the name worlds
 */
public record WorldGroup(String worldName, UUID overworldId, UUID netherId, UUID endId)
{
    public void teleportPlayer(Player player) {
        player.teleport(Bukkit.getWorld(overworldId).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public @Nullable World getOverworld() {
        return overworldId == null ? null : Bukkit.getWorld(overworldId);
    }

    public @Nullable World getNetherWorld() {
        return netherId == null ? null : Bukkit.getWorld(netherId);
    }

    public @Nullable World getEndWorld() {
        return endId == null ? null : Bukkit.getWorld(endId);
    }

    public boolean hasWorld(UUID uuid) {
        return overworldId.equals(uuid) || netherId.equals(uuid) || endId.equals(uuid);
    }

    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        if (getOverworld() != null)
            players.addAll(getOverworld().getPlayers());
        if (getNetherWorld() != null)
            players.addAll(getNetherWorld().getPlayers());
        if (getEndWorld() != null)
            players.addAll(getEndWorld().getPlayers());
        return players;
    }
}
