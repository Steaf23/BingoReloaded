package io.github.steaf23.bingoreloaded.gameloop.veinminer;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.BlockCollection;
import io.github.steaf23.bingoreloaded.lib.api.item.BlockTag;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import net.kyori.adventure.key.Key;

public class CubeVeinMiner implements VeinMiner {

	private final BlockCollection mineable = loadVeinMineable();
	private final int radius;

	// Anti-recursion
	private boolean insideBlockBreak = false;

	/**
	 * @param radius amount of extra blocks to check for the same blocks.
	 */
	public CubeVeinMiner(int radius) {
		this.radius = Math.abs(radius);
	}

	private static BlockCollection loadVeinMineable() {
		DataAccessor data =	BingoReloaded.getDataAccessor("veinminer_settings");
		BlockCollection collection = new BlockCollection();
		data.getList("allowedBlocks", TagDataType.STRING)
				.forEach(s -> {
					if (s.startsWith("#")) {
						collection.add(BlockTag.of(Key.key(s.substring(1))));
					}
					else {
						collection.add(ItemType.of(s));
					}
				});

		return collection;
	}

	@Override
	public void playerBreaksBlock(PlayerHandle player, BingoGame game, GlobalPosition blockPos, StackHandle tool) {
		if (insideBlockBreak) {
			return;
		}

		ItemType typeToMine = player.world().typeAtPos(blockPos);
		if (!mineable.contains(typeToMine)) {
			return;
		}

		if (!typeToMine.isPreferredTool(tool)) {
			return;
		}

		insideBlockBreak = true;

		for (int z = -radius; z < radius + 1; z++) {
			for (int y = -radius; y < radius + 1; y++) {
				for (int x = -radius; x < radius + 1; x++) {
					GlobalPosition newPos = blockPos.clone().add(new GlobalPosition(blockPos.dimension(), x, y, z));
					if (blockPos.blockX() == newPos.blockX() && blockPos.blockY() == newPos.blockY() && blockPos.blockZ() == newPos.blockZ()) {
						continue;
					}
					if (player.world().typeAtPos(newPos).equals(typeToMine)) {
						player.breakBlock(newPos);
					}
				}
			}
		}

		insideBlockBreak = false;
	}
}
