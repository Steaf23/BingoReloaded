package io.github.steaf23.bingoreloaded.lib.api.item;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jspecify.annotations.NonNull;

public record VanillaItem(String keyStr) implements Keyed {

	@Override
	public @NonNull Key key() {
		return Key.key(keyStr);
	}

	public ItemType type() {
		return ItemType.of(keyStr);
	}
}
