package io.github.steaf23.bingoreloaded.lib.api.player;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.PotionEffectInstance;
import io.github.steaf23.bingoreloaded.lib.api.statistics.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatusEffectTypeFabric;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.InventoryHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.platform.FabricServer;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerHandleFabric implements PlayerHandle {

	private final ServerPlayer player;
	private final FabricServer server;

	public PlayerHandleFabric(FabricServer server, ServerPlayer player) {
		this.server = server;
		this.player = player;
	}

	public static GameType fromPlayerMode(PlayerGamemode gamemode) {
		return switch (gamemode) {
			case SPECTATOR -> GameType.SPECTATOR;
			case CREATIVE -> GameType.CREATIVE;
			case SURVIVAL -> GameType.SURVIVAL;
			case ADVENTURE -> GameType.ADVENTURE;
		};
	}

	public static PlayerGamemode toPlayerMode(GameType gameMode) {
		return switch (gameMode) {
			case SURVIVAL -> PlayerGamemode.SURVIVAL;
			case CREATIVE -> PlayerGamemode.CREATIVE;
			case SPECTATOR -> PlayerGamemode.SPECTATOR;
			case ADVENTURE -> PlayerGamemode.ADVENTURE;
		};
	}

	@Override
	public PlatformServer server() {
		return server;
	}

	@Override
	public String playerName() {
		return player.getPlainTextName();
	}

	@Override
	public Component displayName() {
		return Component.empty();
	}

	@Override
	public UUID uniqueId() {
		return player.getUUID();
	}

	@Override
	public WorldHandle world() {
		return new WorldHandleFabric(player.level());
	}

	@Override
	public WorldPosition position() {
		Vec3 pos = player.position();
		return new WorldPosition(world(), pos.x, pos.y, pos.z);
	}

	@Override
	public @Nullable WorldPosition respawnPoint() {
		ServerPlayer.RespawnConfig config = player.getRespawnConfig();
		if (config == null) {
			return null;
		}
		GlobalPos pos = config.respawnData().globalPos();

		ServerLevel levelToRespawn = player.level();
		for (ServerLevel level : server.handle().getAllLevels()) {
			if (level.dimension().equals(pos.dimension())) {
				levelToRespawn = level;
				break;
			}
		}

		return new WorldPosition(new WorldHandleFabric(levelToRespawn), pos.pos().getX(), pos.pos().getY(), pos.pos().getZ());
	}

	@Override
	public boolean hasPermission(String permission) {
		return true;
	}

	@Override
	public int level() {
		return player.experienceLevel;
	}

	@Override
	public float exp() {
		return player.experienceProgress;
	}

	@Override
	public double health() {
		return player.getHealth();
	}

	@Override
	public int foodLevel() {
		return player.getFoodData().getFoodLevel();
	}

	@Override
	public PlayerGamemode gamemode() {
		return toPlayerMode(player.gameMode());
	}

	@Override
	public int getStatisticValue(StatisticHandle stat) {
		return 0;
	}

	@Override
	public void teleportAsync(WorldPosition pos, @Nullable Consumer<Boolean> whenFinished) {

	}

	@Override
	public boolean teleportBlocking(WorldPosition pos) {
		player.teleportTo(((WorldHandleFabric)pos.world()).handle(), pos.x(), pos.y(), pos.z(), Set.of(), 0, 0, false);
		return false;
	}

	@Override
	public PlayerInventoryHandle inventory() {
		return null;
	}

	@Override
	public void clearInventory() {
		player.getInventory().clearContent();
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
		BlockPos pos = new BlockPos(newSpawn.blockX(), newSpawn.blockY(), newSpawn.blockZ());
		player.setRespawnPosition(new ServerPlayer.RespawnConfig(new LevelData.RespawnData(GlobalPos.of(player.level().dimension(), pos), 0, 0), force), false);
	}

	@Override
	public void setLevel(int level) {
		player.setExperienceLevels(level);
	}

	@Override
	public void setExp(float exp) {
		player.setExperiencePoints((int)exp);
	}

	@Override
	public void setFoodLevel(int foodLevel) {
		player.getFoodData().setFoodLevel(foodLevel);
	}

	@Override
	public void setHealth(double health) {
		player.setHealth((float)health);
	}

	@Override
	public void setGamemode(PlayerGamemode gamemode) {
		player.setGameMode(fromPlayerMode(gamemode));
	}

	@Override
	public void setStatisticValue(StatisticHandle stat, int value) {

	}

	@Override
	public void addEffect(PotionEffectInstance effect) {
		player.addEffect(new MobEffectInstance(
				((StatusEffectTypeFabric) effect.effect()).handle(),
				effect.durationTicks(),
				effect.amplifier(),
				effect.ambient(),
				effect.particles(),
				effect.icon()
		));
	}

	@Override
	public void clearAllEffects() {
		player.removeAllEffects();
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
		return player.isCrouching();
	}

	@Override
	public void closeInventory() {
		player.closeContainer();
	}

	@Override
	public void setWaypointColor(@Nullable TextColor color) {
		player.waypointIcon().color = Optional.of(color.value());
	}

	@Override
	public @NotNull Iterable<? extends Audience> audiences() {
		return List.of(player);
	}
}
