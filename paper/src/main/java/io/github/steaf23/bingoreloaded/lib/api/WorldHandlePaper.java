package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandlePaper;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.DebugLogger;
import net.kyori.adventure.key.Key;
import org.bukkit.World;

import java.util.Collection;

public class WorldHandlePaper implements WorldHandle {

	private final World world;
	private final PlatformServer server;

	public WorldHandlePaper(PlatformServer server, World world) {
		this.world = world;
		this.server = server;
	}

	@Override
	public Key key() {
		return world.key();
	}

	@Override
	public Collection<? extends PlayerHandle> players() {
		return world.getPlayers().stream().map(p -> new PlayerHandlePaper(server, p)).toList();
	}

	@Override
	public GlobalPosition spawnPoint() {
		DebugLogger.addLog("Spawn point of " + key() + ": " + world.getSpawnLocation());
		return PaperApiHelper.worldPosFromLocation(world.getSpawnLocation());
	}

	@Override
	public DimensionType dimensionType() {
		return switch (world.getEnvironment()) {
			case NETHER -> DimensionType.NETHER;
			case THE_END -> DimensionType.THE_END;
			default -> DimensionType.OVERWORLD;
		};
	}

	@Override
	public void spawnEntity(EntityType type, GlobalPosition pos) {
		world.spawnEntity(PaperApiHelper.locationFromWorldPos(this, pos), ((EntityTypePaper)type).handle());
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
	public BiomeType biomeAtPos(GlobalPosition pos) {
		return new BiomeTypePaper(world.getBiome(PaperApiHelper.locationFromWorldPos(this, pos)));
	}

	@Override
	public ItemType typeAtPos(GlobalPosition pos) {
		return ItemType.of(world.getType(pos.blockX(), pos.blockY(), pos.blockZ()).key());
	}

	@Override
	public void setTypeAtPos(GlobalPosition pos, ItemType type) {
		world.setType(PaperApiHelper.locationFromWorldPos(this, pos), ((ItemTypePaper)type).handle());
	}

	@Override
	public GlobalPosition highestBlockAt(GlobalPosition pos) {
		return PaperApiHelper.worldPosFromLocation(world.getHighestBlockAt(pos.blockX(), pos.blockZ()).getLocation());
	}

	@Override
	public void dropItem(StackHandle item, GlobalPosition location) {
		world.dropItem(PaperApiHelper.locationFromWorldPos(this, location), ((StackHandlePaper)item).handle());
	}

	public World handle() {
		return world;
	}
}
