package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.inventory.InventoryTemplate;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.platform.PaperInventories;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.statistics.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.util.DebugLogger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.waypoints.Waypoint;
import org.bukkit.GameMode;
import org.bukkit.Registry;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
	private final PlatformServer server;

	public PlayerHandlePaper(@NotNull PlatformServer server, @NotNull Player player) {
		this.player = player;
		this.server = server;
	}

	@Override
	public PlatformServer server() {
		return server;
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
		return new WorldHandlePaper(server, player.getWorld());
	}

	@Override
	public GlobalPosition position() {
		return PaperApiHelper.worldPosFromLocation(player.getLocation());
	}

	@Override
	public @Nullable GlobalPosition respawnPoint() {
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
	public int getStatisticValue(StatisticHandle stat) {
		if (stat.hasEntity()) {
			return player.getStatistic(Registry.STATISTIC.get(stat.type().key()), ((EntityTypePaper)stat.entityType()).handle());
		} else if (stat.hasItemType()) {
			return player.getStatistic(Registry.STATISTIC.get(stat.type().key()), ((ItemTypePaper)stat.itemType()).handle());
		} else {
			return player.getStatistic(Registry.STATISTIC.get(stat.type().key()));
		}
	}

	@Override
	public void teleportAsync(GlobalPosition pos, @Nullable Consumer<Boolean> whenFinished) {
		DebugLogger.addLog("Teleporting player async to pos: " + pos.x() + ", " + pos.y() + ", " + pos.z() + ", world: " + pos.dimension());

		var future = player.teleportAsync(PaperApiHelper.locationFromWorldPos(world(), pos), PlayerTeleportEvent.TeleportCause.PLUGIN);
		if (whenFinished != null) {
			future.thenAccept(whenFinished);
		}
	}

	@Override
	public boolean teleportBlocking(GlobalPosition pos) {
		DebugLogger.addLog("Teleporting player blocking to pos: " + pos.x() + ", " + pos.y() + ", " + pos.z() + ", world: " + pos.dimension());
		return player.teleport(PaperApiHelper.locationFromWorldPos(world(), pos), PlayerTeleportEvent.TeleportCause.PLUGIN);
	}

	@Override
	public void clearInventory() {
		player.getInventory().clear();
	}

	@Override
	public void tryOpenInventory(InventoryTemplate inventory) {
		PaperInventories.BukkitInventoryUpdater updater = inventory.firstListenerOfType(PaperInventories.BukkitInventoryUpdater.class);
		if (updater == null) {
			return;
		}

		player.openInventory(updater.inventory());
	}

	@Override
	public void setRespawnPoint(GlobalPosition newSpawn, boolean force) {
		player.setRespawnLocation(PaperApiHelper.locationFromWorldPos(world(), newSpawn), force);
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
	public void setStatisticValue(StatisticHandle stat, int value) {
		if (stat.hasEntity()) {
			player.setStatistic(Registry.STATISTIC.get(stat.type().key()), ((EntityTypePaper)stat.entityType()).handle(), value);
		} else if (stat.hasItemType()) {
			player.setStatistic(Registry.STATISTIC.get(stat.type().key()), ((ItemTypePaper)stat.itemType()).handle(), value);
		} else {
			player.setStatistic(Registry.STATISTIC.get(stat.type().key()), value);
		}
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
		player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
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

	@Override
	public boolean equals(Object obj) {
		return obj instanceof PlayerHandlePaper paperPlayer && paperPlayer.uniqueId().equals(uniqueId());
	}
}
