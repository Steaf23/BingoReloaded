package io.github.steaf23.bingoreloaded.gui.inventory.core;

public record MenuFilterSettings(FilterType filterType, String name) {

	public static MenuFilterSettings EMPTY = new MenuFilterSettings(FilterType.NONE, "");
}
