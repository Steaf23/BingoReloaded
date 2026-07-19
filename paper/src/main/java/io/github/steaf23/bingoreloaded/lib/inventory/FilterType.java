package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.util.StringAdditions;

public enum FilterType
{
    NONE,
    DISPLAY_NAME,
    MATERIAL,
    DATA,
    SELECTED,
    ;

    @Override
    public String toString() {
        return StringAdditions.capitalize(name());
    }
}
