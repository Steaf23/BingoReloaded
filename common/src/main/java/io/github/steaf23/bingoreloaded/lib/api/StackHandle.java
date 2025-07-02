package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.text.Component;

import java.util.List;

public interface StackHandle {
	ItemType type();
	int amount();
	Component customName();
	List<Component> lore();
}
