package io.github.steaf23.bingoreloaded.lib.api.item;

import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;

import java.util.Collection;
import java.util.List;

public class BlockTagPaper implements BlockTag {

	private final TagKey<BlockType> key;
	private final Collection<BlockType> types;

	public BlockTagPaper(TagKey<BlockType> id) {
		this.types = Registry.BLOCK.getTagValues(id);
		key = id;
	}

	@Override
	public Key id() {
		return key.key();
	}

	@Override
	public boolean contains(ItemType blockType) {
		return types.contains(Registry.BLOCK.get(blockType.key()));
	}

	@Override
	public List<ItemType> entries() {
		return types.stream().map(t -> ItemType.of(t.key())).toList();
	}
}
