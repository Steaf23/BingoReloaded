package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class KitOptionsUI extends SubGUIInventory
{
    public KitOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(9, "Choose Player Kit", parent);
        this.game = game;

        normalItem = new CustomItem(Material.LIME_CONCRETE, TITLE_PREFIX + "Normal");
        fillOptions(new int[]{4}, new CustomItem[]{normalItem});
    }

    @Override
    public void delegateClick(InventoryClickEvent event, ItemStack itemClicked, Player player)
    {
        game.setKit("normal");
        openParent(player);
    }

    private final BingoGame game;
    private final CustomItem normalItem;
}
