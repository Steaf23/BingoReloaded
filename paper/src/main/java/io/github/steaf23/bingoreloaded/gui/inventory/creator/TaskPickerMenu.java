package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuFilterSettings;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedDataMenu;
import io.github.steaf23.bingoreloaded.lib.inventory.action.ComboBoxButtonAction;
import io.github.steaf23.bingoreloaded.lib.inventory.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;

import java.util.List;
import java.util.stream.Collectors;

public class TaskPickerMenu extends PaginatedDataMenu<GameTask> {

	private final String listName;

	protected static final Component[] SELECTED_LORE = createSelectedLore();
	protected static final Component[] UNSELECTED_LORE = createUnselectedLore();

	public static final ItemTemplate SHOW_SELECTED_ONLY = new ItemTemplate(6, 5, ItemTypePaper.of(Material.OPEN_EYEBLOSSOM), BingoReloaded.applyTitleFormat("Show selected tasks only"));
	public static final ItemTemplate SHOW_ALL = new ItemTemplate(6, 5, ItemTypePaper.of(Material.CLOSED_EYEBLOSSOM), BingoReloaded.applyTitleFormat("Show all tasks"));

	public TaskPickerMenu(MenuBoard board, String initialTitle, List<GameTask> options, String listName) {
		super(board, Component.text(initialTitle), options);
		this.listName = listName;
	}

	@Override
	public void onOptionClickedDelegate(MenuAction.ActionArguments args, GameTask clickedOption) {
		switch (args.clickType()) {
			case LEFT -> incrementItemCount(clickedOption, 1);
			case SHIFT_LEFT -> incrementItemCount(clickedOption, 10);
			case RIGHT -> decrementItemCount(clickedOption, 1);
			case SHIFT_RIGHT -> decrementItemCount(clickedOption, 10);
		}
	}

	@Override
	public Material material(GameTask gameTask, boolean selected) {
		return ((ItemTypePaper)gameTask.icon(CardDisplayInfo.DUMMY_DISPLAY_INFO)).handle();
	}

	@Override
	public Component displayName(GameTask gameTask, boolean selected) {
		return gameTask.getName();
	}

	@Override
	public ItemTemplate editItem(ItemTemplate item, GameTask gameTask, boolean selected) {
		ItemTemplate newItem = gameTask.toItem(CardDisplayInfo.DUMMY_DISPLAY_INFO);

		Component[] addedLore;
		if (selected)
			addedLore = SELECTED_LORE;
		else
			addedLore = UNSELECTED_LORE;

		newItem.setGlowing(selected);
		newItem.setLore(gameTask.data().getItemDescription());
		newItem.addDescription("selected", 5, addedLore);
		return newItem;
	}

	@Override
	public boolean filterByData(GameTask gameTask, MenuFilterSettings filter) {

		return PlainTextComponentSerializer.plainText().serialize(gameTask.data().getName()).toLowerCase().contains(filter.name().toLowerCase());
	}

	@Override
	public void beforeOpening(PlayerHandle player) {
		super.beforeOpening(player);

		BingoCardData cardsData = new BingoCardData();
		List<TaskData> tasks = cardsData.lists().getTasks(listName);

		for (GameTask item : getAllItems()) {
			TaskData itemData = item.data();
			TaskData savedTask = null;
			for (var t : tasks) {
				if (t.isTaskEqual(itemData)) {
					savedTask = t;
					break;
				}
			}

			if (savedTask != null) {
				item.setData(savedTask);
				selectItem(item, true);
			}
		}

		MenuAction filterSelectedAction = new ComboBoxButtonAction.Builder("selected", SHOW_SELECTED_ONLY.copy())
				.addOption("all", SHOW_ALL.copy())
				.setCallback((currentValue, newValue, args) -> {
					setFilterSelected(currentValue.equals("selected"));
					return true;
				})
				.buildAction(ItemTemplate.slotFromXY(6, 5));
		addAction(filterSelectedAction);
	}

	@Override
	public void beforeClosing(PlayerHandle player) {
		super.beforeClosing(player);

		BingoCardData cardsData = new BingoCardData();
		cardsData.lists().saveTasksFromGroup(listName,
				getAllItems().stream().map(GameTask::data).collect(Collectors.toList()),
				getSelectedItems().stream().map(GameTask::data).collect(Collectors.toList()));
	}

	public void incrementItemCount(GameTask item, int by) {
		// When entering this method, the item always needs to be selected by the end.
		// Now just check if the item was already selected prior to this moment.
		boolean alreadySelected = isDataSelected(item);

		int count = item.data().getRequiredAmount();
		int newAmount = count;
		if (alreadySelected) {
			newAmount = Math.min(64, newAmount + by);
			if (newAmount == count)
				return;
		}

		item.setData(item.data().setRequiredAmount(newAmount));
		selectItem(item, true);
	}

	public void decrementItemCount(GameTask item, int by) {
		// When entering this method the item could already be deselected, in which case we return;
		boolean deselect = false;
		if (!isDataSelected(item)) {
			return;
		}

		int count = item.data().getRequiredAmount();
		// If the item is selected and its amount is set to 1 prior to this, then deselect it
		if (count == 1) {
			deselect = true;
		}

		int newAmount = count;
		if (!deselect) {
			newAmount = Math.max(1, newAmount - by);
		}

		item.setData(item.data().setRequiredAmount(newAmount));
		selectItem(item, !deselect);
	}

	private static Component[] createSelectedLore() {
		return new Component[]{
				ComponentUtils.MINI_BUILDER.deserialize("<white><italic> - <dark_purple>This task has been added to the list")};
	}

	private static Component[] createUnselectedLore() {
		return new Component[]{
				ComponentUtils.MINI_BUILDER.deserialize("<white><italic> - <gray>Click to make this task"),
				Component.text("   appear on bingo cards", NamedTextColor.GRAY, TextDecoration.ITALIC)};
	}
}
