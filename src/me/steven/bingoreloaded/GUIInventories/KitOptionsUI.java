package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class KitOptionsUI extends AbstractGUIInventory
{
    public KitOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(9, "Choose Player Kit", parent);
        this.game = game;

        normalItem = new InventoryItem(4, Material.LIME_CONCRETE, TITLE_PREFIX + "Normal");
        fillOptions(new int[]{4}, new InventoryItem[]{normalItem});
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        game.setKit("normal");
        openParent(player);
    }

    private final BingoGame game;
    private final InventoryItem normalItem;
}
