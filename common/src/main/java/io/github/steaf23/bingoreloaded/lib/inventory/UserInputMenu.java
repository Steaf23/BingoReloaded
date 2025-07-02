package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.PlayerDisplayTranslationKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.function.Consumer;

public class UserInputMenu extends BasicMenu
{
    private final Consumer<String> resultAction;
    private String text;

    private final ItemTemplate save = new ItemTemplate(2, ItemType.of("emerald"), PlayerDisplayTranslationKey.MENU_ACCEPT.translate().color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
    private static final ItemTemplate CLEAR = new ItemTemplate(1, ItemType.of("hopper"), PlayerDisplayTranslationKey.MENU_CLEAR_FILTER.translate().color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD));

    public UserInputMenu(MenuBoard manager, Component initialTitle, Consumer<String> result, String startingText) {
        super(manager, initialTitle, InventoryType.ANVIL);

        this.resultAction = result;
        this.text = "";

        addItem(new ItemTemplate(0, ItemType.of("name_tag"), Component.text(startingText)));
        addCloseAction(save);
        addAction(CLEAR.copy(), args -> {
            text = "";
            close(args.player());
        });
    }

    public void handleTextChanged(String newText) {
        this.text = newText;
        addItem(save.setLore(Component.text(text)));
    }

    /**
     * For user input text, we want to accept the text as late as possible, this will allow the following menu or receiver of this result to use it when it's ready.
     */
    @Override
    public void close(PlayerHandle player) {
        super.close(player);
        resultAction.accept(text);
    }
}