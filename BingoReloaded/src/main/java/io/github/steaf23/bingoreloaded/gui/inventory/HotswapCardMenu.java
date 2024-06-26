package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.cards.HotswapTaskHolder;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HotswapCardMenu extends CardMenu
{
    private List<HotswapTaskHolder> taskHolders;

    public HotswapCardMenu(MenuBoard menuBoard, CardSize cardSize, String title) {
        super(menuBoard, cardSize, title);
    }

    // Override to only allow tasks getting updated from the task holders.
    @Override
    public void updateTasks(List<BingoTask> tasks) {
        super.updateTasks(taskHolders.stream().map(h -> h.task).toList());
    }

    public void updateTaskHolders(List<HotswapTaskHolder> holders) {
        this.taskHolders = holders;
        updateTasks(null);
    }

    @Override
    public @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        HotswapTaskHolder holder = taskHolders.get(taskIndex);
        return holder.convertToItem();
    }
}
