package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;

import java.util.EnumSet;

public enum EffectOptionFlags
{
    NIGHT_VISION(BingoReloadedCore.translate("menu.effects.night_vision")),
    WATER_BREATHING(BingoReloadedCore.translate("menu.effects.water_breath")),
    FIRE_RESISTANCE(BingoReloadedCore.translate("menu.effects.fire_res")),
    NO_FALL_DAMAGE(BingoReloadedCore.translate("menu.effects.no_fall_dmg")),
    SPEED(BingoReloadedCore.translate("menu.effects.speed"));

    public final String name;

    EffectOptionFlags(String name)
    {
        this.name = name;
    }

    public static final EnumSet<EffectOptionFlags> ALL_ON = EnumSet.allOf(EffectOptionFlags.class);
    public static final EnumSet<EffectOptionFlags> ALL_OFF = EnumSet.noneOf(EffectOptionFlags.class);
}