package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItem;
import io.github.steaf23.bingoreloaded.lib.api.item.VanillaItems;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListEditorMenu extends BasicMenu
{
    private final String listName;

    private static final ItemTemplate ITEMS = new ItemTemplate(2, 1, VanillaItems.APPLE.type(), BingoReloaded.applyTitleFormat("Items"), Component.text("Click to add or remove items"));
    private static final ItemTemplate ADVANCEMENTS = new ItemTemplate(4, 1, VanillaItems.ENDER_EYE.type(), BingoReloaded.applyTitleFormat("Advancements"), net.kyori.adventure.text.Component.text("Click to add or remove advancements"));
    private static final ItemTemplate STATISTICS = new ItemTemplate(6, 1, VanillaItems.GLOBE_BANNER_PATTERN.type(), BingoReloaded.applyTitleFormat("Statistics"), Component.text("Click to add or remove statistics"));
    private static final ItemTemplate TAGS = new ItemTemplate(4, 3, VanillaItems.NAME_TAG.type(), BingoReloaded.applyTitleFormat("Task Tags"), Component.text("Click to add or remove tags from tasks"));

    private static final ItemTemplate SAVE = new ItemTemplate(4, 5, VanillaItems.REDSTONE.type(), BingoMessage.MENU_SAVE_EXIT.asPhrase().color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

    public ListEditorMenu(MenuBoard manager, String listName) {
        super(manager, Component.text("Editing '" + listName + "'"), 6);
        this.listName = listName;
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
        return new StatisticSelectionMenu(menuBoard, listName);
    }

    private BasicMenu createItemPicker(MenuBoard menuBoard) {

        List<GameTask> tasks = new ArrayList<>();
        for (ItemType m : PlatformResolver.getRegistries().allItems()) {
            if (!m.isAir()) {
                tasks.add(GameTask.simpleItemTask(m, 1));
            }
        }

        return new TaskPickerMenu(menuBoard, "Select Items", tasks, listName);
    }

    private BasicMenu createAdvancementPicker(MenuBoard menuBoard) {

        PlatformServer server = menuBoard.context().server();
        List<GameTask> tasks = new ArrayList<>();
        for (AdvancementHandle advancement : server.allAdvancements()) {
            String key = advancement.key().value();
            if (key.startsWith("recipes/") || key.endsWith("/root")) {
                continue;
            }

            AdvancementTask task = new AdvancementTask(advancement);
            tasks.add(new GameTask(task));
        }

        return new TaskPickerMenu(menuBoard, "Add Advancements", tasks, listName);
    }

    private BasicMenu createTagManager(MenuBoard menuBoard) {
        return new TagManagerMenu(menuBoard, listName);
    }
}
