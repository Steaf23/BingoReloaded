package io.github.steaf23.playerdisplay.inventory;

import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import io.github.steaf23.playerdisplay.util.PlayerDisplayTranslationKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class PaginatedSelectionMenu extends BasicMenu
{
    /**
     * Called by this Inventory's click event whenever an item in the page window gets clicked.
     *
     * @param event the associated inventory click event
     * @param clickedOption item that was clicked on, it's slot being the same slot that was clicked on.
     * @param player player that clicked on the menu.
     */
    public abstract void onOptionClickedDelegate(final InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player);

    // There are 5 rows of items per page
    public static final int ITEMS_PER_PAGE = 9 * 5;

    // All the items that exist in this picker
    private final List<ItemTemplate> allItems;

    // All selected items in this picker
    private final List<ItemTemplate> selectedItems;

    private Function<ItemTemplate, Boolean> customFilter;

    // All items that pass the filter, these are always the items shown to the player
    private final List<ItemTemplate> filteredItems;
    private int pageAmount;
    private int currentPage;
    private String keywordFilter;
    public FilterType filterType;

    private final ItemTemplate filterItem;
    private final ItemTemplate nextPageItem;
    private final ItemTemplate previousPageItem;

    protected static final ItemTemplate NEXT = new ItemTemplate(8, 5, Material.STRUCTURE_VOID,
            PlayerDisplayTranslationKey.MENU_NEXT.translate()
                    .color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

    protected static final ItemTemplate PREVIOUS = new ItemTemplate(0, 5, Material.BARRIER,
            PlayerDisplayTranslationKey.MENU_PREVIOUS.translate()
                    .color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

    protected static final ItemTemplate CLOSE = new ItemTemplate(4, 5, Material.REDSTONE,
            PlayerDisplayTranslationKey.MENU_SAVE_EXIT.translate()
                    .color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

    protected static final ItemTemplate FILTER = new ItemTemplate(1, 5, Material.SPYGLASS,
            PlayerDisplayTranslationKey.MENU_FILTER.translate()
                    .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

    public PaginatedSelectionMenu(MenuBoard board, Component initialTitle, List<ItemTemplate> options, Function<ItemTemplate, Boolean> customFilter) {
        this(board, initialTitle, options, FilterType.CUSTOM);
        this.customFilter = customFilter;
    }

    public PaginatedSelectionMenu(MenuBoard board, Component initialTitle, List<ItemTemplate> options, FilterType filterType) {
        super(board, initialTitle, 6);

        this.filterItem = FILTER.copy();
        this.nextPageItem = NEXT.copy();
        this.previousPageItem = PREVIOUS.copy();
        this.filterType = filterType;

        addAction(nextPageItem, args -> this.nextPage());
        if (filterType == FilterType.NONE) {
            addItem(BLANK.copyToSlot(1, 5));
        } else {
            addAction(filterItem, args -> {
                new UserInputMenu(board, Component.text("Filter by name"), this::applyFilter, keywordFilter.isBlank() ? "name" : keywordFilter)
                        .open(args.player());
            });
        }
        addAction(previousPageItem, args -> this.previousPage());

        addItems(
                BLANK.copyToSlot(2, 5),
                BLANK.copyToSlot(3, 5),
                BLANK.copyToSlot(5, 5),
                BLANK.copyToSlot(6, 5),
                BLANK.copyToSlot(7, 5)
        );
        addCloseAction(CLOSE.copy());

        currentPage = 0;
        allItems = options;
        selectedItems = new ArrayList<>();
        filteredItems = new ArrayList<>(options);
        keywordFilter = "";
        clearFilter();
    }

    @Override
    public boolean onClick(InventoryClickEvent event, HumanEntity player, int clickedSlot, ClickType clickType) {
        boolean cancel = super.onClick(event, player, clickedSlot, clickType);

        boolean isValidSlot = ITEMS_PER_PAGE * currentPage + event.getRawSlot() < filteredItems.size() && event.getRawSlot() < ITEMS_PER_PAGE;
        if (isValidSlot)
        {
            ItemTemplate item = filteredItems.get(ITEMS_PER_PAGE * currentPage + event.getRawSlot());
            onOptionClickedDelegate(event, item.setSlot(clickedSlot), player);
        }
        return cancel;
    }

    public void applyFilter(String filter) {
        if (filterType == FilterType.NONE) {
            return;
        }

        keywordFilter = filter;
        filterItem.setLore(Component.text("{" + keywordFilter + "}"));
        //TODO: automate addItem?
        addItem(filterItem);

        filteredItems.clear();

        Function<ItemTemplate, Boolean> filterCriteria;

        filterCriteria =
                switch (filterType) {
                    case NONE -> item -> true;
                    case ITEM_KEY -> (item) -> item.getCompareKey().contains(keywordFilter);
                    case MATERIAL -> (item) ->
                    {
                        String name = item.getMaterial().name().replace("_", " ");
                        return name.toLowerCase().contains(keywordFilter.toLowerCase());
                    };
                    case DISPLAY_NAME -> (item) -> item.getPlainTextName()
                            .toLowerCase().contains(keywordFilter.toLowerCase());
                    case CUSTOM -> customFilter;
                };

        for (ItemTemplate item : allItems) {
            if (filterCriteria.apply(item)) {
                filteredItems.add(item);
            }
        }

        currentPage = 0;
        updatePageAmount();
        updatePage();
    }

    public String getFilter() {
        return keywordFilter;
    }

    public void addItemsToSelect(Collection<ItemTemplate> newItems) {
        //first remove any previous whitespace
        while (!allItems.isEmpty()) {
            ItemTemplate lastItem = allItems.getLast();

            if (lastItem.isEmpty())
                allItems.remove(lastItem);
            else

                break;
        }

        allItems.addAll(newItems);
        clearFilter();
    }

    public void removeItems(int... itemIndices) {
        //first remove any previous whitespace
        while (!allItems.isEmpty()) {
            ItemTemplate lastItem = allItems.getLast();

            if (lastItem.isEmpty())
                allItems.remove(lastItem);
            else
                break;
        }
        for (int i : itemIndices)
            allItems.remove(i);

        updatePage();
    }

    public void clearItems() {
        allItems.clear();
        updatePage();
    }

    public List<ItemTemplate> getAllItems() {
        return allItems;
    }

    public List<ItemTemplate> getSelectedItems() {
        return selectedItems;
    }

    public void selectItem(ItemTemplate item, boolean value) {
        if (!allItems.contains(item)) {
            return;
        }

        if (value) {
            selectedItems.add(item);
        } else {
            selectedItems.remove(item);
        }

        item.setGlowing(value);

        allItems.set(allItems.indexOf(item), item);
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
                addItem(ItemTemplate.EMPTY.copyToSlot(i));
        }

        //Update Page description e.g. (20/23) for the Next and Previous 'buttons'.
        Component pageCountDesc = Component.text(String.format("%02d", currentPage + 1) + "/" + String.format("%02d", pageAmount));

        nextPageItem.setLore(pageCountDesc);
        previousPageItem.setLore(pageCountDesc);
        addItems(nextPageItem, previousPageItem);
    }

    private void updatePageAmount() {
        pageAmount = Math.max(1, (int) Math.ceil(filteredItems.size() / (double) ITEMS_PER_PAGE));
    }

    /**
     * Replaces the item in the given slot at the current page to the new item. Keeps the item's selection status.
     */
    public void replaceItem(ItemTemplate newItem, int slot) {
        ItemTemplate oldItem = filteredItems.get(ITEMS_PER_PAGE * currentPage + slot);
        replaceItem(newItem, oldItem);
    }

    public void replaceItem(ItemTemplate newItem, ItemTemplate oldItem) {
        if (!allItems.contains(oldItem)) {
            return;
        }

        allItems.set(allItems.indexOf(oldItem), newItem);

        if (filteredItems.contains(oldItem))
            filteredItems.set(filteredItems.indexOf(oldItem), newItem);

        selectedItems.remove(oldItem);
    }

    public void clearFilter() {
        if (filterType == FilterType.NONE) {
            // When we have no filter type, all items pass the filter
            filteredItems.clear();
            filteredItems.addAll(allItems);
            updatePage();
        }
        applyFilter("");
    }
}
