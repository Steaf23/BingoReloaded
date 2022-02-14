package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.BingoGameMode;
import me.steven.bingoreloaded.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GamemodeOptionsUI extends AbstractGUIInventory
{
    public GamemodeOptionsUI(AbstractGUIInventory parent, BingoGame game)
    {
        super(9, "Choose Gamemode", parent);
        this.game = game;
        options = new InventoryItem[]{
                new InventoryItem(1, Material.LIME_CONCRETE, TITLE_PREFIX + "Regular", "Your regular Bingo game!", "Complete one line to win (horizontal, vertical or diagonal)."),
                new InventoryItem(3, Material.PURPLE_CONCRETE, TITLE_PREFIX + "Lockout", "Compete to complete the majority of the card to win!"),
                new InventoryItem(5, Material.CYAN_CONCRETE, TITLE_PREFIX + "Complete-All", "Collect all items on the card to win!"),
                new InventoryItem(7, Material.RED_CONCRETE, TITLE_PREFIX + "Rush", "Like regular Bingo but with a 3x3 Card, Spicy!"),
        };
        fillOptions(new int[]{1, 3, 5, 7}, options);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        if (slotClicked == options[0].getSlot())
        {
            game.setGameMode(BingoGameMode.REGULAR);
        }
        else if (slotClicked == options[1].getSlot())
        {
            game.setGameMode(BingoGameMode.LOCKOUT);
        }
        else if (slotClicked == options[2].getSlot())
        {
            game.setGameMode(BingoGameMode.COMPLETE);
        }
        else if (slotClicked == options[3].getSlot())
        {
            game.setGameMode(BingoGameMode.RUSH);
        }
        close(player);
    }

    private final BingoGame game;
    private final InventoryItem[] options;
}
