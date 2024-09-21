package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PlayerStorageSerializer implements DataStorageSerializer<SerializablePlayer>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, SerializablePlayer value) {
        storage.setString("version", value.pluginVersion);
        storage.setUUID("uuid", value.playerId);
        storage.setLocation("location", value.location);
        storage.setDouble("health", value.health);
        storage.setInt("hunger", value.hunger);
        storage.setString("gamemode", value.gamemode.toString());
        storage.setLocation("spawn_point", value.spawnPoint);
        storage.setInt("xp_level", value.xpLevel);
        storage.setFloat("xp_points", value.xpPoints);
        storage.setList("inventory", TagDataType.ITEM_STACK, Arrays.stream(value.inventory).toList());
        storage.setList("ender_inventory", TagDataType.ITEM_STACK, Arrays.stream(value.inventory).toList());
    }

    @Override
    public SerializablePlayer fromDataStorage(@NotNull DataStorage storage) {
        var player = new SerializablePlayer();
        player.pluginVersion = storage.getString("version", "-");
        player.playerId = storage.getUUID("uuid");
        player.location = storage.getLocation("location", new Location(null, 0.0, 0.0, 0.0));
        player.health = storage.getDouble("health", 20.0);
        player.hunger = storage.getInt("hunger", 0);
        player.gamemode = GameMode.valueOf(storage.getString("gamemode", "SURVIVAL"));
        player.spawnPoint = storage.getLocation("location", new Location(null, 0.0, 0.0, 0.0));
        player.xpLevel = storage.getInt("xp_level", 0);
        player.xpPoints = storage.getFloat("xp_points", 0.0f);
        player.inventory = storage.getList("inventory", TagDataType.ITEM_STACK).toArray(new ItemStack[]{});
        player.enderInventory = storage.getList("ender_inventory", TagDataType.ITEM_STACK).toArray(new ItemStack[]{});
        return player;
    }
}
