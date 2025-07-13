package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerHandlePaper implements PlayerHandle {

	private final Player player;

	public PlayerHandlePaper(Player player) {
		this.player = player;
	}

	@Override
	public String playerName() {
		return player.getName();
	}

	@Override
	public Component displayName() {
		return player.displayName();
	}

	@Override
	public UUID uniqueId() {
		return player.getUniqueId();
	}

	@Override
	public WorldHandle world() {
		return new WorldHandlePaper(player.getWorld());
	}

	@Override
	public WorldPosition position() {
		return PaperApiHelper.worldPosFromLocation(player.getLocation());
	}

	@Override
	public @Nullable WorldPosition respawnPoint() {
		return PaperApiHelper.worldPosFromLocation(player.getRespawnLocation());
	}

	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}

	@Override
	public int level() {
		return player.getLevel();
	}

	@Override
	public float exp() {
		return player.getExp();
	}

	@Override
	public double health() {
		return player.getHealth();
	}

	@Override
	public int foodLevel() {
		return player.getFoodLevel();
	}

	@Override
	public PlayerGamemode gamemode() {
		return toPlayerMode(player.getGameMode());
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
		var future = player.teleportAsync(PaperApiHelper.locationFromWorldPos(pos));
		if (whenFinished != null) {
			future.thenAccept(whenFinished);
		}
	}

	@Override
	public boolean teleportBlocking(WorldPosition pos) {
		return player.teleport(PaperApiHelper.locationFromWorldPos(pos));
	}

	@Override
	public PlayerInventoryHandle inventory() {
		return null;
	}

	@Override
	public void clearInventory() {

	}

	@Override
	public void openInventory(InventoryHandle handle) {

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
		player.setGameMode(fromPlayerMode(gamemode));
	}

	public static GameMode fromPlayerMode(PlayerGamemode gamemode) {
		return switch (gamemode) {
			case SPECTATOR -> GameMode.SPECTATOR;
			case CREATIVE -> GameMode.CREATIVE;
			case SURVIVAL -> GameMode.SURVIVAL;
			case ADVENTURE -> GameMode.ADVENTURE;
		};
	}

	public static PlayerGamemode toPlayerMode(GameMode gameMode) {
		return switch (gameMode) {
			case SURVIVAL -> PlayerGamemode.SURVIVAL;
			case CREATIVE -> PlayerGamemode.CREATIVE;
			case SPECTATOR -> PlayerGamemode.SPECTATOR;
			case ADVENTURE -> PlayerGamemode.ADVENTURE;
		};
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
	public void clearAllEffects() {

	}

	@Override
	public void removeAdvancementProgress(AdvancementHandle advancement) {
		AdvancementProgress progress = player.getAdvancementProgress(((AdvancementHandlePaper)advancement).handle());
		progress.getAwardedCriteria().forEach(progress::revokeCriteria);
	}

	@Override
	public boolean hasCooldown(StackHandle stack) {
		return player.hasCooldown(((StackHandlePaper)stack).handle());
	}

	@Override
	public void setCooldown(StackHandle stack, int cooldownTicks) {
		player.setCooldown(((StackHandlePaper)stack).handle(), cooldownTicks);
	}

	@Override
	public @NotNull Iterable<? extends Audience> audiences() {
		return List.of(player);
	}
}
