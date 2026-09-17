package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public interface EntityType extends Keyed {

	EntityType PLAYER = EntityType.of(Key.key("player"));

	static EntityType of(Key key) {
		return PlatformResolver.getRegistries().resolveEntityType(key);
	}

	boolean equals(Object other);
}
