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

    public void generateCard(String difficulty)
    {
        List<Material> materials = BingoItem.ITEMS.get("normal");
        Collections.shuffle(materials);

        for (int i = 0; i < Math.pow(CARD_SIZE, 2); i++)
        {
            items.add(new BingoItem(materials.get(i % materials.size())));
        }
    }

    public abstract boolean checkBingo();

    public boolean isItemInCard(Material item)
    {
        BingoReloaded.print("Check out " + item.toString() + "!", null);

        return false;
    }

    public boolean completeItem(Material item)
    {
        return true;
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
        int row;
        if (itemIndex == 24)
        {
            row = 4;
        }
        else
        {
            row = (int) Math.floor(itemIndex / (double)CARD_SIZE);
        }

        return itemIndex + 2 + row * 4;
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
