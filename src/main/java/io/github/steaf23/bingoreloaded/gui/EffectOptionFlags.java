package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.TranslationData;

import java.util.EnumSet;

public enum EffectOptionFlags
{
    NIGHT_VISION(TranslationData.translate("menu.effects.night_vision")),
    WATER_BREATHING(TranslationData.translate("menu.effects.water_breath")),
    FIRE_RESISTANCE(TranslationData.translate("menu.effects.fire_res")),
    NO_FALL_DAMAGE(TranslationData.translate("menu.effects.no_fall_dmg")),
    CARD_SPEED(TranslationData.translate("menu.effects.card_speed"));

    public final String name;

    EffectOptionFlags(String name)
    {
        this.name = name;
    }

    public static final EnumSet<EffectOptionFlags> ALL_ON = EnumSet.allOf(EffectOptionFlags.class);
    public static final EnumSet<EffectOptionFlags> ALL_OFF = EnumSet.noneOf(EffectOptionFlags.class);
}