package io.github.steaf23.bingoreloaded.lib.util;

import net.kyori.adventure.text.Component;

import java.util.function.Function;

public enum PlayerDisplayTranslationKey
{
    MENU_PREVIOUS,
    MENU_NEXT,
    MENU_ACCEPT,
    MENU_SAVE_EXIT,
    MENU_FILTER,
    MENU_CLEAR_FILTER;

    public static void setTranslateFunction(Function<PlayerDisplayTranslationKey, Component> translateFunction) {
        TRANSLATE_FUNCTION = translateFunction;
    }
    private static Function<PlayerDisplayTranslationKey, Component> TRANSLATE_FUNCTION = null;

    public Component translate() {
        if (TRANSLATE_FUNCTION != null) {
            return TRANSLATE_FUNCTION.apply(this);
        }
        else {
            return Component.text("ERROR LOADING TRANSLATION TEXT");
        }
    }
}
