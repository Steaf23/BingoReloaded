package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import net.md_5.bungee.api.ChatColor;

import java.util.EnumSet;
import java.util.List;

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

    public static String effectsToString(EnumSet<EffectOptionFlags> effects) {
        String result = "";
        if (effects.size() == 0)
        {
            result = ChatColor.GRAY + "None";
        }
        else
        {
            result = "\n";
            // Display effects in pairs of 2 per line to save space
            int effectIdx = 0;
            List<EffectOptionFlags> allEffects = effects.stream().toList();
            int effectCount = allEffects.size();

            boolean firstLine = true;
            for (int effectPair = 0; effectPair < effectCount / 2.0; effectPair++) {
                String effectNameLeft = allEffects.get(effectPair * 2).name;
                String prefix = firstLine ? " - " : "   ";
                if (effectCount > effectPair * 2 + 1) {
                    String effectNameRight = allEffects.get(effectPair * 2 + 1).name;
                    result += prefix + ChatColor.GRAY + effectNameLeft + ", " + effectNameRight + "\n";
                } else {
                    result += prefix + ChatColor.GRAY + effectNameLeft + "\n";
                }
                firstLine = false;
            }
        }

        return result;
    }
}