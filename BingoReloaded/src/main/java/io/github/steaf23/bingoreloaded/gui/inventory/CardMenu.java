package io.github.steaf23.bingoreloaded.gui.inventory;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CardMenu extends BasicMenu
{
    private final CardSize size;
    private List<BingoTask> tasks;

    public CardMenu(MenuBoard menuBoard, CardSize cardSize, String title)
    {
        super(menuBoard, Component.text(title), cardSize.size);
        this.size = cardSize;
        this.tasks = new ArrayList<>();
        setMaxStackSizeOverride(64);
    }

    public void updateTasks(List<BingoTask> tasks) {
        this.tasks = tasks;
        for (int i = 0; i < tasks.size(); i++)
        {
            addItem(getItemFromTask(i).setSlot(size.getCardInventorySlot(i)));
        }
    }

    public @NotNull ItemTemplate getItemFromTask(int taskIndex) {
        return tasks.get(taskIndex).toItem();
    }

    public void setInfo(Component name, Component... description)
    {
        ItemTemplate info = new ItemTemplate(0, Material.MAP, name, description);
        addItem(info);
    }

    @Override
    public void beforeOpening(HumanEntity player) {
        for (int i = 0; i < tasks.size(); i++)
        {
            addItem(getItemFromTask(i).setSlot(size.getCardInventorySlot(i)));
        }
    }
}
