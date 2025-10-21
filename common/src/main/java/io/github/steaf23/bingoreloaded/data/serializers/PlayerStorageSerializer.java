package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerStorageSerializer implements DataStorageSerializer<SerializablePlayer>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull SerializablePlayer value) {
        storage.setString("version", value.extensionVersion);
        storage.setUUID("uuid", value.playerId);
        storage.setWorldPosition("location", value.location);
        storage.setDouble("health", value.health);
        storage.setInt("hunger", value.hunger);
        storage.setString("gamemode", value.gamemode.toString());
		if (value.spawnPoint != null) {
			storage.setWorldPosition("spawn_point", value.spawnPoint);
		}
        storage.setInt("xp_level", value.xpLevel);
        storage.setFloat("xp_points", value.xpPoints);
        storage.setSerializableList("inventory", SerializableItem.class, serializeInventory(value.inventory));
        storage.setSerializableList("ender_inventory", SerializableItem.class, serializeInventory(value.enderInventory));
    }

    @Override
    public SerializablePlayer fromDataStorage(@NotNull DataStorage storage) {
        var player = new SerializablePlayer();
        player.extensionVersion = storage.getString("version", "-");
        player.playerId = storage.getUUID("uuid");
        player.location = storage.getWorldPosition("location", new WorldPosition(null, 0.0, 0.0, 0.0));
        player.health = storage.getDouble("health", 20.0);
        player.hunger = storage.getInt("hunger", 0);
        player.gamemode = PlayerGamemode.valueOf(storage.getString("gamemode", "SURVIVAL"));
        player.spawnPoint = storage.getWorldPosition("location");
        player.xpLevel = storage.getInt("xp_level", 0);
        player.xpPoints = storage.getFloat("xp_points", 0.0f);
		List<SerializableItem> items = storage.getSerializableList("inventory", SerializableItem.class);
        player.inventory = deserializeInventory(items, items.size());
		List<SerializableItem> enderItems = storage.getSerializableList("ender_inventory", SerializableItem.class);
        player.enderInventory = deserializeInventory(enderItems, enderItems.size());
        return player;
    }

    private static List<SerializableItem> serializeInventory(StackHandle[] items) {
        List<SerializableItem> inventory = new ArrayList<>();
        int index = 0;
        for (StackHandle stack : items) {
            if (stack == null) {
                index++;
                continue;
            }
            inventory.add(new SerializableItem(index, stack));
            index++;
        }
        return inventory;
    }

    private static StackHandle[] deserializeInventory(List<SerializableItem> items, int size) {
        StackHandle[] inventory = new StackHandle[size];
        for (SerializableItem stack : items) {
            inventory[stack.slot()] = stack.stack();
        }
        return inventory;
    }
}
