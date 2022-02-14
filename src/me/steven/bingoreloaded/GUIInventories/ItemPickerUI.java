package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public abstract class ItemPickerUI extends AbstractGUIInventory
{
    public abstract void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player);
    public final boolean isSubUI;

    private static final int ITEMS_PER_PAGE = 45;
    private final List<InventoryItem> items;
    private int pageAmount;
    private int currentPage;

    private static final InventoryItem BG_ITEM = new InventoryItem(Material.BLACK_STAINED_GLASS_PANE, " ", "");
    private static final InventoryItem NEXT = new InventoryItem(53, Material.STRUCTURE_VOID, "Next page", "");
    private static final InventoryItem PREVIOUS = new InventoryItem(45, Material.BARRIER, "Previous page", "");
    private static final InventoryItem CLOSE = new InventoryItem(49, Material.REDSTONE, "Close Menu", "");
    public ItemPickerUI(List<InventoryItem> options, String title, AbstractGUIInventory parent)
    {
        super(54, title != null ? title : "Item Picker", parent);
        isSubUI = parent != null;

        fillOptions(new int[]{45, 46, 47, 48, 49, 50, 51, 52, 53}, new InventoryItem[]{
                PREVIOUS, BG_ITEM, BG_ITEM, BG_ITEM, CLOSE, BG_ITEM, BG_ITEM, BG_ITEM, NEXT,
        });

        currentPage = 0;
        items = options;
        updatePageAmount();

        updatePage();

    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        if (slotClicked == NEXT.getSlot())
        {
            nextPage();
        }
        else if (slotClicked == PREVIOUS.getSlot())
        {
            previousPage();
        }
        else if (slotClicked == CLOSE.getSlot())
        {
            close(player);
        }
        else if (isSlotValidOption(slotClicked) && getOption(slotClicked) != null) //If it is a normal item;
        {
            onOptionClickedDelegate(event, getOption(slotClicked), player);
        }
    }

    protected boolean isSlotValidOption(int slot)
    {
        return slot <= 44;
    }

    public void addItems(InventoryItem... newItems)
    {
        //first remove any previous whitespace
        while (items.size() > 0)
        {
            InventoryItem lastItem = items.get(items.size() - 1);

            if (lastItem.getType().isAir())
                items.remove(lastItem);
            else

                break;
        }

        Collections.addAll(items, newItems);

        updatePageAmount();
    }

    public void removeItems(int... itemIndices)
    {
        //first remove any previous whitespace
        while (items.size() > 0)
        {
            InventoryItem lastItem = items.get(items.size() - 1);

            if (lastItem.getType().isAir())
                items.remove(lastItem);
            else
                break;
        }

        for (int i : itemIndices)
            items.remove(i);

        updatePageAmount();
    }

    public void clearItems()
    {
        items.clear();
        updatePageAmount();
    }

    protected void nextPage()
    {
        updatePageAmount();
        currentPage = Math.floorMod(currentPage + 1, pageAmount);
        updatePage();
    }

    protected void previousPage()
    {
        updatePageAmount();
        currentPage = Math.floorMod(currentPage - 1, pageAmount);
        updatePage();
    }

    protected void updatePage()
    {
        updatePageAmount();

        int startingIndex = currentPage * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE; i++)
        {
            if (startingIndex + i < items.size())
                addOption(i, items.get(startingIndex + i));
            else
                addOption(i, new InventoryItem(Material.AIR, "", ""));
        }

        String pageCountDesc = String.format("%02d", currentPage + 1) + "/" + String.format("%02d", pageAmount);

        InventoryItem next = getOption(NEXT.getSlot());
        ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta != null)
        {
            nextMeta.setLore(List.of(pageCountDesc));
        }
        next.setItemMeta(nextMeta);
        addOption(NEXT.getSlot(), next);

        InventoryItem previous = getOption(PREVIOUS.getSlot());
        ItemMeta prevMeta = previous.getItemMeta();
        if (prevMeta != null)
        {
            prevMeta.setLore(List.of(pageCountDesc));
        }
        previous.setItemMeta(prevMeta);
        addOption(PREVIOUS.getSlot(), previous);
    }

    private void updatePageAmount()
    {
        pageAmount = Math.max(1, (int)Math.ceil(items.size() / (double)ITEMS_PER_PAGE));
    }
}
