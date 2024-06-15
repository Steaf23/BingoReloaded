package io.github.steaf23.easymenulib.inventory;

import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import io.github.steaf23.easymenulib.util.EasyMenuTranslationKey;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;

import java.util.function.Consumer;

public class UserInputMenu extends BasicMenu
{
    private final Consumer<String> resultAction;
    private String text;

    private final ItemTemplate save = new ItemTemplate(2, Material.EMERALD, ChatComponentUtils.convert(EasyMenuTranslationKey.MENU_ACCEPT.translate()));
    private final ItemTemplate clear = new ItemTemplate(1, Material.HOPPER, ChatComponentUtils.convert(EasyMenuTranslationKey.MENU_CLEAR_FILTER.translate()));

    public UserInputMenu(MenuBoard manager, String initialTitle, Consumer<String> result, String startingText) {
        super(manager, initialTitle, InventoryType.ANVIL);

        this.resultAction = result;
        this.text = "";

        addCloseAction(save);
        addAction(clear, args -> {
            text = "";
            close(args.player());
        });
    }

    public void handleTextChanged(String newText) {
        this.text = newText;
    }

    @Override
    public void beforeClosing(HumanEntity player) {
        super.beforeClosing(player);
        resultAction.accept(text);
    }
}
