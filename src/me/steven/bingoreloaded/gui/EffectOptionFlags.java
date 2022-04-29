package me.steven.bingoreloaded.gui;

import java.util.EnumSet;

public enum EffectOptionFlags
{
    NIGHT_VISION("Night Vision"),
    WATER_BREATHING("Water Breathing"),
    FIRE_RESISTANCE("Fire Resistance"),
    NO_FALL_DAMAGE("No Fall Damage"),
    CARD_SPEED("Speed When Holding Card");

    public final String name;

    EffectOptionFlags(String name)
    {
        this.name = name;
    }

    public static final EnumSet<EffectOptionFlags> ALL_ON = EnumSet.allOf(EffectOptionFlags.class);
    public static final EnumSet<EffectOptionFlags> ALL_OFF = EnumSet.noneOf(EffectOptionFlags.class);
}