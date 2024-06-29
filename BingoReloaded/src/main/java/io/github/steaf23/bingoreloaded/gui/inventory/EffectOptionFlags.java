package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

import java.util.EnumSet;
import java.util.List;

public enum EffectOptionFlags
{
    NIGHT_VISION(BingoMessage.EFFECTS_NIGHT_VISION.asPhrase()),
    WATER_BREATHING(BingoMessage.EFFECTS_WATER_BREATH.asPhrase()),
    FIRE_RESISTANCE(BingoMessage.EFFECTS_FIRE_RES.asPhrase()),
    NO_FALL_DAMAGE(BingoMessage.EFFECTS_NO_FALL_DMG.asPhrase()),
    SPEED(BingoMessage.EFFECTS_SPEED.asPhrase()),
    NO_DURABILITY(BingoMessage.EFFECTS_NO_DURABILITY.asPhrase()),
    KEEP_INVENTORY(BingoMessage.EFFECTS_KEEP_INVENTORY.asPhrase());

    public final Component name;

    EffectOptionFlags(Component name)
    {
        this.name = name;
    }

    public static final EnumSet<EffectOptionFlags> ALL_ON = EnumSet.allOf(EffectOptionFlags.class);
    public static final EnumSet<EffectOptionFlags> ALL_OFF = EnumSet.noneOf(EffectOptionFlags.class);

    //FIXME: somehow create a multiline component for this
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
//            for (int effectPair = 0; effectPair < effectCount / 2.0; effectPair++) {
//                String effectNameLeft = allEffects.get(effectPair * 2).name;
//                String prefix = firstLine ? " - " : "   ";
//                if (effectCount > effectPair * 2 + 1) {
//                    String effectNameRight = allEffects.get(effectPair * 2 + 1).name;
//                    result += prefix + ChatColor.GRAY + effectNameLeft + ", " + effectNameRight + "\n";
//                } else {
//                    result += prefix + ChatColor.GRAY + effectNameLeft + "\n";
//                }
//                firstLine = false;
//            }
        }

        return result;
    }
}