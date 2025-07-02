package io.github.steaf23.bingoreloaded.lib.util;

import io.github.steaf23.bingoreloaded.lib.PlayerDisplay;
import net.kyori.adventure.text.Component;

public enum PlayerDisplayTranslationKey
{
    MENU_PREVIOUS,
    MENU_NEXT,
    MENU_ACCEPT,
    MENU_SAVE_EXIT,
    MENU_FILTER,
    MENU_CLEAR_FILTER;

    public Component translate() {
        return PlayerDisplay.translateKey(this);
    }
}
