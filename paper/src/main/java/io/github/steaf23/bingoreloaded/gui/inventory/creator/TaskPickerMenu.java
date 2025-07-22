package io.github.steaf23.bingoreloaded.gui.inventory.creator;

import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.TaskDisplayMode;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.util.ComponentUtils;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.lib.inventory.FilterType;
import io.github.steaf23.bingoreloaded.lib.inventory.PaginatedSelectionMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskPickerMenu extends PaginatedSelectionMenu
{
    private final String listName;

    protected static final Component[] SELECTED_LORE = createSelectedLore();
    protected static final Component[] UNSELECTED_LORE = createUnselectedLore();
    protected static final CardDisplayInfo DUMMY_DISPLAY_INFO = new CardDisplayInfo(
            BingoGamemode.REGULAR,
            CardSize.X5,
            TaskDisplayMode.UNIQUE_TASK_ITEMS,
            TaskDisplayMode.UNIQUE_TASK_ITEMS,
            false);

    public TaskPickerMenu(MenuBoard manager, String title, List<GameTask> options, String listName) {
        super(manager, Component.text(title), asPickerItems(options), FilterType.DISPLAY_NAME);
        this.listName = listName;
        this.setMaxStackSizeOverride(64);
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, PlayerHandle player) {
        switch (event.getClick()) {
            case LEFT -> incrementItemCount(clickedOption, 1);
            case SHIFT_LEFT -> incrementItemCount(clickedOption, 10);
            case RIGHT -> decrementItemCount(clickedOption, 1);
            case SHIFT_RIGHT -> decrementItemCount(clickedOption, 10);
        }
    }

    public void incrementItemCount(ItemTemplate item, int by) {
        // When entering this method, the item always needs to be selected by the end.
        // Now just check if the item was already selected prior to this moment.
        boolean alreadySelected = getSelectedItems().contains(item);

        int newAmount = item.getAmount();
        if (alreadySelected) {
            newAmount = Math.min(64, newAmount + by);
            if (newAmount == item.getAmount())
                return;
        }

        TaskData newData = GameTask.fromItem(item.buildItem()).data;
        ItemTemplate newItem = getUpdatedTaskItem(newData, true, newAmount)
                .copyToSlot(item.getSlot());
        replaceItem(newItem, newItem.getSlot());
        selectItem(newItem, true);
    }

    public void decrementItemCount(ItemTemplate item, int by) {
        // When entering this method the item could already be deselected, in which case we return;
        boolean deselect = false;
        if (!getSelectedItems().contains(item)) {
            return;
        }

        // If the item is selected and its amount is set to 1 prior to this, then deselect it
        if (item.getAmount() == 1) {
            deselect = true;
        }

        int newAmount = item.getAmount();
        if (!deselect) {
            newAmount = Math.max(1, newAmount - by);
        }

        TaskData newData = GameTask.fromItem(item.buildItem()).data;
        ItemTemplate newItem = getUpdatedTaskItem(newData, !deselect, newAmount)
                .copyToSlot(item.getSlot());
        replaceItem(newItem, newItem.getSlot());
        selectItem(newItem, !deselect);
    }

    @Override
    public void beforeOpening(PlayerHandle player) {
        super.beforeOpening(player);

        BingoCardData cardsData = new BingoCardData();
        Set<TaskData> tasks = cardsData.lists().getTasks(listName, true, true);

        for (ItemTemplate item : getAllItems()) {
            TaskData itemData = GameTask.fromItem(item.buildItem()).data;
            TaskData savedTask = null;
            for (var t : tasks) {
                if (t.isTaskEqual(itemData)) {
                    savedTask = t;
                    break;
                }
            }

            if (savedTask != null) {
                int count = savedTask.getRequiredAmount();

                ItemTemplate newItem = getUpdatedTaskItem(itemData, true, count);
                replaceItem(newItem, item);
                selectItem(newItem, true);
            }
        }
    }

    @Override
    public void beforeClosing(PlayerHandle player) {
        super.beforeClosing(player);

        BingoCardData cardsData = new BingoCardData();
        cardsData.lists().saveTasksFromGroup(listName,
                getAllItems().stream().map(item -> GameTask.fromItem(item.buildItem()).data).collect(Collectors.toList()),
                getSelectedItems().stream().map(item -> GameTask.fromItem(item.buildItem()).data).collect(Collectors.toList()));
    }

    public static List<ItemTemplate> asPickerItems(List<GameTask> tasks) {
        List<ItemTemplate> result = new ArrayList<>();
        tasks.forEach(task -> {
            ItemTemplate item = getUpdatedTaskItem(task.data, false, 1);
            item.setGlowing(false);
            result.add(item);
        });
        return result;
    }

    private static ItemTemplate getUpdatedTaskItem(TaskData old, boolean selected, int newCount) {
        TaskData newData = old;
        if (selected) {
            if (newData instanceof StatisticTask statisticTask) {
                newData = new StatisticTask(statisticTask.statistic(), newCount);
            }
            if (newData instanceof ItemTask itemTask) {
                newData = new ItemTask(itemTask.itemType(), newCount);
            }
        }

        GameTask newTask = new GameTask(newData);
        ItemTemplate item = newTask.toItem(DUMMY_DISPLAY_INFO);

        Component[] addedLore;
        if (selected)
            addedLore = SELECTED_LORE;
        else
            addedLore = UNSELECTED_LORE;

        item.setLore(newTask.data.getItemDescription());
        item.addDescription("selected", 5, addedLore);
        return item;
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
