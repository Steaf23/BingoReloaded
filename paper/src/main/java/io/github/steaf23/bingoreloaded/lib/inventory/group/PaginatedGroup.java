package io.github.steaf23.bingoreloaded.lib.inventory.group;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.ExtensionTask;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PaginatedGroup<Data> extends ItemGroup {

	private final List<ItemTemplate> allItems = new ArrayList<>(); // only used when no itemFunction is used.
	private final List<Data> allData = new ArrayList<>();
	private final Set<Integer> hiddenItemIndices = new HashSet<>();

	private final DataClickedCallback<Data> dataClickedCallback;

	private int currentPage = 0;
	private final SelectionModel selection;
	private final boolean allowUserSelection;

	private ExtensionTask updateItemsTask = null;

	private BiFunction<Data, Boolean, ItemTemplate> itemFunction = null; // when set, used instead of the itemList to get items from data. lazy loading equivalent of the item list.

	public PaginatedGroup(int startX, int startY, int sizeX, int sizeY, @Nullable DataClickedCallback<Data> dataClicked) {
		this(startX, startY, sizeX, sizeY, dataClicked, SelectionModel.SelectMode.NONE, false);
	}

	public PaginatedGroup(int startX, int startY, int sizeX, int sizeY, @Nullable DataClickedCallback<Data> dataClicked, SelectionModel.SelectMode mode, boolean allowUserSelection) {
		this(new ItemRect(startX, startY, sizeX, sizeY), dataClicked, SelectionModel.SelectMode.NONE, allowUserSelection);
	}

	public PaginatedGroup(ItemRect rect, @Nullable DataClickedCallback<Data> dataClicked, SelectionModel.SelectMode selectMode, boolean allowUserSelection) {
		super(rect);
		this.dataClickedCallback = Objects.requireNonNullElse(dataClicked, (_, _, _) -> {});
		this.selection = new SelectionModel(selectMode, () -> !allData.isEmpty());
		this.allowUserSelection = allowUserSelection;
	}

	public void setItems(List<ItemTemplate> items, List<Data> data) {
		this.itemFunction = null;
		allItems.clear();
		allItems.addAll(items);

		allData.clear();
		allData.addAll(data);
		selection.reset();
		showAllItems();
	}

	public void setItems(BiFunction<Data, Boolean, ItemTemplate> itemFunction, Collection<Data> data) {
		this.itemFunction = itemFunction;
		allItems.clear();

		allData.clear();
		allData.addAll(data);
		selection.reset();
		showAllItems();
	}

	public void hideIndex(int localSlotIndex, boolean hide) {
		if (hide) {
			hideIndex(localSlotIndex);
		} else {
			showIndex(localSlotIndex);
		}
	}

	public void showIndex(int localSlotIndex) {
		hiddenItemIndices.remove(localSlotIndex);
	}

	public void hideIndex(int localSlotIndex) {
		hiddenItemIndices.add(localSlotIndex);
	}

	public void showAllItems() {
		hiddenItemIndices.clear();
	}

	public void hideData(Data data, boolean hide) {
		if (hide) {
			hideData(data);
		} else {
			showData(data);
		}
	}

	public void showData(Data data) {
		int index = allData.indexOf(data);
		if (index == -1) {
			return;
		}

		hideIndex(index, false);
	}

	public void hideData(Data data) {
		int index = allData.indexOf(data);
		if (index == -1) {
			return;
		}

		hideIndex(index, true);
	}

	public int getPageCount() {
		return (int) Math.ceil((double) (allData.size() - hiddenItemIndices.size()) / rect().getSlotCount());
	}

	public void setPage(BasicMenu menu, int newPage) {
		if (newPage >= getPageCount() || newPage < 0) {
			return;
		}

		currentPage = newPage;
		updateVisibleItems(menu);
	}

	public SelectionModel selection() {
		return selection;
	}

	public void nextPage(BasicMenu menu) {
		setPage(menu, currentPage + 1);
	}

	public void previousPage(BasicMenu menu) {
		setPage(menu, currentPage - 1);
	}

	public List<Data> allData()
	{
		return allData;
	}

	/**
	 * @param idx location in the data list to set this item.
	 * @param newData new data to place at the specified idx.
	 * @param newItem pass null if the itemFunction is used to convert items from data.
	 */
	public void setItem(int idx, Data newData, @Nullable ItemTemplate newItem) {
		if (idx >= allData.size())
		{
			return;
		}

		allData.set(idx, newData);
		if (newItem != null) {
			allItems.set(idx, newItem);
		}
		showIndex(idx);
	}

	@Override
	public void updateVisibleItems(BasicMenu menu) {
		if (updateItemsTask != null && !updateItemsTask.isCancelled()) {
			return;
		}

		updateItemsTask = PlatformResolver.get().runTask(t -> {
			List<Integer> indicesOnPage = indicesVisibleOnPage(currentPage);
			int emptyItems = rect().getSlotCount() - indicesOnPage.size();
			int indexOnPage = 0;
			for (int i : indicesOnPage) { // Importantly, if items are hidden, this list might not be in consecutive order.
				int globalSlot = pageIndexToGlobal(indexOnPage);
				Data data = allData.get(i);
				ItemTemplate item = itemFunction.apply(data, selection.contains(i)).copyToSlot(globalSlot);
				if (selection.mode() != SelectionModel.SelectMode.NONE) {
					item.setGlowing(selection.contains(i));
				}

				menu.addAction(item, args -> onClick(args, i, data));
				indexOnPage++;
			}

			for (int i = 0; i < emptyItems; i++) {
				int slotIdx = pageIndexToGlobal(indexOnPage);
				menu.addItem(ItemTemplate.EMPTY.copyToSlot(slotIdx));
				indexOnPage++;
			}
			updateItemsTask.cancel();
		});
	}

	public void onClick(MenuAction.ActionArguments arguments, int slotInPage, Data data) {
		int indexInList = currentPage * rect().getSlotCount() + slotInPage;
		if (allowUserSelection) {
			selection.toggleSlot(indexInList);
		}

		dataClickedCallback.clicked(arguments, indexInList, data);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param page
	 * @return the indices of the items that are be visible on the given page, accounting for hidden items that are earlier in the list.
	 */
	private List<Integer> indicesVisibleOnPage(int page) {
		int visualStartIndex = page * rect().getSlotCount();

		// count until we reached the point where the page starts.
		int count = 0; // count will be at most the amount of visible items.
		for (int i = 0; i < allData.size(); i++) {
			if (count == visualStartIndex) {
				List<Integer> result = new ArrayList<>();
				// for every slot after this point that's not hidden, we add it to the page.
				for (int slot = i; slot < allData.size(); slot++) {
					if (!hiddenItemIndices.contains(slot)) {
						result.add(slot);
					}

					if (result.size() == rect().getSlotCount()) {
						return result;
					}
				}

				return result;
			}

			if (!hiddenItemIndices.contains(i))
			{
				count++;
			}
		}

		return List.of(); // There are not enough items in the list that are visible to have this page.
	}

	public int pageIndexToGlobal(int index) {
		int localX = index % rect().sizeX();
		int localY = index / rect().sizeX();
		return rect().toGlobal(localX, localY);
	}

	public List<Data> allSelectedData() {
		List<Data> data = new ArrayList<>();
		for (int slot : selection().selectedSlots()) {
			data.add(allData.get(slot));
		}
		return data;
	}

	private ItemTemplate getOrCreateItemAtDataIndex(int index) {
		if (itemFunction == null) {
			return allItems.get(index);
		} else {
			return itemFunction.apply(allData.get(index), selection.contains(index));
		}
	}

	@FunctionalInterface
	public interface DataClickedCallback<ClickedData> {
		void clicked(MenuAction.ActionArguments arguments, int groupSlot, ClickedData data);
	}
}
