package io.github.steaf23.bingoreloaded.lib.inventory.group;

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class SelectionModel {
	public enum SelectMode {
		NONE,
		SINGLE_OR_NONE,
		SINGLE,
		MULTIPLE_OR_NONE,
	}

	private final SelectMode selectMode;
	private final Set<Integer> selectedSlots = new HashSet<>();
	private final Supplier<Boolean> canSelectFirstItem;

	public SelectionModel(SelectMode mode, @Nullable Supplier<Boolean> canSelectFirstItem) {
		this.selectMode = mode;
		this.canSelectFirstItem = Objects.requireNonNullElse(canSelectFirstItem, () -> false);
	}

	public void reset() {
		selectedSlots.clear();
		if (selectMode == SelectMode.SINGLE) {
			if (canSelectFirstItem.get()) {
				selectedSlots.add(0);
			}
		}
	}

	public boolean contains(int slotIndex) {
		return selectedSlots().contains(slotIndex);
	}

	public Set<Integer> selectedSlots() {
		return selectedSlots;
	}

	public void toggleSlot(int slotIndex) {
		switch (selectMode) {
			case SINGLE -> {
				selectedSlots.clear();
				selectedSlots.add(slotIndex);
			}
			case SINGLE_OR_NONE -> {
				if (selectedSlots.contains(slotIndex)) {
					selectedSlots.clear();
				} else {
					selectedSlots.clear();
					selectedSlots.add(slotIndex);
				}
			}
			case MULTIPLE_OR_NONE -> {
				if (selectedSlots.contains(slotIndex)) {
					selectedSlots.remove(slotIndex);
				} else {
					selectedSlots.add(slotIndex);
				}
			}
		}
	}
}
