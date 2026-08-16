package io.github.steaf23.bingoreloaded.data.helper;

import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.lib.item.SerializableItem;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SerializablePlayer {

	public static final DataStorageSerializer<SerializablePlayer> SERIALIZER = DataStorageSerializer.of(SerializablePlayer.class,
			(storage, value) -> {
				storage.setString("version", value.extensionVersion);
				storage.setUUID("uuid", value.playerId);
				storage.setGlobalPosition("location", value.location);
				storage.setDouble("health", value.health);
				storage.setInt("hunger", value.hunger);
				storage.setString("gamemode", value.gamemode.toString());
				if (value.spawnPoint != null) {
					storage.setGlobalPosition("spawn_point", value.spawnPoint);
				}
				storage.setInt("xp_level", value.xpLevel);
				storage.setFloat("xp_points", value.xpPoints);
				storage.setSerializableList("inventory", SerializableItem.SERIALIZER, serializeInventory(value.inventory));
				storage.setSerializableList("ender_inventory", SerializableItem.SERIALIZER, serializeInventory(value.enderInventory));
			}, storage -> {
				var player = new SerializablePlayer();
				player.extensionVersion = storage.getString("version", "-");
				player.playerId = storage.getUUID("uuid");
				player.location = storage.getGlobalPosition("location", new GlobalPosition(Key.key("overworld"), 0.0, 0.0, 0.0));
				player.health = storage.getDouble("health", 20.0);
				player.hunger = storage.getInt("hunger", 0);
				player.gamemode = PlayerGamemode.valueOf(storage.getString("gamemode", "SURVIVAL"));
				player.spawnPoint = storage.getGlobalPosition("location");
				player.xpLevel = storage.getInt("xp_level", 0);
				player.xpPoints = storage.getFloat("xp_points", 0.0f);
				List<SerializableItem> items = storage.getSerializableList("inventory", SerializableItem.SERIALIZER);
				player.inventory = deserializeInventory(items, items.size());
				List<SerializableItem> enderItems = storage.getSerializableList("ender_inventory", SerializableItem.SERIALIZER);
				player.enderInventory = deserializeInventory(enderItems, enderItems.size());
				return player;
			});

	public String extensionVersion;
	public UUID playerId;
	public GlobalPosition location;
	public double health;
	public int hunger;
	public PlayerGamemode gamemode;
	public @Nullable GlobalPosition spawnPoint;
	public int xpLevel;
	public float xpPoints;
	public StackHandle[] inventory;
	public StackHandle[] enderInventory;

	public static @NotNull SerializablePlayer fromPlayer(String extensionVersion, @NotNull PlayerHandle player) {
		SerializablePlayer data = new SerializablePlayer();
		data.extensionVersion = extensionVersion;
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
	public static SerializablePlayer reset(String extensionVersion, PlayerHandle player, GlobalPosition location) {
		SerializablePlayer data = new SerializablePlayer();
		data.extensionVersion = extensionVersion;
		data.location = location;
		data.playerId = player.uniqueId();
		data.health = 20.0;
		data.hunger = 20;
		data.gamemode = player.gamemode();
		data.spawnPoint = null;
		data.xpLevel = 0;
		data.xpPoints = 0.0f;
		data.inventory = new StackHandle[player.inventory().size()];
		data.enderInventory = new StackHandle[player.enderChest().size()];
		return data;
	}

	public SerializablePlayer() {
	}

	public void apply(PlayerHandle player) {
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
		if (inventory != null) {
			player.inventory().setContents(inventory);
		}

		InventoryTemplate template = player.enderChest();
        template.clearContents();
		if (enderInventory != null) {
			template.setContents(enderInventory);
		}
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
