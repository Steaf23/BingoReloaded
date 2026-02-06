package io.github.steaf23.bingoreloaded.lib.api.player;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.EntityType;
import io.github.steaf23.bingoreloaded.lib.api.HytaleServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.PotionEffectInstance;
import io.github.steaf23.bingoreloaded.lib.api.StatisticType;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandleHytale;
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
		TransformComponent transform = entityComponent(TransformComponent.getComponentType());
		return fromTransformComponent(transform, world());
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
		PlatformResolver.get().runTask(playerRef.getWorldUuid(), task -> {
			Store<EntityStore> store = playerRef.getReference().getStore();
			Teleport teleport = Teleport.createForPlayer(((WorldHandleHytale)pos.world()).handle(),
					new Vector3d(pos.x(), pos.y(), pos.z()), // Target position
					new Vector3f(0, 0, 0)  // Target rotation (pitch, yaw, roll)
			);

			store.addComponent(playerRef.getReference(), Teleport.getComponentType(), teleport);

			if (whenFinished != null) {
				whenFinished.accept(true);
			}
		});
	}

	@Override
	public boolean teleportBlocking(WorldPosition pos) {
		teleportAsync(pos);
		return true;
	}

	@Override
	public PlayerInventoryHandle inventory() {
		return new PlayerInventoryHandleHytale(playerFromInternal().getInventory());
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
		Player.setGameMode(playerRef.getReference(), switch (gamemode) {
			case SURVIVAL -> GameMode.Adventure;
			case CREATIVE -> GameMode.Creative;
			case SPECTATOR -> GameMode.Adventure;
		}, store());
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
		return entityComponent(Player.getComponentType());
	}

	public <T extends com.hypixel.hytale.component.Component<EntityStore>> T entityComponent(ComponentType<EntityStore, T> type) {
		return playerRef.getReference().getStore().getComponent(playerRef.getReference(), type);
	}

	public Store<EntityStore> store() {
		return playerRef.getReference().getStore();
	}

	public WorldPosition fromTransformComponent(TransformComponent transform, WorldHandle world) {
		return new WorldPosition(world, transform.getPosition().x, transform.getPosition().y, transform.getPosition().z, transform.getRotation().getPitch(), transform.getRotation().getYaw());
	}

	public PlayerRef ref() {
		return playerRef;
	}
}
