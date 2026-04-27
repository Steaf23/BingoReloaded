package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.TaskTagData;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.group.PaginatedGroup;
import io.github.steaf23.bingoreloaded.lib.inventory.group.ScrollableItemBar;
import io.github.steaf23.bingoreloaded.lib.inventory.group.SelectionModel;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.PlayerDisplayTranslationKey;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagManagerMenu extends BasicMenu {

	private final String listName;
	private final BingoCardData cardData;

	private Map<String, TaskTagData.TaskTag> availableTags = new HashMap<>();
	private final ScrollableItemBar<String> tagBar = new ScrollableItemBar<>(this, 0, 0, 9, SelectionModel.SelectMode.SINGLE);
	private final PaginatedGroup<GameTask> taskGroup = new PaginatedGroup<>(1, 2, 7, 4, this::onTaskClicked, SelectionModel.SelectMode.MULTIPLE_OR_NONE, true);

	private static final ItemTemplate NEXT = new ItemTemplate(0, ItemTypePaper.of(Material.STRUCTURE_VOID),
			PlayerDisplayTranslationKey.MENU_NEXT.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	private static final ItemTemplate PREVIOUS = new ItemTemplate(8, ItemTypePaper.of(Material.BARRIER),
			PlayerDisplayTranslationKey.MENU_PREVIOUS.translate()
					.color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

	public TagManagerMenu(MenuBoard manager, String listName) {
		super(manager, Component.text("Manage task tags"), 6);
		this.listName = listName;
		this.cardData = new BingoCardData();
	}

	@Override
	public void beforeOpening(PlayerHandle player) {
		super.beforeOpening(player);

		List<TaskData> tasks = cardData.lists().getTasks(listName);
		TaskTagData data = new BingoCardData().tags();

		availableTags = data.getAllTags();

		List<ItemTemplate> items = new ArrayList<>();
		List<String> allTags = new ArrayList<>();
		int i = 0;
		for (String tagName : availableTags.keySet()) {
			TaskTagData.TaskTag tag = availableTags.get(tagName);
			items.add(ItemTemplate.createColoredLeather(tag.color(), ItemTypePaper.of(Material.LEATHER_HELMET))
					.setName(Component.text("<" + tagName + ">").color(tag.color())));
			allTags.add(tagName);
			i++;
		}

		tagBar.setItems(items, allTags);
		tagBar.setItemClickedCallback(this::onTagBarClicked);

		for (int j = 0; j < 9; j++) {
			addItem(BasicMenu.BLANK.copyToSlot(j, 1));
		}

		for (int k = 2; k < 6; k++) {
			addItem(BasicMenu.BLANK.copyToSlot(0, k));
		}

		for (int k = 2; k < 6; k++) {
			addItem(BasicMenu.BLANK.copyToSlot(8, k));
		}

		List<ItemTemplate> taskItems = new ArrayList<>();
		List<GameTask> taskData = new ArrayList<>();
		for (TaskData task : tasks) {
			GameTask gameTask = new GameTask(task);
			taskData.add(gameTask);
			taskItems.add(createItemFromTask(gameTask));
		}
		taskGroup.setItems(taskItems, taskData);
		taskGroup.updateVisibleItems(this);

		switchTabs(tagBar.selectedData().getFirst());
		PlatformResolver.get().runTask(t -> {
			taskGroup.updateVisibleItems(this);
			updatePageNavigation(taskGroup);
		});
	}

	@Override
	public void beforeClosing(PlayerHandle player) {
		super.beforeClosing(player);

		List<TaskData> mappedData = taskGroup.allData().stream().map(item -> item.data).toList();
		cardData.lists().saveTasksFromGroup(listName, mappedData, mappedData);
	}

	public void onTagBarClicked(int idx, ItemTemplate item, String tagName) {
		switchTabs(tagName);
		PlatformResolver.get().runTask(t -> {
			taskGroup.updateVisibleItems(this);
		});
	}

	public void switchTabs(String newTag) {
		TaskTagData.TaskTag tag = availableTags.get(newTag);
		taskGroup.selection().reset();

		for (int i = 0; i < taskGroup.allData().size(); i++) {
			GameTask task = taskGroup.allData().get(i);
			if (task.data.tags().contains(newTag)) {
				taskGroup.selection().toggleSlot(i);
			}
		}
	}

	public void onTaskClicked(int slotIndex, GameTask task) {
		if (tagBar.selectedData().isEmpty()) {
			return;
		}

		String selectedTag = tagBar.selectedData().getFirst();

		if (task.data.tags().contains(selectedTag)) {
			task.data.tags().remove(selectedTag);
		} else {
			task.data.tags().add(selectedTag);
		}

		taskGroup.setItem(slotIndex, task, createItemFromTask(task));
		PlatformResolver.get().runTask((t) -> {
			taskGroup.updateVisibleItems(this);
		});
	}

	ItemTemplate createItemFromTask(GameTask task) {
		ItemTemplate item = task.toItem(CardDisplayInfo.DUMMY_DISPLAY_INFO);

		if (task.data.tags().isEmpty()) {
			return item;
		}

		Component tags = Component.empty();
		for (String tag : task.data.tags()) {
			TaskTagData.TaskTag tagInfo = availableTags.getOrDefault(tag, new TaskTagData.TaskTag(NamedTextColor.WHITE));
			tags = tags.append(Component.text("<" + tag + "> ").color(tagInfo.color()));
		}
		item.addDescription("tags", 10, tags);
		return item;
	}

	private void updatePageNavigation(PaginatedGroup<?> page) {
		int currentPage = page.getCurrentPage();
		int pageCount = page.getPageCount();
		Component pageCountDesc = Component.text(String.format("%02d", currentPage + 1) + "/" + String.format("%02d", pageCount));

		ItemTemplate prevPage = PREVIOUS.copyToSlot(0, 5).setLore(pageCountDesc);
		ItemTemplate nextPage = NEXT.copyToSlot(8, 5).setLore(pageCountDesc);

		if (currentPage > 0) {
			addAction(prevPage, (args) -> {
				page.previousPage(this);
				PlatformResolver.get().runTask((t) -> {
					updatePageNavigation(page);
				});
			});
		} else {
			addItem(BLANK.copyToSlot(0, 5));
		}

		if (currentPage < pageCount - 1) {
			addAction(nextPage, (args) -> {
				page.nextPage(this);
				PlatformResolver.get().runTask((t) -> {
					updatePageNavigation(page);
				});
			});
		} else {
			addItem(BLANK.copyToSlot(8, 5));
		}
	}
}
