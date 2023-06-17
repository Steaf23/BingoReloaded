package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;

import java.util.EnumSet;

public enum EffectOptionFlags
{
    NIGHT_VISION(BingoTranslation.EFFECTS_NIGHT_VISION.translate()),
    WATER_BREATHING(BingoTranslation.EFFECTS_WATER_BREATH.translate()),
    FIRE_RESISTANCE(BingoTranslation.EFFECTS_FIRE_RES.translate()),
    NO_FALL_DAMAGE(BingoTranslation.EFFECTS_NO_FALL_DMG.translate()),
    SPEED(BingoTranslation.EFFECTS_SPEED.translate()),
    NO_DURABILITY(BingoTranslation.EFFECTS_NO_DURABILITY.translate()),
    KEEP_INVENTORY(BingoTranslation.EFFECTS_KEEP_INVENTORY.translate());

    public final String name;

    EffectOptionFlags(String name)
    {
        this.name = name;
    }

    public static final EnumSet<EffectOptionFlags> ALL_ON = EnumSet.allOf(EffectOptionFlags.class);
    public static final EnumSet<EffectOptionFlags> ALL_OFF = EnumSet.noneOf(EffectOptionFlags.class);
}