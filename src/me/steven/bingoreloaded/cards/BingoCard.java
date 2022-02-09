package me.steven.bingoreloaded.cards;

import me.steven.bingoreloaded.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class BingoCard extends AbstractGUIInventory
{
    public enum CardDifficulty
    {
        NORMAL,
    }

    public BingoGame game;
    // CARD_SIZE must be between 1 and 6! Denotes the size of the length and width of the bingo card (i.e. size 5 will have 25 total spaces)
    public static CardSize size = CardSize.X5;

    public String name = "Bingo Card";
    public ArrayList<BingoItem> items = new ArrayList<>();
    public final Map<String, ItemStack> menuItems = new HashMap<>(){{
        put("join", new MenuItem(Material.WHITE_GLAZED_TERRACOTTA, "Join A Team"));
        put("leave", new MenuItem(Material.BARRIER, "Quit Bingo"));
    }};

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

        fillMenuItems();

        player.openInventory(inventory);
    }

    public void fillMenuItems()
    {
        switch (size)
        {
            case X1 -> {
                inventory.setItem(6, menuItems.get("join"));
                inventory.setItem(8, menuItems.get("leave"));
            }
            case X2 -> {
                inventory.setItem(7, menuItems.get("join"));
                inventory.setItem(16, menuItems.get("leave"));
            }
            case X3 -> {
                inventory.setItem(7, menuItems.get("join"));
                inventory.setItem(25, menuItems.get("leave"));
            }
            case X4 -> {
                inventory.setItem(7, menuItems.get("join"));
                inventory.setItem(34, menuItems.get("leave"));
            }
            case X5 -> {
                inventory.setItem(17, menuItems.get("join"));
                inventory.setItem(35, menuItems.get("leave"));
            }
            case X6 -> {
                inventory.setItem(17, menuItems.get("join"));
                inventory.setItem(44, menuItems.get("leave"));
            }
        }
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        BingoReloaded.print(itemName + " " + menuItems.get("join").getItemMeta().getDisplayName());
        if (itemName.equals(menuItems.get("join").getItemMeta().getDisplayName()))
        {
            game.playerJoin((Player)event.getWhoClicked());
        }
        else if (itemName.equals(menuItems.get("leave").getItemMeta().getDisplayName()))
        {

        }
    }

    @Override
    public void delegateDrag(InventoryDragEvent event)
    {

    }
}
