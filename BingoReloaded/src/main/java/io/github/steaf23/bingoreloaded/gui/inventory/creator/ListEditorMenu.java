package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.BlockColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;

import java.util.*;

public class ListEditorMenu extends BasicMenu
{
    private final String listName;

    private static final ItemTemplate ITEMS = new ItemTemplate(2, 2, Material.APPLE, BasicMenu.applyTitleFormat("Items"), Component.text("Click to add or remove items"));
    private static final ItemTemplate ADVANCEMENTS = new ItemTemplate(4, 2, Material.ENDER_EYE, BasicMenu.applyTitleFormat("Advancements"), Component.text("Click to add or remove advancements"));
    private static final ItemTemplate STATISTICS = new ItemTemplate(6, 2, Material.GLOBE_BANNER_PATTERN, BasicMenu.applyTitleFormat("Statistics"), Component.text("Click to add or remove statistics"));
    private static final ItemTemplate SAVE = new ItemTemplate(4, 5, Material.REDSTONE, Component.text(BingoTranslation.MENU_SAVE_EXIT.translate()).color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

    public ListEditorMenu(MenuBoard manager, String listName) {
        super(manager, Component.text("Editing '" + listName + "'"), 6);
        this.listName = listName;
        addAction(ITEMS, p -> createItemPicker(manager).open(p));
        addAction(ADVANCEMENTS, p -> createAdvancementPicker(manager).open(p));
        addAction(STATISTICS, p -> createStatisticsPicker(manager).open(p));
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
        Set<Material> glassPanes = new HashSet<>();
        for (BlockColor blockColor : BlockColor.values()) {
            glassPanes.add(blockColor.glassPane);
        }

        List<BingoTask> tasks = new ArrayList<>();
        for (Material m : Material.values()) {
            if (!m.name().contains("LEGACY_") && !glassPanes.contains(m) && m.isItem() && !m.isAir()) {
                tasks.add(new BingoTask(new ItemTask(m, 1)));
            }
        }

        return new TaskPickerMenu(menuBoard, "Select Items", tasks, listName);
    }

    private BasicMenu createAdvancementPicker(MenuBoard menuBoard) {
        List<BingoTask> tasks = new ArrayList<>();
        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); ) {
            Advancement a = it.next();
            String key = a.getKey().getKey();
            if (key.startsWith("recipes/") || key.endsWith("/root")) {
                continue;
            }

            AdvancementTask task = new AdvancementTask(a);
            tasks.add(new BingoTask(task));
        }

        return new TaskPickerMenu(menuBoard, "Add Advancements", tasks, listName);
    }
}
