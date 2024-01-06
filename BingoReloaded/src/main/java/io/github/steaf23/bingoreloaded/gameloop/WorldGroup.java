package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.data.world.WorldFactory;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * A world group represents a group of 3 worlds, world_name, world_name_nether and world_name_the_end
 * These worlds are saved in the plugin's data folder under the name worlds
 */
public class WorldGroup
{
    private final WorldFactory worldData;
    private final String worldName;
    private UUID overworldId;
    private UUID netherId;
    private UUID endId;

    public WorldGroup(WorldFactory worldData, String worldName) {
        this.worldData = worldData;
        this.worldName = worldName;
    }

    public void create() {
        overworldId = worldData.createOverworld(worldName).getUID();
        netherId = worldData.createNether(worldName).getUID();
        endId = worldData.createTheEnd(worldName).getUID();
        Message.log("OW: " + overworldId.toString() + " " + worldName);
    }

    public void destroy() {
//        List<Player> allPlayers = new ArrayList<>();
//
//        World overworld = Bukkit.getWorld(worldName);
//        if (overworld != null) {
//            allPlayers.addAll(overworld.getPlayers());
//        }
//
//        for (Player p : allPlayers)
//        {
//
//        }
        //TODO: make sure all players have left, otherwise we cannot unload the world
        boolean removedOverworld = worldData.removeWorld(Bukkit.getWorld(overworldId));

        boolean removedNether = worldData.removeWorld(Bukkit.getWorld(netherId));
        boolean removedEnd = worldData.removeWorld(Bukkit.getWorld(endId));

        //TODO: when a world unloads successfully, remove it from the filesystem
//        Message.log("destroyed " + worldName + " nether: " + unloadedNether + "end: " + unloadedEnd);
    }

    public void teleportPlayer(Player player) {
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
