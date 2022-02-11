package me.steven.bingoreloaded.cardcreator;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemPickerUI extends AbstractGUIInventory
{
    public ItemPickerUI()
    {
        super(54, "Item Picker");
        setMaterialList();
        initMenu();

        currentPage = 0;
        fillPage(0);

    }

    @Override
    public void delegateClick(InventoryClickEvent event, ItemStack itemClicked, Player player)
    {
        if (itemClicked == null) return;

        if (isMenuItem(itemClicked, NEXT))
        {
            currentPage = Math.floorMod(currentPage + 1, pageAmount);
            fillPage(currentPage);
        }
        else if (isMenuItem(itemClicked, PREVIOUS))
        {
            currentPage = Math.floorMod(currentPage - 1, pageAmount);
            fillPage(currentPage);
        }
        else if (!isMenuItem(itemClicked, BG_ITEM)) //If it is a normal item;
        {
            ItemDifficultySelectionUI difficultySelector = new ItemDifficultySelectionUI(itemClicked.getType(), this);
            difficultySelector.open(player);
        }
    }

    public void fillPage(int pageNumber)
    {
        int startingIndex = pageNumber * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++)
        {
            addOption(getSlotIndexForItem(i), new CustomItem(materialList.get(startingIndex + i), "", "Click to make this item appear", "on bingo cards"));
        }

        String pageCountDesc = String.format("%02d", pageNumber + 1) + "/" + (pageAmount);

        CustomItem next = getOption(17);
        ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta != null)
        {
            nextMeta.setLore(List.of(pageCountDesc));
        }
        next.setItemMeta(nextMeta);
        addOption(17, next);

        CustomItem previous = getOption(44);
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

        for (int i = remainingSpaces; i > 0; i--)
        {
            materialList.add(Material.AIR);
        }

        pageAmount = (materialList.size() / ITEMS_PER_PAGE);
    }

    public void initMenu()
    {
        fillOptions(new int[]{8, 17, 26, 35, 44, 53}, new CustomItem[]{
                BG_ITEM, NEXT, BG_ITEM, BG_ITEM, PREVIOUS, BG_ITEM,
        });
    }

    private static final int ITEMS_PER_PAGE = 48;
    private final List<Material> materialList = new ArrayList<>();
    private int pageAmount;
    private int currentPage;

    private static final CustomItem BG_ITEM = new CustomItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.MAGIC + "a", "");
    private static final CustomItem NEXT = new CustomItem(Material.STRUCTURE_VOID, "Next Page", "");
    private static final CustomItem PREVIOUS = new CustomItem(Material.BARRIER, "Previous Page", "");
}
