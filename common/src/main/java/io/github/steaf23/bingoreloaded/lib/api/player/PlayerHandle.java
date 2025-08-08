package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.PotionEffectInstance;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerHandle extends ForwardingAudience, ActionUser {

	String playerName();

	Component displayName();

	UUID uniqueId();

	WorldHandle world();

	WorldPosition position();

	@Nullable WorldPosition respawnPoint();

	boolean hasPermission(String permission);

	int level();

	float exp();

	double health();

	int foodLevel();

	PlayerGamemode gamemode();

	int getStatisticValue(StatisticType type);

	int getStatisticValue(StatisticType type, EntityType entity);

	int getStatisticValue(StatisticType type, ItemType item);

	void teleportAsync(WorldPosition pos, @Nullable Consumer<Boolean> whenFinished);

	default void teleportAsync(WorldPosition pos) {
		teleportAsync(pos, null);
	}

	/**
	 * Blocking teleport is way faster compared to teleportAsync if the chunk is already loaded, Else it is way slower.
	 *
	 * @return true when the teleport was successful.
	 */
	boolean teleportBlocking(WorldPosition pos);

	PlayerInventoryHandle inventory();

	void clearInventory();

	void openInventory(InventoryHandle inventory);

	InventoryHandle enderChest();

	/**
	 * @param newSpawn new position.
	 * @param force    true if setting the spawn point should ignore valid bed/respawn positions too.
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

	void addEffect(PotionEffectInstance effect);

	void clearAllEffects();

	void removeAdvancementProgress(AdvancementHandle advancement);

	boolean equals(Object other);

	boolean hasCooldown(StackHandle stack);

	void setCooldown(StackHandle stack, int cooldownTicks);

	boolean isSneaking();

	void closeInventory();
}
