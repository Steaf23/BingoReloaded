package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class KitOptionsUI extends SubGUIInventory
{
    private final BingoGame game;

    private final MenuItem normalItem;

    public KitOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(9, "Choose Player Kit", parent);
        this.game = game;

        normalItem = new MenuItem(Material.LIME_CONCRETE, TITLE_PREFIX + "Normal");
        fillOptions(new int[]{4}, new MenuItem[]{normalItem});
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {
        game.setKit("normal");
        openParent((Player)event.getWhoClicked());
    }
}
