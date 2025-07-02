package io.github.steaf23.bingoreloaded.lib.inventory;

public record MenuFilterSettings(FilterType filterType, String name) {

	public static MenuFilterSettings EMPTY = new MenuFilterSettings(FilterType.NONE, "");
}
