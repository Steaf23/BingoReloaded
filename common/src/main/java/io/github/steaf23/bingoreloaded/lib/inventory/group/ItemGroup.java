package io.github.steaf23.bingoreloaded.lib.inventory.group;

import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;

public abstract class ItemGroup {
	private final ItemRect rect;

	public ItemGroup(ItemRect rect) {
		this.rect = rect;
	}

	public ItemGroup(int startX, int startY, int sizeX, int sizeY) {
		this(new ItemRect(startX, startY, sizeX, sizeY));
	}

	public abstract void updateVisibleItems(BasicMenu menu);

	ItemRect rect() {
		return rect;
	}

}
