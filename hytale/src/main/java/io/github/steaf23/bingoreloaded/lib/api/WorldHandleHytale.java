package io.github.steaf23.bingoreloaded.lib.api;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypeHytale;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class WorldHandleHytale implements WorldHandle {

	public static int WORLD_HEIGHT = 320;

	World world;

	public WorldHandleHytale(World world) {
		this.world = world;
	}

	public World handle() {
		return world;
	}

	@Override
	public String name() {
		return world.getName();
	}

	@Override
	public UUID uniqueId() {
		return world.getWorldConfig().getUuid();
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
		return DimensionType.OVERWORLD;
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
		BlockType type = world.getBlockType(pos.blockX(), pos.blockY(), pos.blockZ());
		if (type != null) {
			return new ItemTypeHytale(type.getId());
		}
		return ItemType.AIR;
	}

	@Override
	public void setTypeAtPos(WorldPosition pos, ItemType type) {

	}

	@Override
	public WorldPosition highestBlockAt(WorldPosition pos) {

		for (int y = WORLD_HEIGHT; y >= 0; y--) {
			BlockType type = world.getBlockType(pos.blockX(), pos.blockY(), pos.blockZ());
			if (type == null || type.equals(BlockType.EMPTY)) {
				continue;
			}
			return pos.clone().setY(y);
		}

		return pos.clone().setY(0.0);
	}

	@Override
	public void dropItem(StackHandle item, WorldPosition location) {

	}
}
