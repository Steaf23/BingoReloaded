package io.github.steaf23.bingoreloaded.lib.api.player;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.HytaleServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.PotionEffectInstance;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerHandleHytale implements PlayerHandle {

	private final PlayerRef playerRef;
	private final HytalePlayerAudience audience;

	public PlayerHandleHytale(PlayerRef playerRef) {
		this.playerRef = playerRef;
		audience = new HytalePlayerAudience(playerRef);
	}

	@Override
	public String playerName() {
		return playerFromInternal().getDisplayName();
	}

	@Override
	public Component displayName() {
		return Component.text(playerName());
	}

	@Override
	public UUID uniqueId() {
		return playerRef.getUuid();
	}

	@Override
	public WorldHandle world() {
		return HytaleServerSoftware.fromWorld(Universe.get().getWorld(playerRef.getWorldUuid()));
	}

	@Override
	public WorldPosition position() {
		return null;
	}

	@Override
	public @Nullable WorldPosition respawnPoint() {
		return null;
	}

	@Override
	public boolean hasPermission(String permission) {
		return true;
	}

	@Override
	public int level() {
		return 0;
	}

	@Override
	public float exp() {
		return 0;
	}

	@Override
	public double health() {
		return 0;
	}

	@Override
	public int foodLevel() {
		return 0;
	}

	@Override
	public PlayerGamemode gamemode() {
		return switch (playerFromInternal().getGameMode()) {
			case Adventure -> PlayerGamemode.SURVIVAL;
			case Creative -> PlayerGamemode.CREATIVE;
		};
	}

	@Override
	public int getStatisticValue(StatisticType type) {
		return 0;
	}

	@Override
	public int getStatisticValue(StatisticType type, EntityType entity) {
		return 0;
	}

	@Override
	public int getStatisticValue(StatisticType type, ItemType item) {
		return 0;
	}

	@Override
	public void teleportAsync(WorldPosition pos, @Nullable Consumer<Boolean> whenFinished) {

	}

	@Override
	public boolean teleportBlocking(WorldPosition pos) {
		return false;
	}

	@Override
	public PlayerInventoryHandle inventory() {
		return new PlayerInventoryHandleHytale(playerFromInternal().getInventory());
	}

	@Override
	public void clearInventory() {

	}

	@Override
	public void openInventory(InventoryHandle inventory) {

	}

	@Override
	public InventoryHandle enderChest() {
		return null;
	}

	@Override
	public void setRespawnPoint(WorldPosition newSpawn, boolean force) {

	}

	@Override
	public void setLevel(int level) {

	}

	@Override
	public void setExp(float exp) {

	}

	@Override
	public void setFoodLevel(int foodLevel) {

	}

	@Override
	public void setHealth(double health) {

	}

	@Override
	public void setGamemode(PlayerGamemode gamemode) {

	}

	@Override
	public void setStatisticValue(StatisticType type, int value) {

	}

	@Override
	public void setStatisticValue(StatisticType type, EntityType entity, int value) {

	}

	@Override
	public void setStatisticValue(StatisticType type, ItemType item, int value) {

	}

	@Override
	public void addEffect(PotionEffectInstance effect) {

	}

	@Override
	public void clearAllEffects() {

	}

	@Override
	public void removeAdvancementProgress(AdvancementHandle advancement) {

	}

	@Override
	public boolean hasCooldown(StackHandle stack) {
		return false;
	}

	@Override
	public boolean hasCooldownOnGroup(Key cooldownGroup) {
		return false;
	}

	@Override
	public void setCooldown(StackHandle stack, int cooldownTicks) {

	}

	@Override
	public void setCooldownOnGroup(Key cooldownGroup, int cooldownTicks) {

	}

	@Override
	public boolean isSneaking() {
		return false;
	}

	@Override
	public void closeInventory() {

	}

	@Override
	public void kick(@Nullable Component reason) {

	}

	@Override
	public void setWaypointColor(@Nullable TextColor color) {

	}

	@Override
	public @NotNull Iterable<? extends Audience> audiences() {
		return List.of(audience);
	}

	public Player playerFromInternal() {
		return playerRef.getReference().getStore().getComponent(playerRef.getReference(), Player.getComponentType());
	}
}
