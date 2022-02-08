package me.steven.bingoreloaded.cards;

import me.steven.bingoreloaded.AbstractGUIInventory;
import me.steven.bingoreloaded.BingoItem;
import me.steven.bingoreloaded.BingoReloaded;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BingoCard extends AbstractGUIInventory
{
    public enum CardDifficulty
    {
        EASY,
        NORMAL,
        HARD,
    }

    public BingoGameMode mode;
    // CARD_SIZE must be between 1 and 6! Denotes the size of the length and width of the bingo card (i.e. size 5 will have 25 total spaces)
    public static int CARD_SIZE = 5;

    public String name = "Bingo Card";
    public ArrayList<BingoItem> items = new ArrayList<>();

    public BingoCard()
    {

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

        for (int i = 0; i < Math.pow(CARD_SIZE, 2); i++)
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

    @Override
    public void showInventory(HumanEntity player)
    {
        this.inventory = Bukkit.createInventory(null, 9 * CARD_SIZE, ChatColor.WHITE + "Bingo Reloaded - " + name);
        for (int i = 0; i < items.size(); i++)
        {
            inventory.setItem(getCardInventorySlot(i), items.get(i).stack);
        }

        player.openInventory(inventory);
    }

    public int getCardInventorySlot(int itemIndex)
    {
        return getCardInventorySlot(itemIndex, 2, 2);
    }

    public int getCardInventorySlot(int itemIndex, int leftSpacing, int rightSpacing)
    {
        int row;
        if (itemIndex == Math.pow(CARD_SIZE, 2) - 1)
        {
            row = CARD_SIZE - 1;
        }
        else
        {
            row = (int) Math.floor(itemIndex / (double)CARD_SIZE);
        }

        return itemIndex + leftSpacing + row * (leftSpacing + rightSpacing);
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
