package io.github.steaf23.bingoreloaded.lib.inventory.group;

import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class PaginatedGroup<Data> extends ItemGroup {

	private final List<ItemTemplate> allItems = new ArrayList<>();
	private final List<Data> allData = new ArrayList<>();

	private final Consumer<Data> dataClickedCallback;

	private int currentPage = 0;

	public PaginatedGroup(int startX, int startY, int sizeX, int sizeY, Consumer<Data> dataClicked) {
		super(startX, startY, sizeX, sizeY);
		this.dataClickedCallback = dataClicked;
	}

	public void setItems(List<ItemTemplate> items, List<Data> data) {
		allItems.clear();
		allItems.addAll(items);

		allData.clear();
		allData.addAll(data);
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

	public void nextPage(BasicMenu menu) {
		setPage(menu, currentPage + 1);
	}

	public void previousPage(BasicMenu menu) {
		setPage(menu, currentPage - 1);
	}


	@Override
	public void updateVisibleItems(BasicMenu menu) {
		Collection<ItemTemplate> items = getItems();
		int emptyItems = rect().getSlotCount() - items.size();

		int itemIdx = currentPage * rect().getSlotCount();
		for (ItemTemplate item : items) {
			int slotIdx = pageIndexToGlobal(itemIdx % rect().getSlotCount());
			Data data = allData.get(itemIdx);
			menu.addAction(item.copyToSlot(slotIdx), args -> onClick(args, data));
			itemIdx++;
		}

		for (int i = 0; i < emptyItems; i++) {
			int slotIdx = pageIndexToGlobal(itemIdx % rect().getSlotCount());
			menu.addItem(ItemTemplate.EMPTY.copyToSlot(slotIdx));
			itemIdx++;
		}
	}

	public void onClick(MenuAction.ActionArguments arguments, Data data) {
		if (arguments.clickType() == ClickType.LEFT) {
			dataClickedCallback.accept(data);
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
}
