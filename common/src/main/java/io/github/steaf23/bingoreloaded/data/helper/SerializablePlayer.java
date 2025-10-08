package io.github.steaf23.bingoreloaded.data.helper;

import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SerializablePlayer
{
    public String extensionVersion;
    public UUID playerId;
    public WorldPosition location;
    public double health;
    public int hunger;
    public PlayerGamemode gamemode;
    public WorldPosition spawnPoint;
    public int xpLevel;
    public float xpPoints;
    public StackHandle[] inventory;
    public StackHandle[] enderInventory;

    public static @NotNull SerializablePlayer fromPlayer(@NotNull ServerSoftware platform, @NotNull PlayerHandle player)
    {
        SerializablePlayer data = new SerializablePlayer();
        data.extensionVersion = platform.getExtensionInfo().version();
        data.playerId = player.uniqueId();
        data.location = player.position();
        data.health = player.health();
        data.hunger = player.foodLevel();
        data.gamemode = player.gamemode();
        data.spawnPoint = player.respawnPoint();
        data.xpLevel = player.level();
        data.xpPoints = player.exp();
        data.inventory = player.inventory().contents();
        data.enderInventory = player.enderChest().contents();
        return data;
    }

    /**
     * Reset all player data and set location
     */
    public static SerializablePlayer reset(ServerSoftware platform, PlayerHandle player, WorldPosition location)
    {
        SerializablePlayer data = new SerializablePlayer();
        data.extensionVersion = platform.getExtensionInfo().version();
        data.location = location;
        data.playerId = player.uniqueId();
        data.health = 20.0;
        data.hunger = 20;
        data.gamemode = player.gamemode();
        data.spawnPoint = null;
        data.xpLevel = 0;
        data.xpPoints = 0.0f;
        data.inventory = new StackHandle[player.inventory().contents().length];
        data.enderInventory = new StackHandle[player.enderChest().contents().length];
        return data;
    }

    public SerializablePlayer() {
    }

    public void apply(PlayerHandle player)
    {
        if (!playerId.equals(player.uniqueId()))
            return;

        player.teleportBlocking(location);

        player.setHealth(health);
        player.setFoodLevel(hunger);
        player.setGamemode(gamemode);
        player.setRespawnPoint(spawnPoint, true);
        player.setLevel(xpLevel);
        player.setExp(xpPoints);

        player.clearInventory();
        if (inventory != null)
        {
            player.inventory().setContents(inventory);
        }
        player.enderChest().clearContents();
        if (enderInventory != null)
        {
            player.enderChest().setContents(enderInventory);
        }
    }
}
