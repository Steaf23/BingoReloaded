package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.server.level.ServerLevel;

import java.util.Collection;
import java.util.List;

public class WorldHandleFabric implements WorldHandle {

	private final ServerLevel world;

	public WorldHandleFabric(ServerLevel world) {
		this.world = world;
	}

	public ServerLevel handle() {
		return world;
	}

	@Override
	public Key key() {
		return FabricTypes.keyFromId(world.dimension().identifier());
	}

	@Override
	public Collection<? extends PlayerHandle> players() {
		return List.of();
	}

	@Override
	public GlobalPosition spawnPoint() {
		return null;
	}

	@Override
	public DimensionType dimensionType() {
		return null;
	}

	@Override
	public void spawnEntity(EntityType type, GlobalPosition pos) {

	}

	@Override
	public void setStorming(boolean storm) {

	}

	@Override
	public void setTimeOfDay(long time) {

	}

	@Override
	public BiomeType biomeAtPos(GlobalPosition pos) {
		return null;
	}

	@Override
	public ItemType typeAtPos(GlobalPosition pos) {
		return null;
	}

	@Override
	public void setTypeAtPos(GlobalPosition pos, ItemType type) {

	}

	@Override
	public GlobalPosition highestBlockAt(GlobalPosition pos) {
		return null;
	}

	@Override
	public void dropItem(StackHandle item, GlobalPosition location) {

	}
}
