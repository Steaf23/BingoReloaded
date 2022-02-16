package me.steven.bingoreloaded.GUIInventories;

import me.steven.bingoreloaded.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ItemPickerUI extends AbstractGUIInventory
{
    public abstract void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player);
    public final boolean isSubUI;

    private static final int ITEMS_PER_PAGE = 45;
    private final List<InventoryItem> items;
    private final List<InventoryItem> selectedItems;
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

        fillOptions(new InventoryItem[]{
                PREVIOUS, BG_ITEM.inSlot(46), BG_ITEM.inSlot(47), BG_ITEM.inSlot(48), CLOSE, BG_ITEM.inSlot(50), BG_ITEM.inSlot(51), BG_ITEM.inSlot(52), NEXT,
        });

        currentPage = 0;
        items = options;
        selectedItems = new ArrayList<>();
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
        else if (isSlotValidOption(slotClicked) && items.get(ITEMS_PER_PAGE * currentPage + slotClicked) != null) //If it is a normal item;
        {
            onOptionClickedDelegate(event, items.get(ITEMS_PER_PAGE * currentPage + slotClicked), player);
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

        updatePage();
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

        updatePage();
    }

    public void clearItems()
    {
        items.clear();
        updatePage();
    }

    public List<InventoryItem> getItems()
    {
        return items;
    }

    public List<InventoryItem> getSelectedItems()
    {
        return selectedItems;
    }

    public void selectItem(InventoryItem item, boolean value)
    {
        if (items.contains(item))
        {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            if (value)
            {
                meta.setLore(List.of(ChatColor.DARK_PURPLE + "This item has been added to the list"));
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

                item.setItemMeta(meta);
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);

                selectedItems.add(item);
            }
            else
            {
                meta.setLore(List.of(ChatColor.GRAY + "Click to make this item", ChatColor.GRAY + "appear on bingo cards"));
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

                item.setItemMeta(meta);
                item.removeEnchantment(Enchantment.DURABILITY);
                selectedItems.remove(item);
            }

            items.set(items.indexOf(item), item);
        }
        updatePage();
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
                addOption(items.get(startingIndex + i).inSlot(i));
            else
                addOption(new InventoryItem(i, Material.AIR, "", ""));
        }

        //Update Page description (20/23) for the Next and Previous 'buttons'.
        String pageCountDesc = String.format("%02d", currentPage + 1) + "/" + String.format("%02d", pageAmount);

        InventoryItem next = getOption(NEXT.getSlot());
        ItemMeta nextMeta = next.getItemMeta();
        if (nextMeta != null)
        {
            nextMeta.setLore(List.of(pageCountDesc));
        }
        next.setItemMeta(nextMeta);
        addOption(next);

        InventoryItem previous = getOption(PREVIOUS.getSlot());
        ItemMeta prevMeta = previous.getItemMeta();
        if (prevMeta != null)
        {
            prevMeta.setLore(List.of(pageCountDesc));
        }
        previous.setItemMeta(prevMeta);
        addOption(previous);
    }

    private void updatePageAmount()
    {
        pageAmount = Math.max(1, (int)Math.ceil(items.size() / (double)ITEMS_PER_PAGE));
    }
}
