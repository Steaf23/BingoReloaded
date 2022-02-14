package me.steven.bingoreloaded.cardcreator.GUI;

import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.InventoryItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class ListValueEditorGUI extends AbstractGUIInventory
{
    private static final InventoryItem HIGHER = new InventoryItem(13, Material.GUNPOWDER, "More", "");
    private static final InventoryItem LOWER = new InventoryItem(31, Material.HOPPER, "Less", "");
    private final InventoryItem counter = new InventoryItem(22, Material.TARGET, " ", "");
    private final InventoryItem SAVE = new InventoryItem(41, Material.DIAMOND, "Save and go back", "");

    private final CardEditorUI cardEditor;

    public int itemCount = 1;
    private String listName;

    public ListValueEditorGUI(CardEditorUI parent, String listName, int startingValue)
    {
        super(45, "Updating Values", parent);
        this.cardEditor = parent;
        this.listName = listName;

        this.itemCount = startingValue;

        fillOptions(new int[]{13, 22, 31, 41}, new InventoryItem[]{HIGHER, counter, LOWER, SAVE});
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player)
    {
        if (slotClicked == HIGHER.getSlot())
        {
            updateCounter(itemCount + 1);
        }
        else if (slotClicked == LOWER.getSlot())
        {
            updateCounter(itemCount - 1);
        }
        else if (slotClicked == SAVE.getSlot())
        {
            setValueForList();
            close(player);
        }
    }

    public void updateCounter(int newValue)
    {
        itemCount = Math.floorMod(newValue, 36);

        counter.setAmount(itemCount);
        ItemMeta meta = counter.getItemMeta();

        if (meta == null) return;
        meta.setDisplayName("");
        counter.setItemMeta(meta);
        addOption(counter.getSlot(), counter);
    }

    private void setValueForList()
    {
        cardEditor.updateListValues(listName, itemCount);
    }
}
