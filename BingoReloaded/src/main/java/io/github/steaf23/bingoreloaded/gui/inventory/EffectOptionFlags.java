package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
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

    public static Component[] effectsToText(EnumSet<EffectOptionFlags> effects) {
        List<Component> result = new ArrayList<>();
        if (effects.isEmpty())
        {
            result.add(Component.text("None", NamedTextColor.GRAY));
        }
        else
        {
            // Display effects in pairs of 2 per line to save space
            List<EffectOptionFlags> allEffects = effects.stream().toList();
            int effectCount = allEffects.size();

            boolean firstLine = true;
            for (int effectPair = 0; effectPair < effectCount / 2.0; effectPair++) {
                Component effectNameLeft = allEffects.get(effectPair * 2).name;
                Component prefix = Component.text(firstLine ? " - " : "   ");
                if (effectCount > effectPair * 2 + 1) {
                    Component effectNameRight = allEffects.get(effectPair * 2 + 1).name;
                    result.add(prefix.append(effectNameLeft.color(NamedTextColor.GRAY).append(Component.text(", ")).append(effectNameRight)));
                } else {
                    result.add(prefix.append(effectNameLeft.color(NamedTextColor.GRAY)));
                }
                firstLine = false;
            }
        }

        return result.toArray(Component[]::new);
    }
}