package me.steven.bingoreloaded;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BingoOptionsUI extends AbstractGUIInventory
{
    private final BingoGame game;

    public final Map<String, ItemStack> menuItems = new HashMap<>(){{
        put("join", new MenuItem(Material.WHITE_GLAZED_TERRACOTTA, "Join A Team"));
        put("leave", new MenuItem(Material.BARRIER, "Quit Bingo"));
    }};

    public BingoOptionsUI(BingoGame game)
    {
        super(45, BingoReloaded.PRINT_PREFIX + ChatColor.DARK_RED + " Options Menu");
        this.game = game;

        inventory.setItem(20, menuItems.get("join"));
        inventory.setItem(24, menuItems.get("leave"));
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(menuItems.get("join").getItemMeta().getDisplayName()))
        {
            game.teamManager.openTeamSelector((Player)event.getWhoClicked(), this);
        }
        else if (itemName.equals(menuItems.get("leave").getItemMeta().getDisplayName()))
        {
            game.playerQuit((Player)event.getWhoClicked());
        }
    }

    @Override
    public void delegateDrag(InventoryDragEvent event)
    {

    }
}
