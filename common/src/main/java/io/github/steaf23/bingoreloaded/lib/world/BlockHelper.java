package io.github.steaf23.bingoreloaded.lib.world;

import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
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
		if (skipSolidBlocks || !typeAtPos.isSolid() && (mask == null || mask.equals(typeAtPos))) {
			pos.world().setTypeAtPos(pos, blockType);
		}
	}

	static WorldPosition getRandomPosWithinRange(WorldPosition center, int rangeX, int rangeY) {
//		Vector placement = Vector.getRandom().multiply(spread * 2).add(new Vector(-spread, -spread, -spread));
//		return center.add(placement);
		return center.clone();
	}

	static int getHighestBlockYAtPos(WorldPosition pos) {
		return pos.world().highestBlockAt(pos).blockY();
	}
}
