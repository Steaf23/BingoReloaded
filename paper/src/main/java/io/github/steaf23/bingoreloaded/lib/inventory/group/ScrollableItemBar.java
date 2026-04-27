package io.github.steaf23.bingoreloaded.lib.inventory.group;

import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.PlayerDisplayTranslationKey;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class ScrollableItemBar<Data> extends ItemGroup {

	private static final ItemTemplate NEXT = new ItemTemplate(ItemTypePaper.of(Material.STRUCTURE_VOID),
			PlayerDisplayTranslationKey.MENU_NEXT.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	private static final ItemTemplate PREVIOUS = new ItemTemplate(ItemTypePaper.of(Material.BARRIER),
			PlayerDisplayTranslationKey.MENU_PREVIOUS.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	private ItemClickedCallback<Data> itemClickedCallback = (idx, item, data) -> {};
	private List<ItemTemplate> items = new ArrayList<>();
	private List<Data> data = new ArrayList<>();
	private int scrollOffset = 0;
	private final BasicMenu menu;
	private final SelectionModel selection;

	public ScrollableItemBar(BasicMenu menu, int startX, int startY, int length, SelectionModel.SelectMode selectMode) {
		super(new ItemRect(startX, startY, length, 1));
		this.menu = menu;
		this.selection = new SelectionModel(selectMode, () -> !data.isEmpty());
	}

	public void setItems(List<ItemTemplate> items, List<Data> data) {
		this.items = items;
		this.data = data;
		this.scrollOffset = 0;
		this.selection.reset();

		updateVisibleItems(menu);
	}

	public void setItemClickedCallback(ItemClickedCallback<Data> callback) {
		this.itemClickedCallback = callback;
	}

	@Override
	public void updateVisibleItems(BasicMenu menu) {
		boolean needsScrollButtons = items.size() > rect().sizeX();
		if (!needsScrollButtons) {
			int idx = 0;
			for (ItemTemplate item : items) {
				int itemIdx = idx;
				menu.addAction(item.copyToSlot(rect().startX() + idx, rect().startY()).setGlowing(selection.contains(itemIdx)), args -> {
					if (args.clickType() == ClickType.LEFT) {
						onClick(item, itemIdx);
					}
				});
				idx++;
			}
		} else {
			// draw scroll items
			menu.addAction(NEXT.copyToSlot(rect().startX() + rect().sizeX() - 1, rect().startY()), args -> scrollItems(menu, 1));
			menu.addAction(PREVIOUS.copyToSlot(rect().startX()), args -> scrollItems(menu, -1));
			// draw (length - 2) items, starting at itemOffset and ending at itemOffset + (length - 3)
			int fullLength = rect().sizeX();

			for (int i = 0; i < fullLength - 2; i++) {
				int indexOfItemInList = scrollOffset + i;

				if (items.size() > indexOfItemInList) {
					ItemTemplate item = items.get(indexOfItemInList);
					menu.addAction(item
							.copyToSlot(rect().startX() + i + 1, rect().startY())
							.setGlowing(selection.selectedSlots().contains(indexOfItemInList)), args -> {
						if (args.clickType() == ClickType.LEFT) {
							onClick(item, indexOfItemInList);
						}
					});

				} else {
					menu.addItem(ItemTemplate.EMPTY);
				}
			}
		}
	}

	void scrollItems(BasicMenu menu, int by) {
		scrollOffset = Math.clamp(scrollOffset + by, 0, items.size() - 7);
		updateVisibleItems(menu);
	}

	public void onClick(ItemTemplate item, int itemIdx) {
		selection.toggleSlot(itemIdx - scrollOffset);

		itemClickedCallback.execute(itemIdx, item, data.get(itemIdx));
		PlatformResolver.get().runTask(t -> {
			updateVisibleItems(menu);
		});
	}

	public List<Data> selectedData() {
		List<Data> dataSet = new ArrayList<>();
		for (int idx : selection.selectedSlots()) {
			dataSet.add(data.get(idx));
		}
		return dataSet;
	}

	@FunctionalInterface
	public interface ItemClickedCallback<Data> {
		void execute(int index, ItemTemplate item, Data data);
	}
}
