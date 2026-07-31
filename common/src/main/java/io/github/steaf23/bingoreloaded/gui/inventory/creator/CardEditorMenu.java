package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.Menu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedDataMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CardEditorMenu extends PaginatedDataMenu.TextDataMenu
{
    public final String cardName;
    public final BingoCardData cardsData;
    private static final ItemTemplate ADD_LIST = new ItemTemplate(51, VanillaItems.EMERALD.type(), Component.text("Add Item List")
            .color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

    public CardEditorMenu(MenuBoard menuBoard, String cardName, BingoCardData cardsData)
    {
        super(menuBoard, Component.text("Editing '" + cardName + "'"), new ArrayList<>());
        this.cardName = cardName;
        this.cardsData = cardsData;
        addAction(ADD_LIST, arguments -> createListPicker(result -> {
            cardsData.setList(cardName, result, cardsData.lists().getTaskCount(result), 1);
        }).open(arguments.player()));
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        super.beforeOpening(player);
        updateCardDisplay();
    }

    public void updateCardDisplay() {
        setData(cardsData.getListNames(cardName));
        applyFilter(getAppliedFilter());
    }

    private BasicMenu createListPicker(Consumer<String> result) {
        return new PaginatedDataMenu.TextDataMenu(CardEditorMenu.this.getMenuBoard(), Component.text("Pick A List"), cardsData.lists().getListNames())
        {
            @Override
            public void onOptionClickedDelegate(MenuAction.ActionArguments args, String clickedOption) {
                result.accept(clickedOption);
                close(args.player());
            }

            @Override
            public ItemType itemType(String s, boolean selected) {
                return VanillaItems.BOOK.type();
            }

            @Override
            public Component displayName(String listName, boolean selected) {
                return Component.text(listName);
            }

            @Override
            public ItemTemplate editItem(ItemTemplate item, String listName, boolean selected) {
                item.setLore(
                        Component.text("This list contains " + cardsData.lists().getTaskCount(listName) + " task(s)"));
                item.addDescription("input", 1, Menu.INPUT_LEFT_CLICK.append(Component.text("select")));
                return item;
            }
        };
    }

    @Override
    public void onOptionClickedDelegate(MenuAction.ActionArguments arguments, String listName) {
        //if an ItemList attached to a card was clicked on exists
        if (listName.isEmpty()) {
            return;
        }

        if (arguments.isLeftClick())
        {
            new ListValueEditorMenu(getMenuBoard(), this, listName,
                    cardsData.getListMax(cardName, listName),
                    cardsData.getListMin(cardName, listName)).open(arguments.player());
        }
        else if (arguments.isRightClick())
        {
            cardsData.removeList(cardName, listName);
            updateCardDisplay();
        }
    }

    @Override
    public ItemType itemType(String listName, boolean selected) {
        return VanillaItems.BOOK.type();
    }

    @Override
    public Component displayName(String listName, boolean selected) {
        return Component.text(listName);
    }

    @Override
    public ItemTemplate editItem(ItemTemplate item, String listName, boolean selected) {
        int min = cardsData.getListMin(cardName, listName);
        int max = cardsData.getListMax(cardName, listName);
        item.addDescription("uses", 1,
                ComponentUtils.MINI_BUILDER.deserialize("At <bold>least</bold> <aqua>" + min + "</aqua> tasks will be used from this list."),
                ComponentUtils.MINI_BUILDER.deserialize("At <bold>most</bold> <aqua>" + max + "</aqua> tasks will be used from this list."));
        item.addDescription("input", 5,
                Menu.INPUT_LEFT_CLICK.append(Component.text("edit distribution")),
                Menu.INPUT_RIGHT_CLICK.append(Component.text("remove this list")));
        item.setAmount(max);
        return item;
    }
}
