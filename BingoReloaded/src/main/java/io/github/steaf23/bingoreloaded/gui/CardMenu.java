package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.menu.BasicMenu;
import io.github.steaf23.easymenulib.menu.MenuBoard;
import io.github.steaf23.easymenulib.menu.item.ItemTemplate;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CardMenu extends BasicMenu
{
    private final CardSize size;
    private List<BingoTask> tasks;

    public CardMenu(MenuBoard menuBoard, CardSize cardSize, String title)
    {
        super(menuBoard, title, cardSize.size);
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

    public void setInfo(String name, String... description)
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
