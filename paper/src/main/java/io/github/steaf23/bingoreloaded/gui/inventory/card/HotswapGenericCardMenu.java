package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.CardDisplayInfo;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.cards.hotswap.HotswapTaskSlot;
import io.github.steaf23.bingoreloaded.lib.api.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HotswapGenericCardMenu extends GenericCardMenu implements HotswapCardMenu
{
    private List<HotswapTaskSlot> taskHolders;

    public HotswapGenericCardMenu(BingoReloaded bingo, MenuBoard menuBoard, CardDisplayInfo displayInfo, @Nullable Component alternateTitle) {
        super(bingo, menuBoard, displayInfo, alternateTitle);
    }

    public void updateTaskHolders(List<HotswapTaskSlot> holders) {
        this.taskHolders = holders;
    }

    @Override
    public CardMenu copy(@Nullable Component alternateTitle) {
        return new HotswapGenericCardMenu(bingo(), getMenuBoard(), displayInfo(), alternateTitle);
    }

    @Override
    public @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        HotswapTaskSlot holder = taskHolders.get(taskIndex);
        return holder.convertToItem(tasks.get(taskIndex), displayInfo());
    }
}
