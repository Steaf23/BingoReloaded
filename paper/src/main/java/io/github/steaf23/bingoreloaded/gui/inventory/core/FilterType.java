package io.github.steaf23.bingoreloaded.gui.inventory.core;

import io.github.steaf23.bingoreloaded.lib.util.StringAdditions;

public enum FilterType
{
    NONE,
    ITEM_ID,
    DISPLAY_NAME,
    MATERIAL,
    CUSTOM;

    @Override
    public String toString() {
        return StringAdditions.capitalize(name());
    }
}
