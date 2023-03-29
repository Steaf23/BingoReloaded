package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoReloaded;

import java.util.EnumSet;

public enum EffectOptionFlags
{
    NIGHT_VISION(BingoReloaded.translate("menu.effects.night_vision")),
    WATER_BREATHING(BingoReloaded.translate("menu.effects.water_breath")),
    FIRE_RESISTANCE(BingoReloaded.translate("menu.effects.fire_res")),
    NO_FALL_DAMAGE(BingoReloaded.translate("menu.effects.no_fall_dmg")),
    SPEED(BingoReloaded.translate("menu.effects.speed"));

    public final String name;

    EffectOptionFlags(String name)
    {
        this.name = name;
    }

    public static final EnumSet<EffectOptionFlags> ALL_ON = EnumSet.allOf(EffectOptionFlags.class);
    public static final EnumSet<EffectOptionFlags> ALL_OFF = EnumSet.noneOf(EffectOptionFlags.class);
}