package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.core.cards.CardSize;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuInventory;
import io.github.steaf23.bingoreloaded.core.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class CardMenu extends MenuInventory
{
    private final CardSize size;

    public CardMenu(CardSize size, String title)
    {
        super(9 * size.cardSize, title, null);
        this.size = size;
        setMaxStackSizeOverride(64);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (!size.taskSlots.contains(slotClicked))
            return;

        BingoTask task = BingoTask.fromStack(event.getCurrentItem());
        if (task == null)
            return;

        BaseComponent base = new TextComponent("\n");
        BaseComponent name = task.data.getItemDisplayName().asComponent();
        name.setBold(true);
        name.setColor(task.nameColor);

        base.addExtra(name);
        base.addExtra("\n - ");
        base.addExtra(task.data.getDescription());

        Message.sendDebugNoPrefix(base, player);
    }

    public void show(Player player, List<BingoTask> tasks)
    {
        for (int i = 0; i < tasks.size(); i++)
        {
            BingoTask task = tasks.get(i);
            addOption(task.asStack().inSlot(size.getCardInventorySlot(i)));
        }
        open(player);
    }

    public void setInfo(String name, String... description)
    {
        InventoryItem info = new InventoryItem(0, Material.MAP, name, description);
        addOption(info);
    }
}
