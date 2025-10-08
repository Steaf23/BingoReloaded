package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public interface EntityType extends Keyed {
	static EntityType of(Key key) {
		return PlatformResolver.get().resolveEntityType(key);
	}

	boolean equals(Object other);
}
