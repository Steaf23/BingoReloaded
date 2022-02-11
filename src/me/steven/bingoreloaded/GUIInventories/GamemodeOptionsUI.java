package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.BingoGameMode;
import me.steven.bingoreloaded.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GamemodeOptionsUI extends SubGUIInventory
{
    public GamemodeOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(9, "Choose Gamemode", parent);
        this.game = game;
        options = new CustomItem[]{
                new CustomItem(Material.LIME_CONCRETE, TITLE_PREFIX + "Regular", "Your regular Bingo game!", "Complete one line to win (horizontal, vertical or diagonal)."),
                new CustomItem(Material.PURPLE_CONCRETE, TITLE_PREFIX + "Lockout", "Compete to complete the majority of the card to win!"),
                new CustomItem(Material.CYAN_CONCRETE, TITLE_PREFIX + "Complete-All", "Collect all items on the card to win!"),
                new CustomItem(Material.RED_CONCRETE, TITLE_PREFIX + "Rush", "Like regular Bingo but with a 3x3 Card, Spicy!"),
        };
        fillOptions(new int[]{1, 3, 5, 7}, options);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, ItemStack itemClicked, Player player)
    {
        if (itemClicked == null) return;
        if (itemClicked.getItemMeta() == null) return;

        if (isMenuItem(itemClicked, options[0]))
        {
            game.setGameMode(BingoGameMode.REGULAR);
        }
        else if (isMenuItem(itemClicked, options[1]))
        {
            game.setGameMode(BingoGameMode.LOCKOUT);
        }
        else if (isMenuItem(itemClicked, options[2]))
        {
            game.setGameMode(BingoGameMode.COMPLETE);
        }
        else if (isMenuItem(itemClicked, options[3]))
        {
            game.setGameMode(BingoGameMode.RUSH);
        }
        openParent(player);
    }

    private final BingoGame game;
    private final CustomItem[] options;
}
