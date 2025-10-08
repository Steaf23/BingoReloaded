package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public interface DimensionType extends Keyed {

	DimensionType OVERWORLD = of(Key.key("minecraft:overworld"));
	DimensionType NETHER = of(Key.key("minecraft:nether"));
	DimensionType THE_END = of(Key.key("minecraft:the_end"));

	static DimensionType of(Key key) {
		return PlatformResolver.get().resolveDimensionType(key);
	}
}
