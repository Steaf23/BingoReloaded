package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;

public interface ItemTypeFactory {

	ItemType defaultTaskItem();

	ItemType platformBlock();

	ItemType genericStatisticTask();

	ItemType genericAdvancementTask();
}
