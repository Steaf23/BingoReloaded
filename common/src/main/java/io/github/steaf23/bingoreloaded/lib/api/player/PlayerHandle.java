package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.api.ActionUser;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.PotionEffectInstance;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.statistics.StatisticHandle;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerHandle extends ForwardingAudience, ActionUser {

	PlatformServer server();

	String playerName();

	Component displayName();

	UUID uniqueId();

	WorldHandle world();

	GlobalPosition position();

	@Nullable GlobalPosition respawnPoint();

	boolean hasPermission(String permission);

	int level();

	float exp();

	double health();

	int foodLevel();

	PlayerGamemode gamemode();

	int getStatisticValue(StatisticHandle stat);

	void teleportAsync(GlobalPosition pos, @Nullable Consumer<Boolean> whenFinished);

	default void teleportAsync(GlobalPosition pos) {
		teleportAsync(pos, null);
	}

	/**
	 * Blocking teleport is way faster compared to teleportAsync if the chunk is already loaded, Else it is way slower.
	 *
	 * @return true when the teleport was successful.
	 */
	boolean teleportBlocking(GlobalPosition pos);

	void clearInventory();

	void tryOpenInventory(InventoryTemplate inventory);

	default InventoryTemplate enderChest() {
		return server().inventories().enderChest(this);
	}

	default InventoryTemplate inventory() {
		return server().inventories().playerInventory(this);
	}

	default void addItemsToInventory(StackHandle... stacks) {
		server().inventories().addItemToPlayerInventory(this, stacks);
	}

	/**
	 * @param newSpawn new position.
	 * @param force    true if setting the spawn point should ignore valid bed/respawn positions too.
	 */
	void setRespawnPoint(GlobalPosition newSpawn, boolean force);

	void setLevel(int level);

	void setExp(float exp);

	void setFoodLevel(int foodLevel);

	void setHealth(double health);

	void setGamemode(PlayerGamemode gamemode);

	void setStatisticValue(StatisticHandle stat, int value);

	void addEffect(PotionEffectInstance effect);

	void clearAllEffects();

	void removeAdvancementProgress(AdvancementHandle advancement);

	boolean equals(Object other);

	boolean hasCooldown(StackHandle stack);

	boolean hasCooldownOnGroup(Key cooldownGroup);

	void setCooldown(StackHandle stack, int cooldownTicks);

	void setCooldownOnGroup(Key cooldownGroup, int cooldownTicks);

	boolean isSneaking();

	void closeInventory();

	void setWaypointColor(@Nullable TextColor color);
}
