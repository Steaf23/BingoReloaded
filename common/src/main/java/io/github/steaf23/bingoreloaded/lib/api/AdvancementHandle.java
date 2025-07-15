package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public interface AdvancementHandle extends Keyed {
	static AdvancementHandle of(Key key) {
		return PlatformResolver.get().resolveAdvancement(key);
	}

	ItemType displayIcon();
}
