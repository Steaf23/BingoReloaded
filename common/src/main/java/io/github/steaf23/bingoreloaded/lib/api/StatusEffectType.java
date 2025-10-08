package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;

public interface StatusEffectType {

	static StatusEffectType of(@Subst("minecraft:key") String key) {
		return PlatformResolver.get().resolvePotionEffectType(Key.key(key));
	}

	static StatusEffectType of(Key key) {
		return PlatformResolver.get().resolvePotionEffectType(key);
	}
}
