package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.PaginatedPickerUI;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.BingoTask;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskPickerUI extends PaginatedPickerUI
{
    private final List<BingoTask> tasks;
    private final String listName;

    public TaskPickerUI(List<BingoTask> options, String title, AbstractGUIInventory parent, String listName)
    {
        super(getItems(options), title, parent, FilterType.DISPLAY_NAME);
        this.tasks = new ArrayList<>();
        this.listName = listName;
    }

    public void addTasks(BingoTask... tasks)
    {
        this.tasks.addAll(Arrays.stream(tasks).toList());
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
    {
        if (event.getClick() == ClickType.LEFT)
        {
            incrementItemCount(clickedOption);
        }
        else if (event.getClick() == ClickType.RIGHT)
        {
            decrementItemCount(clickedOption);
        }
    }

    public void incrementItemCount(InventoryItem item)
    {
        boolean select = false;
        if (!getSelectedItems().contains(item))
        {
            select = true;
        }

        if (!select)
        {
            item.setAmount(Math.min(item.getMaxStackSize(), item.getAmount() + 1));
        }

        if (select)
        {
            selectItem(item,true);
        }
        updatePage();
    }

    public void decrementItemCount(InventoryItem item)
    {
        boolean deselect = false;
        if (getSelectedItems().contains(item))
        {
            if (item.getAmount() == 1)
            {
                deselect = true;
            }
        }
        item.setAmount(Math.max(1, item.getAmount() - 1));

        if (deselect)
        {
            selectItem(item, false);
        }
        updatePage();
    }

    protected void loadSelectedItems()
    {
//        List<ItemTask> items = BingoTasksData.getItemTasks(listName);
//        List<InventoryItem> allItems = getItems();
//
//        items.forEach(task -> {
//            String mat = task.material.name();
//            Optional<InventoryItem> item = allItems.stream().filter((i) -> i.getType().name().equals(mat)).findFirst();
//            item.ifPresent(inventoryItem -> {
//                selectItem(inventoryItem, true);
//                inventoryItem.setAmount(task.getCount());
//            });
//        });
//        updatePage();
    }

    @Override
    public void handleOpen(final InventoryOpenEvent event)
    {
        super.handleOpen(event);
        loadSelectedItems();
    }

    public static List<InventoryItem> getItems(List<BingoTask> tasks)
    {
        List<InventoryItem> result = new ArrayList<>();
        tasks.forEach(task -> result.add(new InventoryItem(task.asStack())));
        return result;
    }
}
