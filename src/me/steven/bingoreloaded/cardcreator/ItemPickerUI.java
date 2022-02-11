package me.steven.bingoreloaded.cardcreator;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemPickerUI extends AbstractGUIInventory
{
    private static final int ITEMS_PER_PAGE = 48;
    private final List<Material> materialList = new ArrayList<>();
    private int pageAmount;
    private int currentPage;

    private static final MenuItem BG_ITEM = new MenuItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.MAGIC + "a", "");
    private static final MenuItem NEXT = new MenuItem(Material.STRUCTURE_VOID, "Next Page", "");
    private static final MenuItem PREVIOUS = new MenuItem(Material.BARRIER, "Previous Page", "");

    public ItemPickerUI()
    {
        super(54, "Item Picker");
        setMaterialList();
        BingoReloaded.broadcast("Amount of pages: " + pageAmount);
        initMenu();

        currentPage = 0;
        fillPage(0);

    }

    @Override
    public void delegateClick(InventoryClickEvent event)
    {
        if (event.getCurrentItem() == null) return;

        if (isMenuItem(event.getCurrentItem(), NEXT))
        {
            currentPage = Math.floorMod(currentPage + 1, pageAmount);
            fillPage(currentPage);
        }
        else if (isMenuItem(event.getCurrentItem(), PREVIOUS))
        {
            currentPage = Math.floorMod(currentPage - 1, pageAmount);
            fillPage(currentPage);
        }
        else if (!isMenuItem(event.getCurrentItem(), BG_ITEM)) //If it is a normal item;
        {
            ItemDifficultySelectionUI difficultySelector = new ItemDifficultySelectionUI(event.getCurrentItem().getType(), this);
            difficultySelector.open(event.getWhoClicked());
        }
    }

    public void fillPage(int pageNumber)
    {
        int startingIndex = pageNumber * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++)
        {
            addOption(getSlotIndexForItem(i), new MenuItem(materialList.get(startingIndex + i), "", "Click to make this item appear", "on bingo cards"));
        }

        String pageCountDesc = String.format("%02d", pageNumber + 1) + "/" + (pageAmount);

        MenuItem next = getOption(17);
        ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta != null)
        {
            nextMeta.setLore(List.of(pageCountDesc));
        }
        next.setItemMeta(nextMeta);
        addOption(17, next);

        MenuItem previous = getOption(44);
        ItemMeta prevMeta = previous.getItemMeta();
        if (prevMeta != null)
        {
            prevMeta.setLore(List.of(pageCountDesc));
        }
        previous.setItemMeta(prevMeta);
        addOption(44, previous);
    }

    public int getSlotIndexForItem(int itemIndex)
    {
        int row;
        if (itemIndex == ITEMS_PER_PAGE - 1) //set correct row for last item
        {
            row = 5;
        }
        else //set correct row for other items
        {
            row = (int) Math.floor(itemIndex / (double)8);
        }

        return itemIndex + row;
    }

    public void setMaterialList()
    {
        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && m.isItem() && !m.isAir())
            {
                materialList.add(m);
            }
        }

        int remainingSpaces = ITEMS_PER_PAGE - (materialList.size() % ITEMS_PER_PAGE);

        BingoReloaded.broadcast("" + remainingSpaces);

        for (int i = remainingSpaces; i > 0; i--)
        {
            materialList.add(Material.AIR);
        }

        pageAmount = (materialList.size() / ITEMS_PER_PAGE);
    }

    public void initMenu()
    {
        fillOptions(new int[]{8, 17, 26, 35, 44, 53}, new MenuItem[]{
                BG_ITEM, NEXT, BG_ITEM, BG_ITEM, PREVIOUS, BG_ITEM,
        });
    }
}
