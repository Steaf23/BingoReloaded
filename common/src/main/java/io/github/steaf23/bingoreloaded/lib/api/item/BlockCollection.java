package io.github.steaf23.bingoreloaded.lib.api.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockCollection {
	private final List<BlockTag> tags = new ArrayList<>();
	private final Set<ItemType> individuals = new HashSet<>();

	public void add(BlockTag tag) {
		tags.add(tag);
	}

	public void add(ItemType type) {
		individuals.add(type);
	}

	public void add(BlockCollection other) {
		individuals.addAll(other.individuals);
		tags.addAll(other.tags);
	}

	public boolean contains(ItemType type) {
		return individuals.contains(type) || tags.stream().anyMatch(t -> t.contains(type));
	}

}
