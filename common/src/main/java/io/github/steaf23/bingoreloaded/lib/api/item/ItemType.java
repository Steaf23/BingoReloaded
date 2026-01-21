package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface ItemType extends Keyed {

	ItemType AIR = of(Key.key("air"));

	boolean isBlock();
	default boolean isAir() {
		return key().equals(AIR.key());
	}
	boolean isSolid();

	static ItemType of(@NotNull String type) {
		return PlatformResolver.get().resolveItemType(type);
	}

	static ItemType of(@NotNull Key type) {
		return PlatformResolver.get().resolveItemType(type);
	}

	boolean equals(Object other);
}
