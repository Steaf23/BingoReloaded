package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.gui.base.TreeMenu;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public class ListEditorUI extends TreeMenu
{
    private final String listName;

    private static final InventoryItem ITEMS = new InventoryItem(2, 2, Material.APPLE, TITLE_PREFIX + "Items", "Click to add or remove items");
    private static final InventoryItem ADVANCEMENTS = new InventoryItem(4, 2, Material.ENDER_EYE, TITLE_PREFIX + "Advancements", "Click to add or remove advancements");
    private static final InventoryItem STATISTICS = new InventoryItem(6, 2, Material.GLOBE_BANNER_PATTERN, TITLE_PREFIX + "Statistics", "Click to add or remove statistics");
    private static final InventoryItem SAVE = new InventoryItem(4, 5, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + BingoReloadedCore.translate("menu.save_exit"));
    private static final InventoryItem BLANK = new InventoryItem(Material.BLACK_STAINED_GLASS_PANE, " ", "");

    public ListEditorUI(String listName, MenuInventory parent)
    {
        super("Editing '" + listName + "'", parent);
        this.listName = listName;
        addMenuOption(ITEMS, createItemPicker());
        addMenuOption(ADVANCEMENTS, createAdvancementPicker());
        addMenuOption(STATISTICS, createStatisticsPicker());
        fillOptions(BLANK.inSlot(0, 5),
                BLANK.inSlot(1, 5),
                BLANK.inSlot(2, 5),
                BLANK.inSlot(3, 5),
                SAVE,
                BLANK.inSlot(5, 5),
                BLANK.inSlot(6, 5),
                BLANK.inSlot(7, 5),
                BLANK.inSlot(8, 5));
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == SAVE.getSlot())
        {
            close(player);
        }
        super.delegateClick(event, slotClicked, player, clickType);
    }

    public MenuInventory createStatisticsPicker()
    {
        MenuInventory statistics = new StatisticPickerUI(this, listName);
        return statistics;
    }

    private static List<InventoryItem> getItemOptions()
    {
        return new ArrayList<>();
    }

    private MenuInventory createItemPicker()
    {
        Set<Material> glassPanes = new HashSet<>();
        for (FlexColor flexColor : FlexColor.values())
        {
            glassPanes.add(flexColor.glassPane);
        }

        List<BingoTask> tasks = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && !glassPanes.contains(m) && m.isItem() && !m.isAir())
            {
                tasks.add(new BingoTask(new ItemTask(m, 1)));
            }
        }

        TaskPickerUI itemPicker = new TaskPickerUI(tasks,"Select Items", this, listName);
        return itemPicker;
    }

    private MenuInventory createAdvancementPicker()
    {
        List<BingoTask> tasks = new ArrayList<>();
        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); )
        {
            Advancement a = it.next();
            String key = a.getKey().getKey();
            if (key.startsWith("recipes/") || key.endsWith("/root"))
            {
                continue;
            }

            AdvancementTask task = new AdvancementTask(a);
            tasks.add(new BingoTask(task));
        }

        TaskPickerUI advancementPicker = new TaskPickerUI(tasks, "Add Advancements", this, listName);
        return advancementPicker;
    }
}
