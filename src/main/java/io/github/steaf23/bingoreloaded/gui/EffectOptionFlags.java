package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.data.TranslationData;

import java.util.EnumSet;

public enum EffectOptionFlags
{
    NIGHT_VISION(BingoReloaded.data().translationData.translate("menu.effects.night_vision")),
    WATER_BREATHING(BingoReloaded.data().translationData.translate("menu.effects.water_breath")),
    FIRE_RESISTANCE(BingoReloaded.data().translationData.translate("menu.effects.fire_res")),
    NO_FALL_DAMAGE(BingoReloaded.data().translationData.translate("menu.effects.no_fall_dmg")),
    SPEED(BingoReloaded.data().translationData.translate("menu.effects.speed"));

    public final String name;

    EffectOptionFlags(String name)
    {
        this.name = name;
    }

    public static final EnumSet<EffectOptionFlags> ALL_ON = EnumSet.allOf(EffectOptionFlags.class);
    public static final EnumSet<EffectOptionFlags> ALL_OFF = EnumSet.noneOf(EffectOptionFlags.class);
}