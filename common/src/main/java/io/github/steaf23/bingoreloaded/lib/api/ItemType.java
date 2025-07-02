package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public interface ItemType extends Keyed {

	ItemType AIR = of(Key.key("air"));

	default boolean isAir() {
		return key().equals(AIR.key());
	}

	static ItemType of(String type) {

	}

	static ItemType of(Key type) {

	}
}
