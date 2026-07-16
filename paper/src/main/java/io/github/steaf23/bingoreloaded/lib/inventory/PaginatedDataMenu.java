package io.github.steaf23.bingoreloaded.lib.inventory;

import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.inventory.group.ItemGroup;
import io.github.steaf23.bingoreloaded.lib.inventory.group.ItemRect;
import io.github.steaf23.bingoreloaded.lib.inventory.group.PaginatedGroup;
import io.github.steaf23.bingoreloaded.lib.inventory.group.SelectionModel;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.PlayerDisplayTranslationKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class PaginatedDataMenu<Data> extends BasicMenu {

	public static abstract class TextDataMenu extends PaginatedDataMenu<String> {

		public TextDataMenu(MenuBoard board, Component initialTitle, Collection<String> options) {
			super(board, initialTitle, options);
		}

		@Override
		public boolean filterByData(String text, MenuFilterSettings filter) {
			return text.toLowerCase().contains(filter.name().toLowerCase());
		}
	}

	/**
	 * Called by this Inventory's click event whenever an item in the page window gets clicked.
	 *
	 * @param arguments     click context
	 * @param clickedOption item that was clicked on, it's slot being the same slot that was clicked on.
	 */
	public abstract void onOptionClickedDelegate(MenuAction.ActionArguments arguments, Data clickedOption);

	public abstract Material material(Data data, boolean selected);
	public abstract Component displayName(Data data, boolean selected);

	public ItemTemplate editItem(ItemTemplate item, Data data, boolean selected) {
		return item;
	}

	public boolean filterByData(Data data, MenuFilterSettings filter) {
		return false;
	}

	// All items that pass the filter, these are always the items shown to the player
	private String keywordFilter;
	private MenuFilterSettings appliedFilter;
	private boolean filteringSelected = false;

	private final List<FilterType> availableFilters;

	private final MenuAction filterAction;
	private final MenuAction nextPageAction;
	private final MenuAction previousPageAction;

	private final PaginatedGroup<Data> pagination;

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

	public PaginatedDataMenu(MenuBoard board, Component initialTitle, Collection<Data> options) {
		this(board, initialTitle, options, List.of(FilterType.DATA));
	}

	public PaginatedDataMenu(MenuBoard board, Component initialTitle, Collection<Data> options, FilterType filterType) {
		this(board, initialTitle, options, List.of(filterType));
	}

	public PaginatedDataMenu(MenuBoard board, Component initialTitle, Collection<Data> options, List<FilterType> availableFilterTypes) {
		this(board, initialTitle, options, availableFilterTypes, new ItemRect(0, 0, 9, 5), SelectionModel.SelectMode.NONE);
	}

	public PaginatedDataMenu(MenuBoard board, Component initialTitle, Collection<Data> options, List<FilterType> availableFilterTypes, ItemRect paginationRect, SelectionModel.SelectMode selectMode) {
		super(board, initialTitle, 6);
		this.availableFilters = availableFilterTypes;
		this.pagination = new PaginatedGroup<>(paginationRect, this::groupItemClicked, selectMode, false);

		this.nextPageAction = addAction(NEXT.copy(), _ -> {
			this.pagination.nextPage(this);
			updatePageNavigation();
		});
		if (availableFilterTypes.isEmpty() || (availableFilterTypes.size() == 1 && availableFilterTypes.getFirst() == FilterType.NONE)) {
			addItem(BLANK.copyToSlot(1, 5));
			this.filterAction = null;
		} else {
			this.filterAction = addAction(FILTER.copy(), args -> {
				new UserInputMenu(getMenuBoard(), Component.text("Filter on..."), f -> applyFilter(new MenuFilterSettings(availableFilterTypes.getFirst(), f)), appliedFilter.name())
						.open(args.player());
			});
		}
		this.previousPageAction = addAction(PREVIOUS.copy(), _ -> {
			this.pagination.previousPage(this);
			updatePageNavigation();
		});

		addItems(
				BLANK.copyToSlot(2, 5),
				BLANK.copyToSlot(3, 5),
				BLANK.copyToSlot(5, 5),
				BLANK.copyToSlot(6, 5),
				BLANK.copyToSlot(7, 5)
		);
		addCloseAction(CLOSE.copy());

		appliedFilter = MenuFilterSettings.EMPTY;
		clearFilter();
		pagination.setItems(this::createItem, options);
		updatePageNavigation();
	}

	public boolean isDataSelected(Data data) {
		return pagination.selection().selectedSlots().contains(getAllItems().indexOf(data));
	}

	public Collection<Data> getSelectedItems() {
		return pagination.allSelectedData();
	}

	private void groupItemClicked(MenuAction.ActionArguments arguments, int slot, Data data) {
		onOptionClickedDelegate(arguments, data);
	}

	public void applyFilter(MenuFilterSettings filter) {
		if (filter.filterType() == FilterType.NONE || filterAction == null) {
			return;
		}

		this.pagination.showAllItems();

		appliedFilter = filter;
		filterAction.item().setLore(Component.text("{" + appliedFilter.name() + "}"));
		addAction(filterAction);

		Function<Data, Boolean> filterCriteria = switch(filter.filterType()) {
			case MATERIAL -> d -> {
				String matName = material(d, false).key().value().replace("_", " ").toLowerCase();
				return matName.contains(filter.name().toLowerCase());
			};

			case DISPLAY_NAME -> d -> {
				String displayName = PlainTextComponentSerializer.plainText().serialize(displayName(d, false)).toLowerCase();
				return displayName.contains(filter.name().toLowerCase());
			};
			case DATA -> d -> filterByData(d, appliedFilter);
			case SELECTED -> this::isDataSelected;

			default -> throw new IllegalStateException("Unexpected filter type while filtering menu items: " + filter.filterType());
		};

		for (int i = 0; i < getAllItems().size(); i++) {
			Data data = getAllItems().get(i);
			if (!filterCriteria.apply(data)) {
				pagination.hideIndex(i);
			}
		}

		this.pagination.setPage(this, 0);
		updatePageNavigation();
	}

	public MenuFilterSettings getAppliedFilter() {
		return appliedFilter;
	}

	public void setData(Collection<Data> allData) {
		this.pagination.setItems(this::createItem, allData);
		this.pagination.updateVisibleItems(this);
		clearFilter();
		updatePageNavigation();
	}

	public List<Data> getAllItems() {
		return pagination.allData();
	}


	public void selectItem(Data item, boolean value) {
		if (!getAllItems().contains(item)) {
			return;
		}

		pagination.selection().selectManually(getAllItems().indexOf(item), value);
		pagination.updateVisibleItems(this);
	}

	protected void updatePageNavigation() {
		//Update Page description e.g. (20/23) for the Next and Previous 'buttons'.
		Component pageCountDesc = Component.text(String.format("%02d", pagination.getCurrentPage() + 1) + "/" + String.format("%02d", pagination.getPageCount()));

		nextPageAction.item().setLore(pageCountDesc);
		previousPageAction.item().setLore(pageCountDesc);
		addActions(nextPageAction, previousPageAction);
	}

	private ItemTemplate createItem(Data data, boolean selected) {
		ItemTemplate template = new ItemTemplate(ItemTypePaper.of(material(data, selected)), displayName(data, selected));
		template = editItem(template, data, selected);
		template.setDummy(true);
		return template;
	}

	public void clearFilter() {
		if (appliedFilter.filterType() == FilterType.NONE) {
			// When we have no filter type, all items pass the filter
			pagination.showAllItems();
			this.pagination.updateVisibleItems(this);
		}
		applyFilter(new MenuFilterSettings(availableFilters.getFirst(), ""));
	}

	public void setFilterSelected(boolean value) {
		if (value) {
			applyFilter(new MenuFilterSettings(FilterType.SELECTED, ""));
		} else {
			clearFilter();
		}
	}
}
