package io.github.steaf23.bingoreloaded.lib.inventory.group;

import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;

public record ItemRect(int startX, int startY, int sizeX, int sizeY) {

	public int start() {
		return ItemTemplate.slotFromXY(startX, startY);
	}

	public int toGlobal(int localX, int localY) {
		return (startY * 9) + (localY * 9) + startX + localX;
	}

	public int toLocal(int global) {
		int globalY = global / 9;
		int globalX = global % 9;

		return (globalY - startY) * sizeX + (globalX - startX);
	}

	public int getSlotCount() {
		return sizeX * sizeY;
	}

	public boolean containsSlot(int slot) {
		int globalY = slot / 9;
		int globalX = slot % 9;

		return startX <= globalX && startX + sizeX >= globalX && startY <= globalY && startY + sizeY >= globalY;
	}
}
