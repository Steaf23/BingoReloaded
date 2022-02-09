package me.steven.bingoreloaded.cards;

import me.steven.bingoreloaded.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.*;

public abstract class BingoCard extends AbstractGUIInventory
{
    public enum CardDifficulty
    {
        NORMAL,
    }

    // CARD_SIZE must be between 1 and 6! Denotes the size of the length and width of the bingo card (i.e. size 5 will have 25 total spaces)
    public static CardSize size = CardSize.X5;

    public String name = "Bingo Card";
    public ArrayList<BingoItem> items = new ArrayList<>();

    public BingoCard()
    {
        super(9 * size.cardSize, "Bingo Card Menu");
    }

    public static BingoCard fromMode(BingoGameMode mode)
    {
        return switch (mode) {
            case REGULAR -> new RegularBingoCard();
            case LOCKOUT -> new LockoutBingoCard();
            case COMPLETE -> new CompleteBingoCard();
            case RUSH -> new RushBingoCard();
        };
    }

    public void generateCard(CardDifficulty difficulty)
    {
        List<Material> materials = BingoItem.ITEMS.get(difficulty);
        Collections.shuffle(materials);

        for (int i = 0; i < size.fullCardSize; i++)
        {
            items.add(new BingoItem(materials.get(i % materials.size())));
        }
    }

    public abstract boolean hasBingo();

    public boolean completeItem(Material item)
    {
        for (BingoItem bingoItem : items)
        {
            if (bingoItem.stack.getType() == item && !bingoItem.isCompleted())
            {
                bingoItem.complete();
                return true;
            }
        }

        return false;
    }

    public void showInventory(HumanEntity player)
    {
        this.inventory = Bukkit.createInventory(null, 9 * size.cardSize, ChatColor.WHITE + "Bingo Reloaded - " + name);
        for (int i = 0; i < items.size(); i++)
        {
            inventory.setItem(size.getCardInventorySlot(i), items.get(i).stack);
        }

        player.openInventory(inventory);
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {

    }

    @Override
    public void delegateDrag(InventoryDragEvent event)
    {

    }
}
