package io.github.steaf23.bingoreloaded.lib.inventory;

public enum ClickType {
	UNKNOWN,
	LEFT_CLICK,
	RIGHT_CLICK,
	SHIFT_LEFT,
	SHIFT_RIGHT,
	WINDOW_BORDER_LEFT,
	WINDOW_BORDER_RIGHT,
	MIDDLE,
	NUMBER_KEY,
	DOUBLE_CLICK,
	DROP,
	CONTROL_DROP,
	CREATIVE,
	SWAP_OFFHAND,
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
