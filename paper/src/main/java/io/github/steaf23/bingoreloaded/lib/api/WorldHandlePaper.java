package io.github.steaf23.bingoreloaded.lib.api;

import org.bukkit.World;

import java.util.Collection;
import java.util.UUID;

public class WorldHandlePaper implements WorldHandle {

	private final World world;

	public WorldHandlePaper(World world) {
		this.world = world;
	}

	@Override
	public String name() {
		return world.getName();
	}

	@Override
	public UUID uniqueId() {
		return world.getUID();
	}

	@Override
	public Collection<? extends PlayerHandle> players() {
		return world.getPlayers().stream().map(PlayerHandlePaper::new).toList();
	}

	@Override
	public WorldPosition spawnPoint() {
		return PaperApiHelper.worldPosFromLocation(world.getSpawnLocation());
	}

	@Override
	public DimensionType dimension() {
		return switch (world.getEnvironment()) {
			case NETHER -> DimensionType.NETHER;
			case THE_END -> DimensionType.THE_END;
			default -> DimensionType.OVERWORLD;
		};
	}

	@Override
	public void setStorming(boolean storm) {
		world.setStorm(storm);
	}

	@Override
	public void setTimeOfDay(long time) {
		world.setTime(time);
	}

	@Override
	public BiomeType biomeAtPos(WorldPosition pos) {
		return new BiomeTypePaper(world.getBiome(PaperApiHelper.locationFromWorldPos(pos)));
	}

	@Override
	public ItemType typeAtPos(WorldPosition pos) {
		return ItemType.of(world.getType(pos.blockX(), pos.blockY(), pos.blockZ()).key());
	}

	@Override
	public void setTypeAtPos(WorldPosition pos, ItemType type) {

	}

	@Override
	public WorldPosition highestBlockAt(WorldPosition pos) {
		return null;
	}

	public World handle() {
		return world;
	}
}
