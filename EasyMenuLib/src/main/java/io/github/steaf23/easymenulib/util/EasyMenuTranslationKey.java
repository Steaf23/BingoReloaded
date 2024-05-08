package io.github.steaf23.easymenulib.util;

import io.github.steaf23.easymenulib.EasyMenuLibrary;

public enum EasyMenuTranslationKey
{
    MENU_PREVIOUS,
    MENU_NEXT,
    MENU_ACCEPT,
    MENU_SAVE_EXIT,
    MENU_FILTER,
    MENU_CLEAR_FILTER;

    public String translate() {
        return EasyMenuLibrary.translateKey(this);
    }
}
