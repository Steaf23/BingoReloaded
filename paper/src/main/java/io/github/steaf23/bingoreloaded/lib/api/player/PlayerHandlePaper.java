package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.EntityTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.PaperApiHelper;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.PotionEffectInstance;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.StatisticTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.util.DebugLogger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.waypoints.Waypoint;
import org.bukkit.GameMode;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerHandlePaper implements PlayerHandle {

	private final Player player;

	public PlayerHandlePaper(@NotNull Player player) {
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
		return player.getStatistic(((StatisticTypePaper)type).handle());
	}

	@Override
	public int getStatisticValue(StatisticType type, EntityType entity) {
		return player.getStatistic(((StatisticTypePaper)type).handle(), ((EntityTypePaper)entity).handle());
	}

	@Override
	public int getStatisticValue(StatisticType type, ItemType item) {
		return player.getStatistic(((StatisticTypePaper)type).handle(), ((ItemTypePaper)item).handle());
	}

	@Override
	public void teleportAsync(WorldPosition pos, @Nullable Consumer<Boolean> whenFinished) {
		DebugLogger.addLog("Teleporting player async to pos: " + pos.x() + ", " + pos.y() + ", " + pos.z() + ", world: " + pos.world().name());

		var future = player.teleportAsync(PaperApiHelper.locationFromWorldPos(pos), PlayerTeleportEvent.TeleportCause.PLUGIN);
		if (whenFinished != null) {
			future.thenAccept(whenFinished);
		}
	}

	@Override
	public boolean teleportBlocking(WorldPosition pos) {
		DebugLogger.addLog("Teleporting player blocking to pos: " + pos.x() + ", " + pos.y() + ", " + pos.z() + ", world: " + pos.world().name());
		return player.teleport(PaperApiHelper.locationFromWorldPos(pos), PlayerTeleportEvent.TeleportCause.PLUGIN);
	}

	@Override
	public PlayerInventoryHandle inventory() {
		return new PlayerInventoryHandlePaper(player.getInventory());
	}

	@Override
	public void openInventory(InventoryHandle inventory) {
		player.openInventory(((InventoryHandlePaper)inventory).handle());
	}

	@Override
	public InventoryHandle enderChest() {
		return new InventoryHandlePaper(player.getEnderChest());
	}

	@Override
	public void setRespawnPoint(WorldPosition newSpawn, boolean force) {
		player.setRespawnLocation(PaperApiHelper.locationFromWorldPos(newSpawn), force);
	}

	@Override
	public void setLevel(int level) {
		player.setLevel(level);
	}

	@Override
	public void setExp(float exp) {
		player.setExp(exp);
	}

	@Override
	public void setFoodLevel(int foodLevel) {
		player.setFoodLevel(foodLevel);
	}

	@Override
	public void setHealth(double health) {
		player.setHealth(health);
	}

	@Override
	public void setGamemode(PlayerGamemode gamemode) {
		player.setGameMode(fromPlayerMode(gamemode));
	}

	@Override
	public void setStatisticValue(StatisticType type, int value) {
		player.setStatistic(((StatisticTypePaper)type).handle(), value);
	}

	@Override
	public void setStatisticValue(StatisticType type, EntityType entity, int value) {
		player.setStatistic(((StatisticTypePaper)type).handle(), ((EntityTypePaper)entity).handle(), value);
	}

	@Override
	public void setStatisticValue(StatisticType type, ItemType item, int value) {
		player.setStatistic(((StatisticTypePaper)type).handle(), ((ItemTypePaper)item).handle(), value);
	}

	@Override
	public void addEffect(PotionEffectInstance effect) {
		player.addPotionEffect(new PotionEffect(
				((StatusEffectTypePaper)effect.effect()).handle(),
				effect.durationTicks(),
				effect.amplifier(),
				effect.ambient(),
				effect.particles(),
				effect.icon()));
	}

	public static GameMode fromPlayerMode(PlayerGamemode gamemode) {
		return switch (gamemode) {
			case SPECTATOR -> GameMode.SPECTATOR;
			case CREATIVE -> GameMode.CREATIVE;
			case SURVIVAL -> GameMode.SURVIVAL;
		};
	}

	public static PlayerGamemode toPlayerMode(GameMode gameMode) {
		return switch (gameMode) {
			case SURVIVAL, ADVENTURE -> PlayerGamemode.SURVIVAL;
			case CREATIVE -> PlayerGamemode.CREATIVE;
			case SPECTATOR -> PlayerGamemode.SPECTATOR;
		};
	}

	@Override
	public void clearAllEffects() {
		player.clearActivePotionEffects();
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
	public boolean hasCooldownOnGroup(Key cooldownGroup) {
		return player.getCooldown(cooldownGroup) > 0;
	}

	@Override
	public void setCooldown(StackHandle stack, int cooldownTicks) {
		player.setCooldown(((StackHandlePaper)stack).handle(), cooldownTicks);
	}

	@Override
	public void setCooldownOnGroup(Key cooldownGroup, int cooldownTicks) {
		player.setCooldown(cooldownGroup, cooldownTicks);
	}

	@Override
	public boolean isSneaking() {
		return player.isSneaking();
	}

	@Override
	public void closeInventory() {
		player.closeInventory();
	}

	@Override
	public void kick(@Nullable Component reason) {
		player.kick(reason, PlayerKickEvent.Cause.PLUGIN);
	}

	@Override
	public void setWaypointColor(@Nullable TextColor color) {
		ServerPlayer player = ((CraftPlayer)handle()).getHandle();
		Waypoint.Icon icon = player.waypointIcon();
		icon.color = color == null ? Optional.empty() : Optional.of(color.value());
		icon.cloneAndAssignStyle(player);
		player.level().getWaypointManager().addPlayer(player);
	}

	@Override
	public @NotNull Iterable<? extends Audience> audiences() {
		return List.of(player);
	}

	public Player handle() {
		return player;
	}
}
