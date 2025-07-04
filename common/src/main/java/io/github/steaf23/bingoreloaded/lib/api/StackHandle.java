package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface StackHandle {
	ItemType type();
	int amount();
	Component customName();
	List<Component> lore();
	Key compareKey();

	void addStorage(String key, DataStorage storage);
}
