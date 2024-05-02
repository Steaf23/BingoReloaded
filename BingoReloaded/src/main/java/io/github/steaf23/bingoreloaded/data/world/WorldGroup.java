package io.github.steaf23.bingoreloaded.data.world;

import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * A world group represents a group of 3 worlds, world_name, world_name_nether and world_name_the_end
 * These worlds are saved in the plugin's data folder under the name worlds
 */
public record WorldGroup(String worldName, UUID overworldId, UUID netherId, UUID endId)
{
    public void teleportPlayer(Player player) {
        Message.log("OW: " + getOverworld().getUID().toString(), worldName);
        player.teleport(Bukkit.getWorld(overworldId).getSpawnLocation());
    }

    public String getName() {
        return worldName;
    }

    public World getOverworld() {
        return overworldId == null ? null : Bukkit.getWorld(overworldId);
    }

    public World getNetherWorld() {
        return netherId == null ? null : Bukkit.getWorld(netherId);
    }

    public World getEndWorld() {
        return endId == null ? null : Bukkit.getWorld(endId);
    }

    public boolean hasWorld(UUID uuid) {
        return overworldId.equals(uuid) || netherId.equals(uuid) || endId.equals(uuid);
    }
}