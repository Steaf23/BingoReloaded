package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.ExtendedPersistentState;
import net.minecraft.server.world.ServerWorld;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class WorldHandleFabric implements WorldHandle {

	private final ServerWorld world;

	public WorldHandleFabric(ServerWorld world) {
		this.world = world;
	}

	@Override
	public String name() {
		return world.getRegistryKey().getValue().toString();
	}

	@Override
	public UUID uniqueId() {
		return getId();
	}

	@Override
	public Collection<? extends PlayerHandle> players() {
		return List.of();
	}

	@Override
	public WorldPosition spawnPoint() {
		return null;
	}

	@Override
	public DimensionType dimension() {
		return null;
	}

	@Override
	public void spawnEntity(EntityType type, WorldPosition pos) {

	}

	@Override
	public void setStorming(boolean storm) {

	}

	@Override
	public void setTimeOfDay(long time) {

	}

	@Override
	public BiomeType biomeAtPos(WorldPosition pos) {
		return null;
	}

	@Override
	public ItemType typeAtPos(WorldPosition pos) {
		return null;
	}

	@Override
	public void setTypeAtPos(WorldPosition pos, ItemType type) {

	}

	@Override
	public WorldPosition highestBlockAt(WorldPosition pos) {
		return null;
	}

	@Override
	public void dropItem(StackHandle item, WorldPosition location) {

	}

	private UUID getId() {
		return world.getPersistentStateManager().getOrCreate(ExtendedPersistentState.TYPE).uuid();
	}
}
