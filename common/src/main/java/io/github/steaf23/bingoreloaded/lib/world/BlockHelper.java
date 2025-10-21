package io.github.steaf23.bingoreloaded.lib.world;

import io.github.steaf23.bingoreloaded.lib.api.Position;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockHelper {
	static void buildCuboid(ItemType blockType, WorldPosition center, int extendX, int extendZ, int extendY, boolean skipSolidBlocks, @Nullable ItemType mask) {
		for (int y = -extendY; y < extendY + 1; y++) {
			buildPlatform(blockType, center.moveYBlocks(1), extendX, extendZ, skipSolidBlocks, mask);
		}
	}

	static void buildPlatform(ItemType blockType, WorldPosition center, int extendX, int extendZ, boolean skipSolidBlocks, @Nullable ItemType mask) {
		for (int x = -extendX; x < extendX + 1; x++) {
			for (int z = -extendZ; z < extendZ + 1; z++) {
				WorldPosition blockLoc = center.clone();
				blockLoc.setX(blockLoc.blockX() + x);
				blockLoc.setZ(blockLoc.blockZ() + z);
				placeBlock(blockType, blockLoc, skipSolidBlocks, mask);
			}
		}
	}

	static void placeBlock(ItemType blockType, WorldPosition pos, boolean skipSolidBlocks, @Nullable ItemType mask) {
		ItemType typeAtPos = pos.world().typeAtPos(pos);
		if (skipSolidBlocks && typeAtPos.isSolid() && mask == null) {
			return;
		}

		if (mask == null || mask.equals(typeAtPos)) {
			pos.world().setTypeAtPos(pos, blockType);
		}
	}

	static WorldPosition getRandomPosWithinRange(@NotNull WorldPosition center, int rangeX, int rangeZ) {
		if (rangeX == 0 && rangeZ == 0) {
			return center.clone();
		}

		Position pos = Position.random()
				.multiply(rangeX * 2, 1.0D, rangeZ * 2)
				.add(new Position(-rangeX + 0.5D, 1.0D, -rangeZ + 0.5D));
		return center.clone().add(pos);
	}

	static int getHighestBlockYAtPos(WorldPosition pos) {
		return pos.world().highestBlockAt(pos).blockY();
	}
}
