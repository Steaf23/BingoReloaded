package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.helper.TaskFormatting;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ListEditorMenu extends BasicMenu
{
    private final String listName;
    private final TaskFormatting formatting;

    private static final ItemTemplate ITEMS = new ItemTemplate(2, 1, VanillaItems.APPLE.type(), BingoReloaded.applyTitleFormat("Items"), Component.text("Click to add or remove items"));
    private static final ItemTemplate ADVANCEMENTS = new ItemTemplate(4, 1, VanillaItems.ENDER_EYE.type(), BingoReloaded.applyTitleFormat("Advancements"), net.kyori.adventure.text.Component.text("Click to add or remove advancements"));
    private static final ItemTemplate STATISTICS = new ItemTemplate(6, 1, VanillaItems.GLOBE_BANNER_PATTERN.type(), BingoReloaded.applyTitleFormat("Statistics"), Component.text("Click to add or remove statistics"));
    private static final ItemTemplate TAGS = new ItemTemplate(4, 3, VanillaItems.NAME_TAG.type(), BingoReloaded.applyTitleFormat("Task Tags"), Component.text("Click to add or remove tags from tasks"));

    private static final ItemTemplate SAVE = new ItemTemplate(4, 5, VanillaItems.REDSTONE.type(), BingoMessage.MENU_SAVE_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

    public ListEditorMenu(MenuBoard manager, String listName, TaskFormatting formatting) {
        super(manager, Component.text("Editing '" + listName + "'"), 6);
        this.listName = listName;
        this.formatting = formatting;
        addAction(ITEMS, arguments -> createItemPicker(manager).open(arguments.player()));
        addAction(ADVANCEMENTS, arguments -> createAdvancementPicker(manager).open(arguments.player()));
        addAction(STATISTICS, arguments -> createStatisticsPicker(manager).open(arguments.player()));
        addAction(TAGS, args -> createTagManager(manager).open(args.player()));
        addCloseAction(SAVE);
        addItems(BLANK.copyToSlot(0, 5),
                BLANK.copyToSlot(1, 5),
                BLANK.copyToSlot(2, 5),
                BLANK.copyToSlot(3, 5),
                BLANK.copyToSlot(5, 5),
                BLANK.copyToSlot(6, 5),
                BLANK.copyToSlot(7, 5),
                BLANK.copyToSlot(8, 5));
    }

    public BasicMenu createStatisticsPicker(MenuBoard menuBoard) {
        return new StatisticSelectionMenu(menuBoard, listName, formatting);
    }

    private BasicMenu createItemPicker(MenuBoard menuBoard) {
        return new TaskPickerMenu(menuBoard, "Select Items", Tasks.allItems().stream().map(GameTask::new).toList(), listName, formatting);
    }

    private BasicMenu createAdvancementPicker(MenuBoard menuBoard) {
        PlatformServer server = menuBoard.context().server();
        return new TaskPickerMenu(menuBoard, "Add Advancements", Tasks.allAdvancementTasks(server).stream().map(GameTask::new).toList(), listName, formatting);
    }

    private BasicMenu createTagManager(MenuBoard menuBoard) {
        return new TagManagerMenu(menuBoard, listName);
    }
}
