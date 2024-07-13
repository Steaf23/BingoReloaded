package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.CustomTextureData;
import io.github.steaf23.bingoreloaded.gui.inventory.core.TexturedTitleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SlotBackgroundRenderer
{
    public static final int SLOT_TEXTURE_WIDTH = 18;
    /**
     * Converts input slots to title component that can be used to highlight certain slots in an inventory.
     * @param slots set of raw slot numbers to add a background to.
     * @return created component based on the input slots that should be put in front of any actual title text.
     */
    public static Component slotCompletedBackground(Map<Integer, TextColor> slots) {
        TexturedTitleBuilder result = new TexturedTitleBuilder();

        //TODO: move spacing optimization to title builder
        int spaces = 0;

        for (int y = 0; y < 6; y++) {
            CustomTextureData.Texture slotTexture = getTextureForRow(y);
            for (int x = 0; x < 9; x++) {
                int index = x + (y * 9);
                if (!slots.containsKey(index)) {
                    spaces += SLOT_TEXTURE_WIDTH;
                }
                else {
                    result.addSpace(spaces + slotTexture.menuOffset())
                            .addTexture(slotTexture, slots.get(index));
                    spaces = -4;
                }
            }
            spaces = 0;
            result.resetSpace();
        }
        return result.build();
    }

    public static CustomTextureData.Texture getTextureForRow(int row) {
        return BingoReloaded.getInstance().getTextureData().getTexture("slot_row_" + row);
    }
}
