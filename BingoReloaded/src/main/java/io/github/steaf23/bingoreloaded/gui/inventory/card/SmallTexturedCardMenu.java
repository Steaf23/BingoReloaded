package io.github.steaf23.bingoreloaded.gui.inventory.card;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.gui.inventory.CardMenu;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public class SmallTexturedCardMenu extends TexturedCardMenu
{
    public SmallTexturedCardMenu(MenuBoard board, BingoGamemode mode) {
        super(board, mode, CardSize.X3);
    }

    @Override
    public void updateTasks(List<GameTask> tasks) {
        this.tasks = tasks;
        for (int i = 0; i < tasks.size(); i++) {
            addItem(getItemFromTask(i).setSlot(size.getCardInventorySlot(i) + 9));
        }
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        for (int i = 0; i < tasks.size(); i++)
        {
            addItem(getItemFromTask(i).setSlot(size.getCardInventorySlot(i) + 9));
        }
    }

    @Override
    public CardMenu copy() {
        return new SmallTexturedCardMenu(getMenuBoard(), mode);
    }
}
