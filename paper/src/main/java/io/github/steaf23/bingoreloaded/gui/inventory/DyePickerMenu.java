package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.BlockColor;
import io.github.steaf23.bingoreloaded.lib.util.StringAdditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.function.Consumer;

public class DyePickerMenu extends BasicMenu {

    private final Consumer<BlockColor> colorPickedCallback;

    public DyePickerMenu(MenuBoard manager, Consumer<BlockColor> callback) {
        super(manager, Component.text("Pick dye color"), 3);
        this.colorPickedCallback = callback;
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        super.beforeOpening(player);

        for (int i = 0; i < 9; i++) {
            addItem(BasicMenu.BLANK.copyToSlot(i, 2));
        }
        addExitAction(ItemTemplate.slotFromXY(4, 2));

        int slot = 0;
        for (BlockColor color : BlockColor.values()) {
            addAction(new ItemTemplate(slot, color.dye,
                            Component.text(StringAdditions.capitalize(color.name)).color(TextColor.color(color.textColor)).decorate(TextDecoration.BOLD)),
                    args -> {
                close(args.player());
                colorPickedCallback.accept(color);
            });
            slot++;
        }
    }
}
