package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;

public interface PotionEffectType {

	static PotionEffectType of(@Subst("minecraft:key") String key) {
		return PlatformResolver.get().resolvePotionEffectType(Key.key(key));
	}

	static PotionEffectType of(Key key) {
		return PlatformResolver.get().resolvePotionEffectType(key);
	}
}
