package io.github.steaf23.bingoreloaded.gui.inventory.core;

import io.github.steaf23.bingoreloaded.gui.inventory.core.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class PaginatedDataMenu<Data> extends BasicMenu {

	/**
	 * Called by this Inventory's click event whenever an item in the page window gets clicked.
	 *
	 * @param event         the associated inventory click event
	 * @param clickedOption item that was clicked on, it's slot being the same slot that was clicked on.
	 * @param player        player that clicked on the menu.
	 */
	public abstract void onOptionClickedDelegate(final InventoryClickEvent event, Data clickedOption, PlayerHandle player);

	public abstract ItemTemplate toItem(Data data, boolean isSelected);
	public abstract boolean filterData(Data data, MenuFilterSettings filter);

	// There are 5 rows of items per page
	public static final int ITEMS_PER_PAGE = 9 * 5;

	// All the items that exist in this picker
	private final List<Data> allItems;

	// All selected items in this picker by slot id
	private final Set<Data> selectedItems;

	private Function<Data, Boolean> customFilter;

	// All items that pass the filter, these are always the items shown to the player
	private final List<Data> filteredItems;
	private int pageAmount;
	private int currentPage;
	private String keywordFilter;
	private MenuFilterSettings appliedFilter;

	private final MenuAction filterAction;
	private final MenuAction nextPageAction;
	private final MenuAction previousPageAction;

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

	public PaginatedDataMenu(MenuBoard board, Component initialTitle, List<Data> options, Function<Data, Boolean> customFilter) {
		this(board, initialTitle, options, List.of(FilterType.CUSTOM));
		this.customFilter = customFilter;
	}

	public PaginatedDataMenu(MenuBoard board, Component initialTitle, List<Data> options, FilterType filterType) {
		this(board, initialTitle, options, List.of(filterType));
	}

	public PaginatedDataMenu(MenuBoard board, Component initialTitle, List<Data> options, List<FilterType> availableFilterTypes) {
		super(board, initialTitle, 6);

		this.nextPageAction = addAction(NEXT.copy(), args -> this.nextPage());
		if (availableFilterTypes.isEmpty() || (availableFilterTypes.size() == 1 && availableFilterTypes.getFirst() == FilterType.NONE)) {
			addItem(BLANK.copyToSlot(1, 5));
			this.filterAction = null;
		} else {
			this.filterAction = addAction(FILTER.copy(), args -> {
				new UserInputMenu(getMenuBoard(), Component.text("Filter on..."), f -> applyFilter(new MenuFilterSettings(availableFilterTypes.getFirst(), f)), appliedFilter.name())
						.open(args.player());
			});
		}
		this.previousPageAction = addAction(PREVIOUS.copy(), args -> this.previousPage());

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
		selectedItems = new HashSet<>();
		filteredItems = new ArrayList<>(options);
		appliedFilter = MenuFilterSettings.EMPTY;
		clearFilter();
	}

	@Override
	public boolean onClick(InventoryClickEvent event, PlayerHandle player, int clickedSlot, ClickType clickType) {
		boolean cancel = super.onClick(event, player, clickedSlot, clickType);

		boolean isValidSlot = ITEMS_PER_PAGE * currentPage + event.getRawSlot() < filteredItems.size() && event.getRawSlot() < ITEMS_PER_PAGE;
		if (isValidSlot) {
			Data item = filteredItems.get(ITEMS_PER_PAGE * currentPage + event.getRawSlot());
//			onOptionClickedDelegate(event, item.setSlot(clickedSlot), player);
			onOptionClickedDelegate(event, item, player);
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
		if (filter.filterType() == FilterType.NONE || filterAction == null) {
			return;
		}

		appliedFilter = filter;
		filterAction.item().setLore(Component.text("{" + appliedFilter.name() + "}"));
		//TODO: automate addItem?
		addAction(filterAction);

		filteredItems.clear();

		for (Data data : allItems) {
			if (filterData(data, appliedFilter)) {
				filteredItems.add(data);
			}
		}

		currentPage = 0;
		updatePageAmount();
		updatePage();
	}

	public MenuFilterSettings getAppliedFilter() {
		return appliedFilter;
	}

	public void addItemsToSelect(Collection<Data> newItems) {
		allItems.addAll(newItems);
		clearFilter();
	}

	public void removeItems(int... itemIndices) {
		for (int i : itemIndices)
			allItems.remove(i);

		updatePage();
	}

	public void clearItems() {
		allItems.clear();
		updatePage();
	}

	public List<Data> getAllItems() {
		return allItems;
	}

	public Set<Data> getSelectedItems() {
		return selectedItems;
	}

	public void selectItem(Data item, boolean value) {
		if (!allItems.contains(item)) {
			return;
		}

		if (value) {
			selectedItems.add(item);
		} else {
			selectedItems.remove(item);
		}

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
			if (startingIndex + i < filteredItems.size()) {
				Data d = filteredItems.get(startingIndex + i);
				boolean selected = selectedItems.contains(d);
				ItemTemplate template = toItem(d, selected);
				template.setGlowing(selectedItems.contains(d));
				addItem(template.copyToSlot(i));
			}
			else {
				addItem(ItemTemplate.EMPTY.copyToSlot(i));
			}
		}

		//Update Page description e.g. (20/23) for the Next and Previous 'buttons'.
		Component pageCountDesc = Component.text(String.format("%02d", currentPage + 1) + "/" + String.format("%02d", pageAmount));

		nextPageAction.item().setLore(pageCountDesc);
		previousPageAction.item().setLore(pageCountDesc);
		addActions(nextPageAction, previousPageAction);
	}

	private void updatePageAmount() {
		pageAmount = Math.max(1, (int) Math.ceil(filteredItems.size() / (double) ITEMS_PER_PAGE));
	}

//	/**
//	 * Replaces the item in the given slot at the current page to the new item. Keeps the item's selection status.
//	 */
//	public void replaceItem(ItemTemplate newItem, int slot) {
//		ItemTemplate oldItem = filteredItems.get(ITEMS_PER_PAGE * currentPage + slot);
//		replaceItem(newItem, oldItem);
//	}
//
//	public void replaceItem(ItemTemplate newItem, ItemTemplate oldItem) {
//		if (!allItems.contains(oldItem)) {
//			return;
//		}
//
//		allItems.set(allItems.indexOf(oldItem), newItem);
//
//		if (filteredItems.contains(oldItem))
//			filteredItems.set(filteredItems.indexOf(oldItem), newItem);
//
//		selectedItems.remove(oldItem);
//	}

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
