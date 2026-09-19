package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import net.kyori.adventure.key.Key;

import java.util.List;

public interface BlockTag {

	static BlockTag of(Key key) {
		return PlatformResolver.getRegistries().resolveTag(key);
	}

	Key id();
	boolean contains(ItemType key);
	List<ItemType> entries();
}
