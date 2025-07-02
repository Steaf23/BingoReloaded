package io.github.steaf23.bingoreloaded.lib.api;

public enum PlayerClickType {
	LEFT,
	SHIFT_LEFT,
	RIGHT,
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
	UNKNOWN;

	public boolean isKeyboardClick() {
		return (this == PlayerClickType.NUMBER_KEY) || (this == PlayerClickType.DROP) || (this == PlayerClickType.CONTROL_DROP) || (this == PlayerClickType.SWAP_OFFHAND);
	}

	/**
	 * Gets whether this PlayerClickType represents the pressing of a mouse button
	 *
	 * @return {@code true} if this PlayerClickType represents the pressing of a mouse button
	 */
	public boolean isMouseClick() {
		return (this == PlayerClickType.DOUBLE_CLICK) || (this == PlayerClickType.LEFT) || (this == PlayerClickType.RIGHT) || (this == PlayerClickType.MIDDLE)
				|| (this == PlayerClickType.WINDOW_BORDER_LEFT) || (this == PlayerClickType.SHIFT_LEFT) || (this == PlayerClickType.SHIFT_RIGHT) || (this == PlayerClickType.WINDOW_BORDER_RIGHT);
	}

	/**
	 * Gets whether this PlayerClickType represents an action that can only be
	 * performed by a Player in creative mode.
	 *
	 * @return {@code true} if this action requires Creative mode
	 */
	public boolean isCreativeAction() {
		// Why use middle click?
		return (this == PlayerClickType.MIDDLE) || (this == PlayerClickType.CREATIVE);
	}

	/**
	 * Gets whether this PlayerClickType represents a right click.
	 *
	 * @return {@code true} if this PlayerClickType represents a right click
	 */
	public boolean isRightClick() {
		return (this == PlayerClickType.RIGHT) || (this == PlayerClickType.SHIFT_RIGHT);
	}

	/**
	 * Gets whether this PlayerClickType represents a left click.
	 *
	 * @return {@code true} if this PlayerClickType represents a left click
	 */
	public boolean isLeftClick() {
		return (this == PlayerClickType.LEFT) || (this == PlayerClickType.SHIFT_LEFT) || (this == PlayerClickType.DOUBLE_CLICK) || (this == PlayerClickType.CREATIVE);
	}

	/**
	 * Gets whether this PlayerClickType indicates that the shift key was pressed
	 * down when the click was made.
	 *
	 * @return {@code true} if the action uses Shift.
	 */
	public boolean isShiftClick() {
		return (this == PlayerClickType.SHIFT_LEFT) || (this == PlayerClickType.SHIFT_RIGHT);
	}
}
