package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class PaginatedPickerUI extends AbstractGUIInventory
{
    /**
     * Called by this Inventory's ClickEvents.
     * @param event
     * @param clickedOption item that was clicked on, it's slot being the same slot that was clicked on.
     * @param player
     */
    public abstract void onOptionClickedDelegate(final InventoryClickEvent event, InventoryItem clickedOption, Player player);

    public static final int ITEMS_PER_PAGE = 45;

    // All the items that exist in this picker
    private final List<InventoryItem> items;

    // All selected items in this picker
    private final List<InventoryItem> selectedItems;

    // All items that pass the filter, these are always the items shown to the player
    private final List<InventoryItem> filteredItems;
    private int pageAmount;
    private int currentPage;
    private UserInputUI filter;
    private String keywordFilter;
    public FilterType filterType;

    protected static final InventoryItem BG_ITEM = new InventoryItem(Material.BLACK_STAINED_GLASS_PANE, " ", "");
    protected static final InventoryItem NEXT = new InventoryItem(53, Material.STRUCTURE_VOID, "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + TranslationData.translate("menu.next"), "");
    protected static final InventoryItem PREVIOUS = new InventoryItem(45, Material.BARRIER, "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + TranslationData.translate("menu.prev"), "");
    protected static final InventoryItem CLOSE = new InventoryItem(49, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + TranslationData.translate("menu.save_exit"), "");
    protected static final InventoryItem FILTER = new InventoryItem(46, Material.SPYGLASS, TITLE_PREFIX + TranslationData.translate("menu.filter"), "");

    public PaginatedPickerUI(List<InventoryItem> options, String title, AbstractGUIInventory parent, FilterType filterType)
    {
        super(54, title != null ? title : "Item Picker", parent);

        fillOptions(PREVIOUS,
                FILTER,
                BG_ITEM.inSlot(47),
                BG_ITEM.inSlot(48),
                CLOSE,
                BG_ITEM.inSlot(50),
                BG_ITEM.inSlot(51),
                BG_ITEM.inSlot(52),
                NEXT
        );

        currentPage = 0;
        items = options;
        selectedItems = new ArrayList<>();
        filteredItems = new ArrayList<>(options);
        keywordFilter = "";
        this.filterType = filterType;
        clearFilter();
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == NEXT.getSlot())
        {
            nextPage();
        }
        else if (slotClicked == PREVIOUS.getSlot())
        {
            previousPage();
        }
        else if (slotClicked == CLOSE.getSlot())
        {
            close(player);
        }
        else if (slotClicked == FILTER.getSlot())
        {
            UserInputUI.open("Filter by name", this::applyFilter, player, this, keywordFilter);
        }
        else if (isSlotValidOption(slotClicked)) //If it is a normal item;
        {
            onOptionClickedDelegate(event, filteredItems.get(ITEMS_PER_PAGE * currentPage + slotClicked).inSlot(slotClicked), player);
        }
    }

    public void applyFilter(String filter)
    {
        keywordFilter = filter;
        InventoryItem filterItem = FILTER.copy();
        ItemMeta meta = filterItem.getItemMeta();
        meta.setLore(List.of("\"" + keywordFilter + "\""));
        filterItem.setItemMeta(meta);
        addOption(filterItem);

        filteredItems.clear();

        switch (filterType)
        {
            case MATERIAL:
                for (InventoryItem item : items)
                {
                    String name = item.getType().name().replace("_", " ");
                    if (name.toLowerCase().contains(keywordFilter.toLowerCase()))
                    {
                        filteredItems.add(item);
                    }
                }
                break;
            case DISPLAY_NAME:
                for (InventoryItem item : items)
                {
                    if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase().contains(keywordFilter.toLowerCase()))
                    {
                        filteredItems.add(item);
                    }
                }
                break;
            case LORE:
                for (InventoryItem item : items)
                {
                    if (ChatColor.stripColor(item.getItemMeta().getLore().get(0)).toLowerCase().contains(keywordFilter.toLowerCase()))
                    {
                        filteredItems.add(item);
                    }
                }
                break;
            case CUSTOM:
                for (InventoryItem item : items)
                {
                    if (passesFilter(item))
                    {
                        filteredItems.add(item);
                    }
                }
                break;
        }

        updatePageAmount();
        updatePage();
    }

    public void clearFilter()
    {
        applyFilter("");
    }

    /**
     * Implement this method if filterType is set to CUSTOM
     * @param item
     * @return Whether the item should be shown when this filter is applied.
     */
    public boolean passesFilter(InventoryItem item)
    {
        return false;
    }

    public String getFilter()
    {
        return keywordFilter;
    }

    protected boolean isSlotValidOption(int slot)
    {
        return ITEMS_PER_PAGE * currentPage + slot < filteredItems.size();
    }

    public void addItems(InventoryItem... newItems)
    {
        //first remove any previous whitespace
        while (items.size() > 0)
        {
            InventoryItem lastItem = items.get(items.size() - 1);

            if (lastItem.getType().isAir())
                items.remove(lastItem);
            else

                break;
        }

        Collections.addAll(items, newItems);
        clearFilter();
    }

    public void removeItems(int... itemIndices)
    {
        //first remove any previous whitespace
        while (items.size() > 0)
        {
            InventoryItem lastItem = items.get(items.size() - 1);

            if (lastItem.getType().isAir())
                items.remove(lastItem);
            else
                break;
        }
        for (int i : itemIndices)
            items.remove(i);

        updatePage();
    }

    public void clearItems()
    {
        items.clear();
        updatePage();
    }

    public List<InventoryItem> getItems()
    {
        return items;
    }

    public List<InventoryItem> getSelectedItems()
    {
        return selectedItems;
    }

    public void selectItem(InventoryItem item, boolean value)
    {
        if (!items.contains(item))
        {
            return;
        }

        if (value)
        {
            selectedItems.add(item);
        }
        else
        {
            selectedItems.remove(item);
        }

        item.highlight(value);

        items.set(items.indexOf(item), item);
        updatePage();
    }

    protected void nextPage()
    {
        updatePageAmount();
        currentPage = Math.floorMod(currentPage + 1, pageAmount);
        updatePage();
    }

    protected void previousPage()
    {
        updatePageAmount();
        currentPage = Math.floorMod(currentPage - 1, pageAmount);
        updatePage();
    }

    protected void updatePage()
    {
        updatePageAmount();

        int startingIndex = currentPage * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++)
        {
            if (startingIndex + i < filteredItems.size())
                addOption(filteredItems.get(startingIndex + i).inSlot(i));
            else
                addOption(new InventoryItem(i, Material.AIR, "", ""));
        }

        //Update Page description e.g. (20/23) for the Next and Previous 'buttons'.
        String pageCountDesc = String.format("%02d", currentPage + 1) + "/" + String.format("%02d", pageAmount);

        InventoryItem next = getOption(NEXT.getSlot());
        ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta != null)
        {
            nextMeta.setLore(List.of(pageCountDesc));
        }
        next.setItemMeta(nextMeta);
        addOption(next);

        InventoryItem previous = getOption(PREVIOUS.getSlot());
        ItemMeta prevMeta = previous.getItemMeta();
        if (prevMeta != null)
        {
            prevMeta.setLore(List.of(pageCountDesc));
        }
        previous.setItemMeta(prevMeta);
        addOption(previous);
    }

    private void updatePageAmount()
    {
        pageAmount = Math.max(1, (int)Math.ceil(filteredItems.size() / (double)ITEMS_PER_PAGE));
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    //

    /**
     * Replaces the item in the given slot at the current page to the new item. Keeps the item's selection status.
     * @param newItem
     * @param slot
     */
    public void replaceItem(InventoryItem newItem, int slot)
    {
        InventoryItem oldItem = filteredItems.get(ITEMS_PER_PAGE * currentPage + slot);
        replaceItem(newItem, oldItem);
    }

    public void replaceItem(InventoryItem newItem, InventoryItem oldItem)
    {
        if (!items.contains(oldItem))
        {
            return;
        }

        int idx = items.indexOf(oldItem);
        items.set(items.indexOf(oldItem), newItem);
        filteredItems.set(filteredItems.indexOf(oldItem), newItem);

        if (selectedItems.contains(oldItem))
        {
            selectedItems.remove(oldItem);
        }
    }
}
