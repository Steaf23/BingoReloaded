package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.TaskTagData;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.BasicMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.group.PaginatedGroup;
import io.github.steaf23.bingoreloaded.lib.inventory.group.ScrollableItemBar;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TagManagerMenu extends BasicMenu {

	private final String listName;
	private final TaskData.TaskType taskType;

	private Map<String, TaskTagData.TaskTag> availableTags = new HashMap<>();
	private final ScrollableItemBar<String> tagBar = new ScrollableItemBar<>(this, 0, 0, 9, ScrollableItemBar.SelectMode.SINGLE);;
	private final PaginatedGroup<GameTask> taskGroup = new PaginatedGroup<>(1, 2, 7, 4, this::onTaskClicked);

	public TagManagerMenu(MenuBoard manager, TaskData.TaskType taskType, String listName) {
		super(manager, Component.text("Manage task tags"), 6);
		this.taskType = taskType;
		this.listName = listName;
	}

	@Override
	public void beforeOpening(PlayerHandle player) {
		super.beforeOpening(player);

		BingoCardData cardData = new BingoCardData();
		Set<TaskData> tasks = cardData.lists().getTasks(listName, EnumSet.of(taskType));
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

			ItemTemplate item = gameTask.toItem(CardDisplayInfo.DUMMY_DISPLAY_INFO);
			i = 0;
			for (String tag : gameTask.data.tags()) {
				TaskTagData.TaskTag tagInfo = availableTags.getOrDefault(tag, new TaskTagData.TaskTag(NamedTextColor.WHITE));
				item.addDescription("tag_" + tag, 10 + i, Component.text("<" + tag + ">").color(tagInfo.color()));
				i++;
			}
			taskItems.add(item);
		}
		taskGroup.setItems(taskItems, taskData);
		taskGroup.updateVisibleItems(this);
	}

	public void onTagBarClicked(int idx, ItemTemplate item, String tagName) {
		TaskTagData.TaskTag tag = availableTags.get(tagName);
	}

	public void onTaskClicked(GameTask task) {
		ConsoleMessenger.log(task.getName());
	}
}
