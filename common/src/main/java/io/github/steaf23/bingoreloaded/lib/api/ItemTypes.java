package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;

public interface ItemTypes {

	ItemType of(Key key);
	ItemType of(String key);
}
