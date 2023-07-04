package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class PaginatedSelectionMenu extends BasicMenu
{
    /**
     * Called by this Inventory's ClickEvents.
     *
     * @param event
     * @param clickedOption item that was clicked on, it's slot being the same slot that was clicked on.
     * @param player
     */
    public abstract void onOptionClickedDelegate(final InventoryClickEvent event, MenuItem clickedOption, HumanEntity player);

    // There are 5 rows of items per page
    public static final int ITEMS_PER_PAGE = 9 * 5;

    // All the items that exist in this picker
    private final List<MenuItem> items;

    // All selected items in this picker
    private final List<MenuItem> selectedItems;

    private Function<MenuItem, Boolean> customFilter;

    // All items that pass the filter, these are always the items shown to the player
    private final List<MenuItem> filteredItems;
    private int pageAmount;
    private int currentPage;
    private String keywordFilter;
    public FilterType filterType;

    protected static final MenuItem NEXT = new MenuItem(8, 5, Material.STRUCTURE_VOID, "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + BingoTranslation.MENU_NEXT.translate(), "");
    protected static final MenuItem PREVIOUS = new MenuItem(0, 5, Material.BARRIER, "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + BingoTranslation.MENU_PREV.translate(), "");
    protected static final MenuItem CLOSE = new MenuItem(4, 5, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + BingoTranslation.MENU_SAVE_EXIT.translate(), "");
    protected static final MenuItem FILTER = new MenuItem(1, 5, Material.SPYGLASS, TITLE_PREFIX + BingoTranslation.MENU_FILTER.translate(), "");

    public PaginatedSelectionMenu(MenuManager manager, String initialTitle, List<MenuItem> options, Function<MenuItem, Boolean> customFilter) {
        this(manager, initialTitle, options, FilterType.CUSTOM);
        this.customFilter = customFilter;
    }

    public PaginatedSelectionMenu(MenuManager manager, String initialTitle, List<MenuItem> options, FilterType filterType) {
        super(manager, initialTitle, 6);

        addAction(PREVIOUS, p -> this.previousPage());
        addAction(FILTER, p -> {
            new UserInputMenu(manager, "Filter by name", this::applyFilter, p, keywordFilter.isBlank() ? "name" : keywordFilter);
        });
        addAction(NEXT, p -> this.nextPage());

        addItems(
                BLANK.copyToSlot(2, 5),
                BLANK.copyToSlot(3, 5),
                BLANK.copyToSlot(5, 5),
                BLANK.copyToSlot(6, 5),
                BLANK.copyToSlot(7, 5)
        );
        addCloseAction(CLOSE);

        currentPage = 0;
        items = options;
        selectedItems = new ArrayList<>();
        filteredItems = new ArrayList<>(options);
        keywordFilter = "";
        this.filterType = filterType;
        clearFilter();
    }

    @Override
    public boolean onClick(InventoryClickEvent event, HumanEntity player, MenuItem clickedItem, ClickType clickType) {
        boolean cancel = super.onClick(event, player, clickedItem, clickType);

        boolean isValidSlot = ITEMS_PER_PAGE * currentPage + event.getRawSlot() < filteredItems.size() && event.getRawSlot() < ITEMS_PER_PAGE;
        if (isValidSlot)
        {
            onOptionClickedDelegate(event, clickedItem.copyToSlot(event.getRawSlot()), player);
        }
        return cancel;
    }

    public void applyFilter(String filter) {
        keywordFilter = filter;
        MenuItem filterItem = getItemAt(FILTER.getSlot());
        ItemMeta meta = filterItem.getItemMeta();
        meta.setLore(List.of("\"" + keywordFilter + "\""));
        filterItem.setItemMeta(meta);
        updateActionItem(filterItem);

        filteredItems.clear();

        Function<MenuItem, Boolean> filterCriteria;

        filterCriteria =
                switch (filterType) {
                    case ITEM_KEY -> (item) -> item.getCompareKey().contains(keywordFilter);
                    case MATERIAL -> (item) ->
                    {
                        String name = item.getType().name().replace("_", " ");
                        return name.toLowerCase().contains(keywordFilter.toLowerCase());
                    };
                    case DISPLAY_NAME -> (item) -> ChatColor.stripColor(item.getItemMeta().getDisplayName())
                            .toLowerCase().contains(keywordFilter.toLowerCase());
                    case LORE ->
                            (item) -> ChatColor.stripColor(item.getItemMeta().getLore().get(0)).toLowerCase().contains(keywordFilter.toLowerCase());
                    case CUSTOM -> customFilter;
                };

        for (MenuItem item : items) {
            if (filterCriteria.apply(item)) {
                filteredItems.add(item);
            }
        }

        currentPage = 0;
        updatePageAmount();
        updatePage();
    }

    public void clearFilter() {
        applyFilter("");
    }

    public String getFilter() {
        return keywordFilter;
    }

    public void addItemsToSelect(MenuItem... newItems) {
        //first remove any previous whitespace
        while (items.size() > 0) {
            MenuItem lastItem = items.get(items.size() - 1);

            if (lastItem.getType().isAir())
                items.remove(lastItem);
            else

                break;
        }

        Collections.addAll(items, newItems);
        clearFilter();
    }

    public void removeItems(int... itemIndices) {
        //first remove any previous whitespace
        while (items.size() > 0) {
            MenuItem lastItem = items.get(items.size() - 1);

            if (lastItem.getType().isAir())
                items.remove(lastItem);
            else
                break;
        }
        for (int i : itemIndices)
            items.remove(i);

        updatePage();
    }

    public void clearItems() {
        items.clear();
        updatePage();
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public List<MenuItem> getSelectedItems() {
        return selectedItems;
    }

    public void selectItem(MenuItem item, boolean value) {
        if (!items.contains(item)) {
            return;
        }

        if (value) {
            selectedItems.add(item);
        } else {
            selectedItems.remove(item);
        }

        item.setGlowing(value);

        items.set(items.indexOf(item), item);
        updatePage();
    }

    protected void nextPage() {
        updatePageAmount();
        currentPage = Math.floorMod(currentPage + 1, pageAmount);
        updatePage();
    }

    protected void previousPage() {
        updatePageAmount();
        currentPage = Math.floorMod(currentPage - 1, pageAmount);
        updatePage();
    }

    protected void updatePage() {
        updatePageAmount();

        int startingIndex = currentPage * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            if (startingIndex + i < filteredItems.size())
                addItem(filteredItems.get(startingIndex + i).copyToSlot(i));
            else
                addItem(new MenuItem(i, Material.AIR, "", ""));
        }

        //Update Page description e.g. (20/23) for the Next and Previous 'buttons'.
        String pageCountDesc = String.format("%02d", currentPage + 1) + "/" + String.format("%02d", pageAmount);

        MenuItem next = getItemAt(NEXT.getSlot());
        ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setLore(List.of(pageCountDesc));
        }
        next.setItemMeta(nextMeta);
        updateActionItem(next);

        MenuItem previous = getItemAt(PREVIOUS.getSlot());
        ItemMeta prevMeta = previous.getItemMeta();
        if (prevMeta != null) {
            prevMeta.setLore(List.of(pageCountDesc));
        }
        previous.setItemMeta(prevMeta);
        updateActionItem(previous);
    }

    private void updatePageAmount() {
        pageAmount = Math.max(1, (int) Math.ceil(filteredItems.size() / (double) ITEMS_PER_PAGE));
    }

    /**
     * Replaces the item in the given slot at the current page to the new item. Keeps the item's selection status.
     *
     * @param newItem
     * @param slot
     */
    public void replaceItem(MenuItem newItem, int slot) {
        MenuItem oldItem = filteredItems.get(ITEMS_PER_PAGE * currentPage + slot);
        replaceItem(newItem, oldItem);
    }

    public void replaceItem(MenuItem newItem, MenuItem oldItem) {
        if (!items.contains(oldItem)) {
            return;
        }

        items.set(items.indexOf(oldItem), newItem);

        if (filteredItems.contains(oldItem))
            filteredItems.set(filteredItems.indexOf(oldItem), newItem);

        selectedItems.remove(oldItem);
    }
}
