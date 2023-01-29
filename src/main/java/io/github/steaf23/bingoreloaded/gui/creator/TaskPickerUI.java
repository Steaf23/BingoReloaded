package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.TaskListsData;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.PaginatedPickerUI;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.ItemText;
import io.github.steaf23.bingoreloaded.item.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.item.tasks.CountableTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskPickerUI extends PaginatedPickerUI
{
    private final String listName;

    protected static final ItemText[] SELECTED_LORE = createSelectedLore();
    protected static final ItemText[] UNSELECTED_LORE = createUnselectedLore();

    public TaskPickerUI(List<BingoTask> options, String title, AbstractGUIInventory parent, String listName)
    {
        super(asPickerItems(options), title, parent, FilterType.DISPLAY_NAME);
        this.listName = listName;
        this.setMaxStackSizeOverride(64);
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
    {
        switch (event.getClick())
        {
            case LEFT -> incrementItemCount(clickedOption, 1);
            case SHIFT_LEFT -> incrementItemCount(clickedOption, 10);
            case RIGHT -> decrementItemCount(clickedOption, 1);
            case SHIFT_RIGHT -> decrementItemCount(clickedOption, 10);
        }
    }

    public void incrementItemCount(InventoryItem item, int by)
    {
        // When entering this method, the item always needs to be selected by the end.
        // Now just check if the item was already selected prior to this moment.
        boolean alreadySelected = true;
        if (!getSelectedItems().contains(item))
        {
            alreadySelected = false;
        }

        int newAmount = item.getAmount();
        if (alreadySelected)
        {
            newAmount = Math.min(64, newAmount + by);
            if (newAmount == item.getAmount())
                return;
        }

        InventoryItem newItem = new InventoryItem(getUpdatedTaskItem(BingoTask.fromStack(item), true, newAmount))
                .inSlot(item.getSlot());
        replaceItem(newItem, newItem.getSlot());
        selectItem(newItem, true);
    }

    public void decrementItemCount(InventoryItem item, int by)
    {
        // When entering this method the item could already be deselected, in which case we return;
        boolean deselect = false;
        if (!getSelectedItems().contains(item))
        {
            return;
        }

        // If the item is selected and its amount is set to 1 prior to this, then deselect it
        if (item.getAmount() == 1)
        {
            deselect = true;
        }

        int newAmount = item.getAmount();
        if (!deselect)
        {
            newAmount = Math.max(1, newAmount - by);
        }

        InventoryItem newItem = new InventoryItem(getUpdatedTaskItem(BingoTask.fromStack(item), !deselect, newAmount))
                .inSlot(item.getSlot());
        replaceItem(newItem, newItem.getSlot());
        selectItem(newItem, !deselect);
    }

    @Override
    public void handleOpen(final InventoryOpenEvent event)
    {
        super.handleOpen(event);

        // Load selected tasks from saved data.
        List<BingoTask> tasks = TaskListsData.getTasks(listName);

        for (InventoryItem item : getItems())
        {
            var task = tasks.stream().filter(savedTask -> savedTask.data.isTaskEqual(BingoTask.fromStack(item).data)).findFirst();

            if (task.isPresent())
            {
                int count = 1;
                if (task.get().data instanceof CountableTask countable)
                    count = countable.getCount();

                InventoryItem newItem = new InventoryItem(getUpdatedTaskItem(task.get(), true, count));
                replaceItem(newItem, item);
                selectItem(newItem, true);
            }
        }
    }

    @Override
    public void handleClose(InventoryCloseEvent event)
    {
        super.handleClose(event);

        // Save selected items to saved data, removing any unselected tasks.
        BingoTask[] selectedTasks = getSelectedItems().stream()
                .map(item -> BingoTask.fromStack(item))
                .collect(Collectors.toList()).toArray(new BingoTask[]{});

        TaskListsData.saveTasksFromGroup(listName,
                getItems().stream().map(item -> BingoTask.fromStack(item)).toList(),
                Arrays.stream(selectedTasks).toList());
    }

    public static List<InventoryItem> asPickerItems(List<BingoTask> tasks)
    {
        List<InventoryItem> result = new ArrayList<>();
        tasks.forEach(task -> {
            InventoryItem item = new InventoryItem(getUpdatedTaskItem(task, false, 1));
            item.highlight(false);
            result.add(item);
        });
        return result;
    }

    private static ItemStack getUpdatedTaskItem(BingoTask task, boolean selected, int newCount)
    {
        var item = task.asStack();

        BingoTask newTask = task;
        if (selected)
        {
            if (task.data instanceof CountableTask countable)
            {
                newTask = new BingoTask(countable.updateTask(newCount));
            }
            else
            {
                newTask = task;
            }
            item = newTask.asStack();
        }

        var meta = item.getItemMeta();
        List<String> addedLore;
        if (selected)
            addedLore = Arrays.stream(SELECTED_LORE)
                    .map(text -> text.asLegacyString())
                    .collect(Collectors.toList());
        else
            addedLore = Arrays.stream(UNSELECTED_LORE)
                    .map(text -> text.asLegacyString())
                    .collect(Collectors.toList());
        List<String> newLore = new ArrayList<>();
        newLore.add(newTask.data.getItemDescription()[0].asLegacyString());
        newLore.addAll(addedLore);

        meta.setLore(newLore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemText[] createSelectedLore()
    {
        var text = new ItemText(" - ", ChatColor.WHITE, ChatColor.ITALIC);
        text.addText("This task has been added to the list", ChatColor.DARK_PURPLE);
        return new ItemText[]{text};
    }

    private static ItemText[] createUnselectedLore()
    {
        var text = new ItemText(" - ", ChatColor.WHITE, ChatColor.ITALIC);
        text.addText("Click to make this task", ChatColor.GRAY);
        var text2 = new ItemText("   appear on bingo cards", ChatColor.GRAY, ChatColor.ITALIC);
        return new ItemText[]{text, text2};
    }
}
