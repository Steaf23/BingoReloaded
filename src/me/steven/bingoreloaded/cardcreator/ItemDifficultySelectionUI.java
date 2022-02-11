package me.steven.bingoreloaded.cardcreator;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.GUIInventories.SubGUIInventory;
import me.steven.bingoreloaded.ItemDataManager;
import me.steven.bingoreloaded.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemDifficultySelectionUI extends SubGUIInventory
{
    private static final MenuItem EASY = new MenuItem(Material.LIME_CONCRETE, "Easy", "Click here to set item's", "difficulty to easy.");
    private static final MenuItem MEDIUM = new MenuItem(Material.ORANGE_CONCRETE, "Medium", "Click here to set item's", "difficulty to medium.");
    private static final MenuItem HARD = new MenuItem(Material.RED_CONCRETE, "Hard", "Click here to set item's", "difficulty to hard.");

    private final MenuItem item;

    public ItemDifficultySelectionUI(Material material, AbstractGUIInventory parent)
    {
        super(27, "Item Difficulty", parent);
        this.item = new MenuItem(material,"", "Click Here To Cancel");

        fillOptions(new int[]{4, 20, 22, 24}, new MenuItem[]{item, EASY, MEDIUM, HARD});
    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {
        if (event.getCurrentItem() == null) return;

        if (isMenuItem(event.getCurrentItem(), item))
        {
            openParent(event.getWhoClicked());
        }
        else if (isMenuItem(event.getCurrentItem(), EASY))
        {
            saveItemToFile("easy", item);
            BingoReloaded.print("Set " + item.getType() + "'s difficulty to easy", (Player)event.getWhoClicked());
            openParent(event.getWhoClicked());
        }
        else if (isMenuItem(event.getCurrentItem(), MEDIUM))
        {
            saveItemToFile("medium", item);
            BingoReloaded.print("Set " + item.getType() + "'s difficulty to medium", (Player)event.getWhoClicked());
            openParent(event.getWhoClicked());
        }
        else if (isMenuItem(event.getCurrentItem(), HARD))
        {
            saveItemToFile("hard", item);
            BingoReloaded.print("Set " + item.getType() + "'s difficulty to hard", (Player)event.getWhoClicked());
            openParent(event.getWhoClicked());
        }
    }

    public void saveItemToFile(String difficulty, ItemStack stack)
    {
        ItemDataManager.saveItem(difficulty, stack);
    }
}
