package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoTasksData;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.ListPickerUI;
import io.github.steaf23.bingoreloaded.item.ItemTextBuilder;
import io.github.steaf23.bingoreloaded.item.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.item.AdvancementListItem;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ListEditorUI
{
    public final AbstractGUIInventory parent;

    private ListPickerUI items;
    private ListPickerUI advancements;
    private final String listName;

    public ListEditorUI(String listName, AbstractGUIInventory parent)
    {
        this.parent = parent;
        this.listName = listName;
    }

    public void openItemPicker(Player player)
    {
        if (advancements != null)
            advancements.close(player);
        items = createItemPicker();
        items.open(player);
    }

    public void openAdvancementPicker(Player player)
    {
        if (items != null)
            items.close(player);
        advancements = createAdvancementPicker();
        advancements.open(player);
    }

    private static List<InventoryItem> getItemOptions()
    {
        return new ArrayList<>();
    }

    private ListPickerUI createItemPicker()
    {
        List<Material> glassPanes = new ArrayList<>();
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            glassPanes.add(flexColor.glassPane);
        }

        List<InventoryItem> items = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && m.isItem() && !m.isAir() && !glassPanes.contains(m))
            {
                InventoryItem item = new InventoryItem(m, "");
                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;

                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

                item.setItemMeta(meta);
                new ItemTextBuilder(ChatColor.YELLOW)
                        .translate(ItemTextBuilder.getItemKey(m))
                        .buildName(item);
                items.add(item);
            }
        }

        ListPickerUI itemPicker = new ListPickerUI(items,"Managing items", parent, FilterType.MATERIAL)
        {
            private static final InventoryItem ADVANCEMENTS = new InventoryItem(51, Material.ENDER_EYE, TITLE_PREFIX + "Advancements", "Click to add or remove advancements");

            @Override
            public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
            {
                if (slotClicked == ADVANCEMENTS.getSlot())
                {
                    openAdvancementPicker(player);
                }
                else
                {
                    super.delegateClick(event, slotClicked, player, clickType);
                }
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                if (event.getClick().isLeftClick())
                {
                    incrementItemCount(clickedOption);
                }
                else if (event.getClick().isRightClick())
                {
                    decrementItemCount(clickedOption);
                }
            }

            @Override
            public void open(HumanEntity player)
            {
                fillOptions(new InventoryItem[]{ADVANCEMENTS});
                loadSelectedItems();
                super.open(player);
            }

            @Override
            public void close(HumanEntity player)
            {
                List<ItemTask> tasks = new ArrayList<>();
                getSelectedItems().forEach((item) -> {
                    ItemTask newItem = new ItemTask(item.getType(), item.getAmount());
                    tasks.add(newItem);
                });
                BingoTasksData.saveItemTasks(listName, tasks.toArray(ItemTask[]::new));
                super.close(player);
            }

            public void incrementItemCount(InventoryItem item)
            {
                boolean select = false;
                if (!getSelectedItems().contains(item))
                {
                    select = true;
                }

                if (!select)
                {
                    item.setAmount(Math.min(item.getMaxStackSize(), item.getAmount() + 1));
                }

                if (select)
                {
                    selectItem(item,true);
                }
                updatePage();
            }

            public void decrementItemCount(InventoryItem item)
            {
                boolean deselect = false;
                if (getSelectedItems().contains(item))
                {
                    if (item.getAmount() == 1)
                    {
                        deselect = true;
                    }
                }
                item.setAmount(Math.max(1, item.getAmount() - 1));

                if (deselect)
                {
                    selectItem(item, false);
                }
                updatePage();
            }

            private void loadSelectedItems()
            {
                List<ItemTask> items = BingoTasksData.getItemTasks(listName);
                List<InventoryItem> allItems = getItems();

                items.forEach(task -> {
                    String mat = task.item.getType().name();
                    Optional<InventoryItem> item = allItems.stream().filter((i) -> i.getType().name().equals(mat)).findFirst();
                    item.ifPresent(inventoryItem -> {
                        selectItem(inventoryItem, true);
                        inventoryItem.setAmount(task.getCount());
                    });
                });
                updatePage();
            }

            @Override
            public void handleClose(InventoryCloseEvent event)
            {
                List<ItemTask> tasks = new ArrayList<>();
                getSelectedItems().forEach((item) -> {
                    ItemTask newItem = new ItemTask(item.getType(), item.getAmount());
                    tasks.add(newItem);
                });
                BingoTasksData.saveItemTasks(listName, tasks.toArray(ItemTask[]::new));
                super.handleClose(event);
            }
        };
        return itemPicker;
    }

    private ListPickerUI createAdvancementPicker()
    {
        List<InventoryItem> options = new ArrayList<>();
        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); )
        {
            Advancement a = it.next();
            String key = a.getKey().getKey();
            if (key.startsWith("recipes/") || key.endsWith("/root"))
            {
                continue;
            }
            InventoryItem item = new AdvancementListItem(a);
            new ItemTextBuilder(ChatColor.DARK_PURPLE)
                    .translate(ItemTextBuilder.getAdvancementDescKey(a))
                    .text("" + ChatColor.GRAY + "Click to make this item\n appear on bingo cards")
                    .buildDescription(item);
            new ItemTextBuilder(ChatColor.AQUA, "italic")
                    .text("[")
                    .translate(ItemTextBuilder.getAdvancementTitleKey(a))
                    .text("]")
                    .buildName(item);
            options.add(item);
        }

        ListPickerUI advancementPicker = new ListPickerUI(options, "Add Advancements", parent, FilterType.DISPLAY_NAME)
        {
            private static final InventoryItem ITEMS = new InventoryItem(51, Material.APPLE, TITLE_PREFIX + "Items", "Click to add or remove items");

            @Override
            public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
            {
                if (slotClicked == ITEMS.getSlot())
                {
                    openItemPicker(player);
                }
                else
                {
                    super.delegateClick(event, slotClicked, player, clickType);
                }
            }

            @Override
            public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
            {
                selectItem(clickedOption, !getSelectedItems().contains(clickedOption));
            }

            @Override
            public void open(HumanEntity player)
            {
                fillOptions(new InventoryItem[]{ITEMS});
                loadSelectedItems();
                super.open(player);
            }

            @Override
            public void close(HumanEntity player)
            {
                List<AdvancementTask> tasks = new ArrayList<>();
                getSelectedItems().forEach((item) -> {
                    if ((item instanceof AdvancementListItem advItem))
                    {
                        AdvancementTask slot = new AdvancementTask(advItem.advancement);
                        tasks.add(slot);
                    }
                });
                BingoTasksData.saveAdvancementTasks(listName, tasks.toArray(AdvancementTask[]::new));
                super.close(player);
            }

            private void loadSelectedItems()
            {
                List<AdvancementTask> advancementTasks = BingoTasksData.getAdvancementTasks(listName);
                List<AdvancementListItem> allItems = new ArrayList<>();
                for (InventoryItem item : getItems())
                {
                    if (item instanceof AdvancementListItem advItem)
                        allItems.add(advItem);
                }

                advancementTasks.forEach(task -> {
                    String key = task.advancement.getKey().toString();
                    Optional<AdvancementListItem> item = allItems.stream().filter((adv) -> adv.advancement.getKey().toString().equals(key)).findFirst();
                    item.ifPresent(advancementItem -> {
                        selectItem(advancementItem, true);
                    });
                });
                updatePage();
            }

            @Override
            public void handleClose(InventoryCloseEvent event)
            {
                List<AdvancementTask> tasks = new ArrayList<>();
                getSelectedItems().forEach((item) -> {
                    if ((item instanceof AdvancementListItem advItem))
                    {
                        AdvancementTask slot = new AdvancementTask(advItem.advancement);
                        tasks.add(slot);
                    }
                });
                BingoTasksData.saveAdvancementTasks(listName, tasks.toArray(AdvancementTask[]::new));
                super.handleClose(event);
            }
        };
        return advancementPicker;
    }
}
