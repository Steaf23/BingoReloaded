package io.github.steaf23.bingoreloaded.lib.world;

import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.Position;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BlockBuilder {

	private final WorldHandle world;

	public BlockBuilder(WorldHandle world) {
		this.world = world;
	}

	public void buildCuboid(ItemType blockType, GlobalPosition center, int extendX, int extendZ, int extendY, boolean skipSolidBlocks, @Nullable ItemType mask) {
		center.moveYBlocks(extendY * 2 + 1);
		for (int y = extendY; y >= -extendY + 1; y--) {
			buildPlatform(blockType, center.moveYBlocks(-1), extendX, extendZ, skipSolidBlocks, mask);
		}
	}

	public void buildPlatform(ItemType blockType, GlobalPosition center, int extendX, int extendZ, boolean skipSolidBlocks, @Nullable ItemType mask) {
		for (int x = -extendX; x < extendX + 1; x++) {
			for (int z = -extendZ; z < extendZ + 1; z++) {
				GlobalPosition blockLoc = center.clone();
				blockLoc.setX(blockLoc.blockX() + x);
				blockLoc.setZ(blockLoc.blockZ() + z);
				placeBlock(blockType, blockLoc, skipSolidBlocks, mask);
			}
		}
	}

	public void placeBlock(ItemType blockType, GlobalPosition pos, boolean skipSolidBlocks, @Nullable ItemType mask) {
		ItemType typeAtPos = world.typeAtPos(pos);
		if (skipSolidBlocks && typeAtPos.isSolid() && mask == null) {
			return;
		}

		if (mask == null || mask.equals(typeAtPos)) {
			world.setTypeAtPos(pos, blockType);
		}
	}

	public static GlobalPosition getRandomPosWithinRange(@NotNull GlobalPosition center, int rangeX, int rangeZ) {
		if (rangeX == 0 && rangeZ == 0) {
			return center.clone();
		}

		Position pos = Position.random()
				.multiply(rangeX * 2, 1.0D, rangeZ * 2)
				.add(new Position(-rangeX + 0.5D, 1.0D, -rangeZ + 0.5D));
		return center.clone().add(pos);
	}

	public static int getHighestBlockYAtPos(PlatformServer server, GlobalPosition pos) {
		return pos.world(server).highestBlockAt(pos).blockY();
	}
}
