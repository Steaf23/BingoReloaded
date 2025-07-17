package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;

public interface StackBuilder {
	StackHandle buildItem(ItemTemplate template, boolean hideAttributes, boolean customTextures);

	default StackHandle buildItem(ItemTemplate template, boolean hideAttributes) {
		return buildItem(template, hideAttributes, false);
	}

	default StackHandle buildItem(ItemTemplate template) {
		return buildItem(template, false, false);
	}
}
