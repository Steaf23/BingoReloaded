package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.CountableTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;

import io.github.steaf23.easymenulib.inventory.FilterType;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.inventory.PaginatedSelectionMenu;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskPickerMenu extends PaginatedSelectionMenu
{
    private final String listName;

    protected static final BaseComponent[] SELECTED_LORE = createSelectedLore();
    protected static final BaseComponent[] UNSELECTED_LORE = createUnselectedLore();

    public TaskPickerMenu(MenuBoard manager, String title, List<BingoTask> options, String listName) {
        super(manager, title, asPickerItems(options), FilterType.DISPLAY_NAME);
        this.listName = listName;
        this.setMaxStackSizeOverride(64);
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemTemplate clickedOption, HumanEntity player) {
        switch (event.getClick()) {
            case LEFT -> incrementItemCount(clickedOption, 1);
            case SHIFT_LEFT -> incrementItemCount(clickedOption, 10);
            case RIGHT -> decrementItemCount(clickedOption, 1);
            case SHIFT_RIGHT -> decrementItemCount(clickedOption, 10);
        }
    }

    public TaskData incrementItemCount(ItemTemplate item, int by) {
        // When entering this method, the item always needs to be selected by the end.
        // Now just check if the item was already selected prior to this moment.
        boolean alreadySelected = getSelectedItems().contains(item);

        int newAmount = item.getAmount();
        if (alreadySelected) {
            newAmount = Math.min(64, newAmount + by);
            if (newAmount == item.getAmount())
                return null;
        }

        TaskData newData = BingoTask.fromItem(item.buildItem()).data;
        ItemTemplate newItem = getUpdatedTaskItem(newData, true, newAmount)
                .copyToSlot(item.getSlot());
        replaceItem(newItem, newItem.getSlot());
        selectItem(newItem, true);

        return newData;
    }

    public TaskData decrementItemCount(ItemTemplate item, int by) {
        // When entering this method the item could already be deselected, in which case we return;
        boolean deselect = false;
        if (!getSelectedItems().contains(item)) {
            return null;
        }

        // If the item is selected and its amount is set to 1 prior to this, then deselect it
        if (item.getAmount() == 1) {
            deselect = true;
        }

        int newAmount = item.getAmount();
        if (!deselect) {
            newAmount = Math.max(1, newAmount - by);
        }

        TaskData newData = BingoTask.fromItem(item.buildItem()).data;
        ItemTemplate newItem = getUpdatedTaskItem(newData, !deselect, newAmount)
                .copyToSlot(item.getSlot());
        replaceItem(newItem, newItem.getSlot());
        selectItem(newItem, !deselect);

        return newData;
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        super.beforeOpening(player);

        BingoCardData cardsData = new BingoCardData();
        Set<TaskData> tasks = cardsData.lists().getTasks(listName, true, true);

        for (ItemTemplate item : getAllItems()) {
            TaskData itemData = BingoTask.fromItem(item.buildItem()).data;
            TaskData savedTask = null;
            for (var t : tasks) {
                if (t.isTaskEqual(itemData)) {
                    savedTask = t;
                    break;
                }
            }

            if (savedTask != null) {
                int count = 1;
                if (savedTask instanceof CountableTask countable)
                    count = countable.getCount();

                ItemTemplate newItem = getUpdatedTaskItem(itemData, true, count);
                replaceItem(newItem, item);
                selectItem(newItem, true);
            }
        }
    }

    @Override
    public void beforeClosing(HumanEntity player) {
        super.beforeClosing(player);

        BingoCardData cardsData = new BingoCardData();
        cardsData.lists().saveTasksFromGroup(listName,
                getAllItems().stream().map(item -> BingoTask.fromItem(item.buildItem()).data).collect(Collectors.toList()),
                getSelectedItems().stream().map(item -> BingoTask.fromItem(item.buildItem()).data).collect(Collectors.toList()));
    }

    public static List<ItemTemplate> asPickerItems(List<BingoTask> tasks) {
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
            if (newData instanceof CountableTask countable) {
                newData = countable.updateTask(newCount);
            }
        }

        BingoTask newTask = new BingoTask(newData);
        ItemTemplate item = newTask.toItem();
        item.setAction(null);

        BaseComponent[] addedLore;
        if (selected)
            addedLore = SELECTED_LORE;
        else
            addedLore = UNSELECTED_LORE;

        item.setLore(newTask.data.getItemDescription());
        item.addDescription("selected", 5, addedLore);
        return item;
    }

    private static BaseComponent[] createSelectedLore() {
        ComponentBuilder builder = new ComponentBuilder(" - ").color(ChatColor.WHITE).italic(true)
                .append("This task has been added to the list").color(ChatColor.DARK_PURPLE);
        return new BaseComponent[]{builder.build()};
    }

    private static BaseComponent[] createUnselectedLore() {
        BaseComponent text = ChatComponentUtils.convert(" - ", ChatColor.WHITE, ChatColor.ITALIC);
        text.addExtra(ChatComponentUtils.convert("Click to make this task", ChatColor.GRAY));
        BaseComponent text2 = ChatComponentUtils.convert("   appear on bingo cards", ChatColor.GRAY, ChatColor.ITALIC);
        return new BaseComponent[]{text, text2};
    }
}
