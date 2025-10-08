package io.github.steaf23.bingoreloaded.util;

import net.kyori.adventure.key.Key;
import net.minecraft.util.Identifier;

public class FabricTypes {

	public static Identifier idFromKey(Key key) {
		return Identifier.of(key.namespace(), key.value());
	}

	public static Key keyFromId(Identifier id) {
		return Key.key(id.getNamespace(), id.getPath());
	}
}
