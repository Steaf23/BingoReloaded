package me.steven.bingoreloaded.cardcreator;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.GUIInventories.SubGUIInventory;
import me.steven.bingoreloaded.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemDifficultySelectionUI extends SubGUIInventory
{
    public ItemDifficultySelectionUI(Material material, AbstractGUIInventory parent)
    {
        super(36, "Item Difficulty", parent);
        this.item = new CustomItem(material,"", "Click Here To Cancel");

        fillOptions(new int[]{12, 14, 20, 22, 24}, new CustomItem[]{item, RESET, EASY, MEDIUM, HARD});
    }

    @Override
    public void delegateClick(InventoryClickEvent event, ItemStack itemClicked, Player player)
    {
        if (itemClicked == null) return;

        if (isMenuItem(itemClicked, item))
        {
            openParent(player);
        }
        else if (isMenuItem(itemClicked, EASY))
        {
            saveItemToFile("easy");
            BingoReloaded.print("Set " + item.getType() + "'s difficulty to easy", player);
            openParent(player);
        }
        else if (isMenuItem(itemClicked, MEDIUM))
        {
            saveItemToFile("medium");
            BingoReloaded.print("Set " + item.getType() + "'s difficulty to medium", player);
            openParent(player);
        }
        else if (isMenuItem(itemClicked, HARD))
        {
            saveItemToFile("hard");
            BingoReloaded.print("Set " + item.getType() + "'s difficulty to hard", player);
            openParent(player);
        }
        else if (isMenuItem(itemClicked, RESET))
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

    private static final CustomItem EASY = new CustomItem(Material.LIME_CONCRETE, "Easy", "Click here to set item's", "difficulty to easy.");
    private static final CustomItem MEDIUM = new CustomItem(Material.ORANGE_CONCRETE, "Medium", "Click here to set item's", "difficulty to medium.");
    private static final CustomItem HARD = new CustomItem(Material.RED_CONCRETE, "Hard", "Click here to set item's", "difficulty to hard.");
    private static final CustomItem RESET = new CustomItem(Material.BARRIER, "Reset Item", "Click here to remove item", "from the item list");
    private final CustomItem item;
}
