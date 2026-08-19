package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypeFabric;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.platform.FabricServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleFabric;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Collection;

public class WorldHandleFabric implements WorldHandle {

	private final ServerLevel world;
	private final FabricServer server;

	public WorldHandleFabric(FabricServer server, ServerLevel world) {
		this.server = server;
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
		return world.players().stream()
				.map(p -> new PlayerHandleFabric(server, p))
				.toList();
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
		world.getWeatherData().setThundering(storm);
	}

	@Override
	public void setTimeOfDay(long time) {
		Holder<WorldClock> daytime = world.getServer().registryAccess()
				.lookupOrThrow(Registries.WORLD_CLOCK)
				.getOrThrow(WorldClocks.OVERWORLD);
		world.getServer().clockManager().setTotalTicks(daytime, time);
	}

	public boolean isOceanBiome(GlobalPosition pos) {
		return world.getBiome(FabricTypes.toBlockPos(pos)).is(BiomeTags.IS_OCEAN);
	}

	@Override
	public boolean isRiverBiome(GlobalPosition pos) {
		return world.getBiome(FabricTypes.toBlockPos(pos)).is(BiomeTags.IS_RIVER);
	}

	@Override
	public ItemType typeAtPos(GlobalPosition pos) {
		return new ItemTypeFabric(world.getBlockState(FabricTypes.toBlockPos(pos)).getBlock().asItem());
	}

	@Override
	public void setTypeAtPos(GlobalPosition pos, ItemType type) {
		Item item = ((ItemTypeFabric)type).handle();
		if (item instanceof BlockItem bItem) {
			world.setBlock(FabricTypes.toBlockPos(pos), bItem.getBlock().defaultBlockState(), Block.UPDATE_ALL);
		}
	}

	@Override
	public GlobalPosition highestBlockAt(GlobalPosition pos) {
		BlockPos blockPos = FabricTypes.toBlockPos(pos);
		int y = 0;
		if (world.isLoaded(blockPos)) {
			y = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockPos);
		} else {
			ServerChunkCache source = world.getChunkSource();
			y = source.getGenerator().getBaseHeight(
					pos.blockX(), pos.blockZ(),
					Heightmap.Types.WORLD_SURFACE_WG,
					world, source.randomState());
		}

		return new GlobalPosition(pos.dimension(), pos.x(), y, pos.z());
	}

	@Override
	public void dropItem(StackHandle item, GlobalPosition location) {

	}
}
