package io.github.steaf23.bingoreloaded.lib.inventory.group;

import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;

import java.util.ArrayList;
import java.util.List;

public class StackedGroup extends ItemGroup {

	private final List<ItemGroup> stack = new ArrayList<>();
	private int current = 0;

	public StackedGroup(int x, int y, int sizeX, int sizeY) {
		super(x, y, sizeX, sizeY);
	}

	public StackedGroup addGroup(ItemGroup group) {
		stack.add(group);
		return this;
	}

	public void setCurrentGroup(BasicMenu menu, int index) {
		current = index;
		updateVisibleItems(menu);
	}

	public ItemGroup getCurrentGroup() {
		return stack.get(current);
	}

	@Override
	public void updateVisibleItems(BasicMenu menu) {
		ItemGroup current = getCurrentGroup();
		current.updateVisibleItems(menu);
	}
}
