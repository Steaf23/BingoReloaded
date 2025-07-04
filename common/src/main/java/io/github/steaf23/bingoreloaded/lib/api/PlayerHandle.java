package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerHandle extends Audience {

	String playerName();
	Component displayName();
	UUID uniqueId();
	WorldHandle world();
	WorldPosition position();
	@NotNull WorldPosition respawnPoint();
	boolean hasPermission(String permission);
	int level();
	float exp();
	double health();
	int foodLevel();
	PlayerGamemode gamemode();
	int getStatisticValue(StatisticType type);
	int getStatisticValue(StatisticType type, EntityType entity);
	int getStatisticValue(StatisticType type, ItemType item);

	void teleport(WorldPosition pos);

	PlayerInventoryHandle inventory();
	void clearInventory();
	void openInventory(InventoryHandle handle);
	InventoryHandle enderChest();

	/**
	 * @param newSpawn new position.
	 * @param force true if setting the spawn point should ignore valid bed/respawn positions too.
	 */
	void setRespawnPoint(WorldPosition newSpawn, boolean force);
	void setLevel(int level);
	void setExp(float exp);
	void setFoodLevel(int foodLevel);
	void setHealth(double health);
	void setGamemode(PlayerGamemode gamemode);
	void setStatisticValue(StatisticType type, int value);
	void setStatisticValue(StatisticType type, EntityType entity, int value);
	void setStatisticValue(StatisticType type, ItemType item, int value);

	void clearAllEffects();
}
