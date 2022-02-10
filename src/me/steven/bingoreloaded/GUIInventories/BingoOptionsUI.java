package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.MenuItem;
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

    private final Map<String, MenuItem> menuItems = new HashMap<>(){{
        put("join", new MenuItem(Material.WHITE_GLAZED_TERRACOTTA, "" + ChatColor.GOLD + ChatColor.BOLD + "Join A Team"));
        put("leave", new MenuItem(Material.BARRIER, "" + ChatColor.GOLD + ChatColor.BOLD + "Quit Bingo"));
        put("gamemode", new MenuItem(Material.ENCHANTED_BOOK, "" + ChatColor.GOLD + ChatColor.BOLD + "Change Gamemode"));
        put("kit", new MenuItem(Material.IRON_INGOT, "" + ChatColor.GOLD + ChatColor.BOLD + "Change Starting Kit"));
        put("difficulty", new MenuItem(Material.ENDER_EYE, "" + ChatColor.GOLD + ChatColor.BOLD + "Change Bingo Difficulty"));
    }};

    private BingoOptionsUI(BingoGame game)
    {
        super(45, "Options Menu");
        this.game = game;
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        ItemStack item = event.getCurrentItem();

        if (BingoReloaded.areNamesEqual(item, menuItems.get("join")))
        {
            game.teamManager.openTeamSelector((Player)event.getWhoClicked(), this);
        }
        else if (BingoReloaded.areNamesEqual(item, menuItems.get("leave")))
        {
            game.playerQuit((Player)event.getWhoClicked());
        }
        else if (BingoReloaded.areNamesEqual(item, menuItems.get("kit")))
        {
            KitOptionsUI kitSelector = new KitOptionsUI(this, game);
            kitSelector.open(event.getWhoClicked());
        }
        else if (BingoReloaded.areNamesEqual(item, menuItems.get("gamemode")))
        {
            GamemodeOptionsUI gamemodeSelector = new GamemodeOptionsUI(this, game);
            gamemodeSelector.open(event.getWhoClicked());
        }
    }

    @Override
    public void delegateDrag(InventoryDragEvent event)
    {

    }

    public static void open(Player player, BingoGame gameInstance)
    {
        BingoOptionsUI options = new BingoOptionsUI(gameInstance);

        if (player.hasPermission("bingo.admin"))
        {
            options.fillOptions(new int[]{12, 14, 29, 31, 33}, new MenuItem[]{
                    options.menuItems.get("join"),
                    options.menuItems.get("leave"),
                    options.menuItems.get("kit"),
                    options.menuItems.get("gamemode"),
                    options.menuItems.get("difficulty"),
            });
        }
        else
        {
            options.fillOptions(new int[]{21, 23}, new MenuItem[]{
                    options.menuItems.get("join"),
                    options.menuItems.get("leave"),
            });
        }

        options.open(player);
    }
}
