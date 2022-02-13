package me.steven.bingoreloaded.cardcreator.GUI;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.InventoryItem;
import me.steven.bingoreloaded.cardcreator.BingoItemData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemDifficultySelectionUI extends AbstractGUIInventory
{
    private static final InventoryItem EASY = new InventoryItem(20, Material.LIME_CONCRETE, "Easy", "Click here to set item's", "difficulty to easy.");
    private static final InventoryItem MEDIUM = new InventoryItem(22, Material.ORANGE_CONCRETE, "Medium", "Click here to set item's", "difficulty to medium.");
    private static final InventoryItem HARD = new InventoryItem(24, Material.RED_CONCRETE, "Hard", "Click here to set item's", "difficulty to hard.");
    private static final InventoryItem RESET = new InventoryItem(14, Material.BARRIER, "Reset Item", "Click here to remove item", "from the item list");
    private final InventoryItem item;

    public ItemDifficultySelectionUI(Material material, AbstractGUIInventory parent)
    {
        super(36, "Item Difficulty", parent);
        this.item = new InventoryItem(12, material,"", "Click Here To Cancel");

        fillOptions(new int[]{12, 14, 20, 22, 24}, new InventoryItem[]{item, RESET, EASY, MEDIUM, HARD});
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        if (slotClicked == item.getSlot())
        {
            openParent(player);
        }
        else if (slotClicked == EASY.getSlot())
        {
            saveItemToFile("easy");
            BingoReloaded.print("Set " + item.getType() + "'s difficulty to easy", player);
            openParent(player);
        }
        else if (slotClicked == MEDIUM.getSlot())
        {
            saveItemToFile("medium");
            BingoReloaded.print("Set " + item.getType() + "'s difficulty to medium", player);
            openParent(player);
        }
        else if (slotClicked == HARD.getSlot())
        {
            saveItemToFile("hard");
            BingoReloaded.print("Set " + item.getType() + "'s difficulty to hard", player);
            openParent(player);
        }
        else if (slotClicked == RESET.getSlot())
        {
            BingoItemData.removeItem(item.getType());
            BingoReloaded.print("Removed " + item.getType() + " from the item list", player);
            openParent(player);
        }
    }

    public void saveItemToFile(String difficulty)
    {
        BingoItemData.saveItems(difficulty, item.getType());
    }
}
