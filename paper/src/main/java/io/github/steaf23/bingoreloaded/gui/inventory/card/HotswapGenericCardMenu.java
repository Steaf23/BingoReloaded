package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskHolder;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.lib.item.TaskItemConverter;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HotswapGenericCardMenu extends GenericCardMenu implements HotswapCardMenu
{
    private List<HotswapTaskHolder> taskHolders;

    public HotswapGenericCardMenu(BingoReloaded bingo, MenuBoard menuBoard, CardDisplayInfo displayInfo, @Nullable Component alternateTitle) {
        super(bingo, menuBoard, displayInfo, alternateTitle);
    }

    // Override to only allow tasks getting updated from the task holders.
    @Override
    public void updateTasks(List<GameTask> tasks) {
        super.updateTasks(taskHolders.stream().map(HotswapTaskHolder::getTask).toList());
    }

    public void updateTaskHolders(List<HotswapTaskHolder> holders) {
        this.taskHolders = holders;
        updateTasks(null);
    }

    @Override
    public CardMenu copy(@Nullable Component alternateTitle) {
        return new HotswapGenericCardMenu(bingo(), getMenuBoard(), displayInfo(), alternateTitle);
    }

    @Override
    public @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        HotswapTaskHolder holder = taskHolders.get(taskIndex);
        return TaskItemConverter.hotswapTaskToItem(holder, displayInfo());
    }
}
