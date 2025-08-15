package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.PlayerDisplayTranslationKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

//FIXME: maybe extend from PaginatedDataMenu to reduce code duplication?
public abstract class PaginatedSelectionMenu extends BasicMenu {

	/**
	 * Called by this Inventory's click event whenever an item in the page window gets clicked.
	 *
	 * @param event         the associated inventory click event
	 * @param clickedOption item that was clicked on, it's slot being the same slot that was clicked on.
	 * @param player        player that clicked on the menu.
	 */
	public abstract void onOptionClickedDelegate(final InventoryClickEvent event, ItemTemplate clickedOption, PlayerHandle player);

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
	private MenuFilterSettings appliedFilter;

	private final ItemTemplate filterItem;
	private final ItemTemplate nextPageItem;
	private final ItemTemplate previousPageItem;

	protected static final ItemTemplate NEXT = new ItemTemplate(8, 5, ItemTypePaper.of(Material.STRUCTURE_VOID),
			PlayerDisplayTranslationKey.MENU_NEXT.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	protected static final ItemTemplate PREVIOUS = new ItemTemplate(0, 5, ItemTypePaper.of(Material.BARRIER),
			PlayerDisplayTranslationKey.MENU_PREVIOUS.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	protected static final ItemTemplate CLOSE = new ItemTemplate(4, 5, ItemTypePaper.of(Material.REDSTONE),
			PlayerDisplayTranslationKey.MENU_SAVE_EXIT.translate()
					.color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

	protected static final ItemTemplate FILTER = new ItemTemplate(1, 5, ItemTypePaper.of(Material.HOPPER),
			PlayerDisplayTranslationKey.MENU_FILTER.translate()
					.color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));

	public PaginatedSelectionMenu(MenuBoard board, Component initialTitle, List<ItemTemplate> options, Function<ItemTemplate, Boolean> customFilter) {
		this(board, initialTitle, options, List.of(FilterType.CUSTOM));
		this.customFilter = customFilter;
	}

	public PaginatedSelectionMenu(MenuBoard board, Component initialTitle, List<ItemTemplate> options, FilterType filterType) {
		this(board, initialTitle, options, List.of(filterType));
	}

	public PaginatedSelectionMenu(MenuBoard board, Component initialTitle, List<ItemTemplate> options, List<FilterType> availableFilterTypes) {
		super(board, initialTitle, 6);

		this.filterItem = FILTER.copy();
		this.nextPageItem = NEXT.copy();
		this.previousPageItem = PREVIOUS.copy();

		addAction(nextPageItem, args -> this.nextPage());
		if (availableFilterTypes.isEmpty() || (availableFilterTypes.size() == 1 && availableFilterTypes.getFirst() == FilterType.NONE)) {
			addItem(BLANK.copyToSlot(1, 5));
		} else {
			addAction(filterItem, args -> {
				new UserInputMenu(getMenuBoard(), Component.text("Filter on..."), f -> applyFilter(new MenuFilterSettings(availableFilterTypes.getFirst(), f)), appliedFilter.name())
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
		appliedFilter = MenuFilterSettings.EMPTY;
		clearFilter();
	}

	@Override
	public boolean onClick(InventoryClickEvent event, PlayerHandle player, int clickedSlot, ClickType clickType) {
		boolean cancel = super.onClick(event, player, clickedSlot, clickType);

		boolean isValidSlot = ITEMS_PER_PAGE * currentPage + event.getRawSlot() < filteredItems.size() && event.getRawSlot() < ITEMS_PER_PAGE;
		if (isValidSlot) {
			ItemTemplate item = filteredItems.get(ITEMS_PER_PAGE * currentPage + event.getRawSlot());
			onOptionClickedDelegate(event, item.setSlot(clickedSlot), player);
		}
		return cancel;
	}

	@Override
	public void onCustomAction(Key key, DataStorage payload) {
		ConsoleMessenger.log("Received " + key.asMinimalString() + " and " + payload.getString("filter", ""));

		String filterTypeStr = payload.getString("filter_option", "NONE");

		try {
			applyFilter(new MenuFilterSettings(FilterType.valueOf(filterTypeStr),
					payload.getString("filter", "")));
		} catch (IllegalArgumentException illegalFilterTypeException) {
			ConsoleMessenger.bug("Unknown filter type '" + filterTypeStr + "' from filter dialog", PaginatedSelectionMenu.class);
		}
	}

	public void applyFilter(MenuFilterSettings filter) {
		if (filter.filterType() == FilterType.NONE) {
			return;
		}

		appliedFilter = filter;
		filterItem.setLore(Component.text("{" + appliedFilter.name() + "}"));
		//TODO: automate addItem?
		addItem(filterItem);

		filteredItems.clear();

		Function<ItemTemplate, Boolean> filterCriteria;

		filterCriteria =
				switch (filter.filterType()) {
					case ITEM_ID -> (item) -> item.getCompareKey().contains(appliedFilter.name());
					case MATERIAL -> (item) ->
					{
						String name = item.getItemType().key().value().replace("_", " ");
						return name.toLowerCase().contains(appliedFilter.name().toLowerCase());
					};
					case DISPLAY_NAME -> (item) -> item.getPlainTextName()
							.toLowerCase().contains(appliedFilter.name().toLowerCase());
					case CUSTOM -> customFilter;
					default -> throw new IllegalStateException("Unexpected filter type while filtering menu items: " + filter.filterType());
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

	public MenuFilterSettings getAppliedFilter() {
		return appliedFilter;
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
		if (appliedFilter.filterType() == FilterType.NONE) {
			// When we have no filter type, all items pass the filter
			filteredItems.clear();
			filteredItems.addAll(allItems);
			updatePage();
		}
		applyFilter(new MenuFilterSettings(FilterType.NONE, ""));
	}
}
