package io.github.steaf23.bingoreloaded.data.core.helper;

import io.github.steaf23.bingoreloaded.data.core.node.BranchNode;
import io.github.steaf23.bingoreloaded.data.core.node.NodeBuilder;
import io.github.steaf23.bingoreloaded.data.core.node.datatype.NodeDataType;
import io.github.steaf23.bingoreloaded.data.core.node.NodeSerializer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

@SerializableAs("Player")
public class SerializablePlayer implements NodeSerializer
{
    // Generated this data in plugin version x
    public String pluginVersion;
    public UUID playerId;
    public Location location;
    public double health;
    public int hunger;
    public GameMode gamemode;
    public Location spawnPoint;
    public int xpLevel;
    public float xpPoints;
    public ItemStack[] inventory;
    public ItemStack[] enderInventory;

    public static SerializablePlayer fromPlayer(@NotNull JavaPlugin plugin, @NotNull Player player)
    {
        SerializablePlayer data = new SerializablePlayer();
        data.pluginVersion = plugin.getPluginMeta().getVersion();
        data.playerId = player.getUniqueId();
        data.location = player.getLocation();
        data.health = player.getHealth();
        data.hunger = player.getFoodLevel();
        data.gamemode = player.getGameMode();
        data.spawnPoint = player.getRespawnLocation();
        data.xpLevel = player.getLevel();
        data.xpPoints = player.getExp();
        data.inventory = player.getInventory().getContents();
        data.enderInventory = player.getEnderChest().getContents();
        return data;
    }

    /**
     * Reset all player data and set location
     */
    public static SerializablePlayer reset(JavaPlugin plugin, Player player, Location location)
    {
        SerializablePlayer data = new SerializablePlayer();
        data.pluginVersion = plugin.getPluginMeta().getVersion();
        data.location = location;
        data.playerId = player.getUniqueId();
        data.health = 20.0;
        data.hunger = 20;
        data.gamemode = player.getGameMode();
        data.spawnPoint = null;
        data.xpLevel = 0;
        data.xpPoints = 0.0f;
        data.inventory = new ItemStack[player.getInventory().getSize()];
        data.enderInventory = new ItemStack[player.getEnderChest().getSize()];
        return data;
    }

    private SerializablePlayer() {
    }

    public void apply(Player player)
    {
        if (!playerId.equals(player.getUniqueId()))
            return;

        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

        player.setHealth(health);
        player.setFoodLevel(hunger);
        player.setGameMode(gamemode);
        player.setRespawnLocation(spawnPoint, true);
        player.setLevel(xpLevel);
        player.setExp(xpPoints);

        player.getInventory().clear();
        if (inventory != null)
        {
            player.getInventory().setContents(inventory);
        }
        player.getEnderChest().clear();
        if (enderInventory != null)
        {
            player.getEnderChest().setContents(enderInventory);
        }
        player.updateInventory();
    }

    public SerializablePlayer(BranchNode node) {
        pluginVersion = node.getString("version", "-");
        playerId = node.getUUID("uuid");
        location = node.getLocation("location", new Location(null, 0.0, 0.0, 0.0));
        health = node.getDouble("health");
        hunger = node.getInt("hunger");
        gamemode = GameMode.valueOf(node.getString("gamemode", "SURVIVAL"));
        spawnPoint = node.getLocation("location", new Location(null, 0.0, 0.0, 0.0));
        xpLevel = node.getInt("xp_level", 0);
        xpPoints = (float)node.getDouble("xp_points", 0.0D);
        inventory = node.getList("inventory", NodeDataType.ITEM_STACK).toArray(new ItemStack[]{});
        enderInventory = node.getList("ender_inventory", NodeDataType.ITEM_STACK).toArray(new ItemStack[]{});
    }


    @Override
    public BranchNode toNode() {
        return new NodeBuilder()
                .withString("version", pluginVersion)
                .withUUID("uuid", playerId)
                .withLocation("location", location)
                .withDouble("health", health)
                .withInt("hunger", hunger)
                .withString("gamemode", gamemode.toString())
                .withLocation("spawn_point", spawnPoint)
                .withInt("xp_level", xpLevel)
                .withDouble("xp_points", xpPoints)
                .withList("inventory", NodeDataType.ITEM_STACK, Arrays.stream(inventory).toList())
                .withList("ender_inventory", NodeDataType.ITEM_STACK, Arrays.stream(enderInventory).toList())
                .getNode();
    }
}
