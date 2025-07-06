package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

public interface ItemType extends Keyed {

	ItemType AIR = of(Key.key("air"));

	boolean isBlock();
	default boolean isAir() {
		return key().equals(AIR.key());
	}
	boolean isSolid();

	static ItemType of(@NotNull @Subst("minecraft:resource") String type) {
		return of(Key.key(type));
	}

	static ItemType of(@NotNull Key type) {
		return PlatformResolver.get().resolveItemType(type);
	}
}
