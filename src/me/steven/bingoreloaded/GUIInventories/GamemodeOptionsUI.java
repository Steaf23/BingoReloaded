package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.BingoGameMode;
import me.steven.bingoreloaded.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GamemodeOptionsUI extends SubGUIInventory
{
    private final BingoGame game;
    private final MenuItem[] options;

    public GamemodeOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(9, "Choose Gamemode", parent);
        this.game = game;
        options = new MenuItem[]{
                new MenuItem(Material.LIME_CONCRETE, TITLE_PREFIX + "Regular", "Your regular Bingo game!", "Complete one line to win (horizontal, vertical or diagonal)."),
                new MenuItem(Material.PURPLE_CONCRETE, TITLE_PREFIX + "Lockout", "Compete to complete the majority of the card to win!"),
                new MenuItem(Material.CYAN_CONCRETE, TITLE_PREFIX + "Complete-All", "Collect all items on the card to win!"),
                new MenuItem(Material.RED_CONCRETE, TITLE_PREFIX + "Rush", "Like regular Bingo but with a 3x3 Card, Spicy!"),
        };
        fillOptions(new int[]{1, 3, 5, 7}, options);
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        if (isMenuItem(event.getCurrentItem(), options[0]))
        {
            game.setGameMode(BingoGameMode.REGULAR);
        }
        else if (isMenuItem(event.getCurrentItem(), options[1]))
        {
            game.setGameMode(BingoGameMode.LOCKOUT);
        }
        else if (isMenuItem(event.getCurrentItem(), options[2]))
        {
            game.setGameMode(BingoGameMode.COMPLETE);
        }
        else if (isMenuItem(event.getCurrentItem(), options[3]))
        {
            game.setGameMode(BingoGameMode.RUSH);
        }
        openParent((Player)event.getWhoClicked());
    }

    @Override
    public void delegateDrag(InventoryDragEvent event)
    {

    }
}
