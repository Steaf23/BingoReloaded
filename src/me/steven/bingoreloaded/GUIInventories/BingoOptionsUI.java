package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BingoOptionsUI extends AbstractGUIInventory
{
    @Override
    public void delegateClick(InventoryClickEvent event, ItemStack itemClicked, Player player)
    {
        if (itemClicked == null) return;
        if (itemClicked.getItemMeta() == null) return;

        if (isMenuItem(itemClicked, menuItems.get("join")))
        {
            game.getTeamManager().openTeamSelector(player, this);
        }
        else if (isMenuItem(itemClicked, menuItems.get("leave")))
        {
            game.playerQuit(player);
        }
        else if (isMenuItem(itemClicked, menuItems.get("kit")))
        {
            KitOptionsUI kitSelector = new KitOptionsUI(this, game);
            kitSelector.open(player);
        }
        else if (isMenuItem(itemClicked, menuItems.get("gamemode")))
        {
            GamemodeOptionsUI gamemodeSelector = new GamemodeOptionsUI(this, game);
            gamemodeSelector.open(player);
        }
    }

    public static void open(Player player, BingoGame gameInstance)
    {
        BingoOptionsUI options = new BingoOptionsUI(gameInstance);

        if (player.hasPermission("bingo.admin"))
        {
            options.fillOptions(new int[]{12, 14, 29, 31, 33}, new CustomItem[]{
                    options.menuItems.get("join"),
                    options.menuItems.get("leave"),
                    options.menuItems.get("kit"),
                    options.menuItems.get("gamemode"),
                    options.menuItems.get("difficulty"),
            });
        }
        else
        {
            options.fillOptions(new int[]{21, 23}, new CustomItem[]{
                    options.menuItems.get("join"),
                    options.menuItems.get("leave"),
            });
        }

        options.open(player);
    }

    private final BingoGame game;

    private final Map<String, CustomItem> menuItems = new HashMap<>(){{
        put("join", new CustomItem(Material.WHITE_GLAZED_TERRACOTTA, "" + ChatColor.GOLD + ChatColor.BOLD + "Join A Team"));
        put("leave", new CustomItem(Material.BARRIER, "" + ChatColor.GOLD + ChatColor.BOLD + "Quit Bingo"));
        put("gamemode", new CustomItem(Material.ENCHANTED_BOOK, "" + ChatColor.GOLD + ChatColor.BOLD + "Change Gamemode"));
        put("kit", new CustomItem(Material.IRON_INGOT, "" + ChatColor.GOLD + ChatColor.BOLD + "Change Starting Kit"));
        put("difficulty", new CustomItem(Material.ENDER_EYE, "" + ChatColor.GOLD + ChatColor.BOLD + "Change Bingo Difficulty"));
    }};

    private BingoOptionsUI(BingoGame game)
    {
        super(45, "Options Menu");
        this.game = game;
    }
}
