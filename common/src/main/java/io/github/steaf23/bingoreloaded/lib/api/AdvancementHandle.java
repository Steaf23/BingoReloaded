package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;

public interface AdvancementHandle extends Keyed {
	static AdvancementHandle of(Key key) {
		return PlatformResolver.get().resolveAdvancement(key);
	}

	boolean hasDisplay();

	ItemType displayIcon();

	Component displayName();
	Component description();

	boolean equals(Object other);
}
