package io.github.steaf23.bingoreloaded.lib.inventory.group;

import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class PaginatedGroup<Data> extends ItemGroup {

	private final List<ItemTemplate> allItems = new ArrayList<>();
	private final List<Data> allData = new ArrayList<>();

	private final BiConsumer<Integer, Data> dataClickedCallback;

	private int currentPage = 0;
	private final SelectionModel selection;

	public PaginatedGroup(int startX, int startY, int sizeX, int sizeY, @Nullable BiConsumer<Integer, Data> dataClicked) {
		this(startX, startY, sizeX, sizeY, dataClicked, SelectionModel.SelectMode.NONE);
	}

	public PaginatedGroup(int startX, int startY, int sizeX, int sizeY, @Nullable BiConsumer<Integer, Data> dataClicked, SelectionModel.SelectMode selectMode) {
		super(startX, startY, sizeX, sizeY);
		this.dataClickedCallback = Objects.requireNonNullElse(dataClicked, (slot, data) -> {});
		this.selection = new SelectionModel(selectMode, () -> !allData.isEmpty());
	}

	public void setItems(List<ItemTemplate> items, List<Data> data) {
		allItems.clear();
		allItems.addAll(items);

		allData.clear();
		allData.addAll(data);
		selection.reset();
	}

	public int getPageCount() {
		return (int) Math.ceil((double) allItems.size() / rect().getSlotCount());
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

	public List<ItemTemplate> allItems() {
		return allItems;
	}

	public List<Data> allData()
	{
		return allData;
	}

	public void setItem(int idx, Data newData, ItemTemplate newItem) {
		if (idx >= allItems.size())
		{
			return;
		}

		allData.set(idx, newData);
		allItems.set(idx, newItem);
	}

	@Override
	public void updateVisibleItems(BasicMenu menu) {
		Collection<ItemTemplate> items = getItems();
		int emptyItems = rect().getSlotCount() - items.size();

		int itemIdx = currentPage * rect().getSlotCount();
		for (ItemTemplate item : items) {
			int groupSlot = itemIdx % rect().getSlotCount();
			int slotIdx = pageIndexToGlobal(groupSlot);
			Data data = allData.get(itemIdx);
			menu.addAction(item.copyToSlot(slotIdx).setGlowing(selection.contains(itemIdx)), args -> onClick(args, groupSlot, data));
			itemIdx++;
		}

		for (int i = 0; i < emptyItems; i++) {
			int slotIdx = pageIndexToGlobal(itemIdx % rect().getSlotCount());
			menu.addItem(ItemTemplate.EMPTY.copyToSlot(slotIdx));
			itemIdx++;
		}
	}

	public void onClick(MenuAction.ActionArguments arguments, int groupSlotIndex, Data data) {
		int indexInList = currentPage * rect().getSlotCount() + groupSlotIndex;
		selection.toggleSlot(indexInList);

		if (arguments.clickType() == ClickType.LEFT) {
			dataClickedCallback.accept(indexInList, data);
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public Collection<ItemTemplate> getItems() {
		if (allItems.isEmpty()) {
			return List.of();
		}

		int startIdx = currentPage * rect().getSlotCount();
		int endIdx = startIdx + rect().getSlotCount() - 1;
		if (endIdx >= allItems.size()) {
			endIdx = allItems.size();
		}
		else {
			endIdx++;
		}

		return allItems.subList(startIdx, endIdx);
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
}
