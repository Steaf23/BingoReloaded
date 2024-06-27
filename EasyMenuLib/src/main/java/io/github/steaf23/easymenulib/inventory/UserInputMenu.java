package io.github.steaf23.easymenulib.inventory;

import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.EasyMenuTranslationKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;

import java.util.function.Consumer;

public class UserInputMenu extends BasicMenu
{
    private final Consumer<String> resultAction;
    private String text;

    private final ItemTemplate namedItem = new ItemTemplate(0, Material.NAME_TAG, null);
    private final ItemTemplate save = new ItemTemplate(2, Material.EMERALD, Component.text(EasyMenuTranslationKey.MENU_ACCEPT.translate()).color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
    private final ItemTemplate clear = new ItemTemplate(1, Material.HOPPER, Component.text(EasyMenuTranslationKey.MENU_CLEAR_FILTER.translate()).color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD));

    public UserInputMenu(MenuBoard manager, Component initialTitle, Consumer<String> result, String startingText) {
        super(manager, initialTitle, InventoryType.ANVIL);

        this.resultAction = result;
        this.text = "";

        addItem(namedItem.setName(Component.text(startingText)));
        addAction(save, args -> {
            text = ((AnvilInventory)getInventory()).getRenameText();
            close(args.player());
        });
        addAction(clear, args -> {
            text = "";
            close(args.player());
        });
    }

    /**
     * For user input text, we want to accept the text as late as possible, this will allow the following menu or receiver of this result to use it when it's ready.
     * @param arguments
     */
    @Override
    public void close(ActionArguments arguments) {
        super.close(arguments);
        resultAction.accept(text);
    }
}
