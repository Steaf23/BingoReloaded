package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;

public interface AdvancementHandle extends Keyed {
	static AdvancementHandle of(PlatformServer server, Key key) {
		return PlatformResolver.getRegistries().resolveAdvancement(server, key);
	}

	boolean hasDisplay();

	ItemType displayIcon();

	Component displayName();
	Component description();

	boolean equals(Object other);
}
