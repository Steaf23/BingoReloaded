package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ItemPickerUI extends SubGUIInventory
{
    public abstract void onOptionClickedDelegate(InventoryClickEvent event, ItemStack itemClicked, Player player);

    public ItemPickerUI(@Nullable AbstractGUIInventory parent, List<CustomItem> options)
    {
        super(54, "Item Picker", parent);
        isSubUI = parent != null;

        fillOptions(new int[]{8, 17, 26, 35, 44, 53}, new CustomItem[]{
                BG_ITEM, NEXT, BG_ITEM, BG_ITEM, PREVIOUS, BG_ITEM,
        });

        this.items = options;
        addWhitespace();

        currentPage = 0;
        fillPage(0);

    }

    @Override
    final public void delegateClick(InventoryClickEvent event, ItemStack itemClicked, Player player)
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
            onOptionClickedDelegate(event, itemClicked, player);
        }
    }

    public void fillPage(int pageNumber)
    {
        int startingIndex = pageNumber * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++)
        {
            addOption(getSlotIndexForItem(i), items.get(startingIndex + i));
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

    private static final int ITEMS_PER_PAGE = 48;
    private final List<CustomItem> items;
    private int pageAmount;
    private int currentPage;

    private static final CustomItem BG_ITEM = new CustomItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.MAGIC + "a", "");
    private static final CustomItem NEXT = new CustomItem(Material.STRUCTURE_VOID, "Next Page", "");
    private static final CustomItem PREVIOUS = new CustomItem(Material.BARRIER, "Previous Page", "");

    private final boolean isSubUI;

    private void addWhitespace()
    {
        int remainingSpaces = ITEMS_PER_PAGE - (items.size() % ITEMS_PER_PAGE);

        for (int i = remainingSpaces; i > 0; i--)
        {
            items.add(new CustomItem(Material.AIR, "", ""));
        }

        pageAmount = (items.size() / ITEMS_PER_PAGE);
    }
}
