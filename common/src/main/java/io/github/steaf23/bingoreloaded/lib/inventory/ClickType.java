package io.github.steaf23.bingoreloaded.lib.inventory;

public enum ClickType {
	OTHER,
	LEFT_CLICK,
	RIGHT_CLICK,
	SHIFT_LEFT,
	SHIFT_RIGHT,
	;

	public boolean isLeftClick() {
		return this == LEFT_CLICK || this == SHIFT_LEFT;
	}

	public boolean isRightClick() {
		return this == RIGHT_CLICK || this == SHIFT_RIGHT;
	}

	public boolean isShiftClick() {
		return this == SHIFT_LEFT || this == SHIFT_RIGHT;
	}

}
