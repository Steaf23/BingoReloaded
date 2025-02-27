package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskHolder;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HotswapGenericCardMenu extends GenericCardMenu implements HotswapCardMenu
{
    private List<HotswapTaskHolder> taskHolders;

    public HotswapGenericCardMenu(MenuBoard menuBoard, CardSize cardSize, boolean allowViewingAllCards, @Nullable Component alternateTitle) {
        super(menuBoard, BingoGamemode.HOTSWAP, cardSize, allowViewingAllCards, alternateTitle);
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
        return new HotswapGenericCardMenu(getMenuBoard(), size, allowViewingOtherCards(), alternateTitle);
    }

    @Override
    public @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        HotswapTaskHolder holder = taskHolders.get(taskIndex);
        return holder.convertToItem();
    }
}
