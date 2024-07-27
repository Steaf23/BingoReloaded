package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.cards.HotswapTaskHolder;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HotswapGenericCardMenu extends GenericCardMenu implements HotswapCardMenu
{
    private List<HotswapTaskHolder> taskHolders;

    public HotswapGenericCardMenu(MenuBoard menuBoard, CardSize cardSize) {
        super(menuBoard, BingoGamemode.HOTSWAP, cardSize);
    }

    // Override to only allow tasks getting updated from the task holders.
    @Override
    public void updateTasks(List<GameTask> tasks) {
        super.updateTasks(taskHolders.stream().map(h -> h.task).toList());
    }

    public void updateTaskHolders(List<HotswapTaskHolder> holders) {
        this.taskHolders = holders;
        updateTasks(null);
    }

    @Override
    public CardMenu copy() {
        return new HotswapGenericCardMenu(getMenuBoard(), size);
    }

    @Override
    public @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        HotswapTaskHolder holder = taskHolders.get(taskIndex);
        return holder.convertToItem();
    }
}
