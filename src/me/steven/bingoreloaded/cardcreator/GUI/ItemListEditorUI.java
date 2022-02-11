package me.steven.bingoreloaded.cardcreator.GUI;

import me.steven.bingoreloaded.CustomItem;
import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.GUIInventories.ItemPickerUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemListEditorUI extends ItemPickerUI
{
    public ItemListEditorUI(@Nullable AbstractGUIInventory parent)
    {
        super(parent, createMaterialList());
    }

    private static List<CustomItem> createMaterialList()
    {
        List<CustomItem> result = new ArrayList<>();

        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && m.isItem() && !m.isAir())
            {
                result.add(new CustomItem(m, "", "Click to make this item appear", "on bingo cards"));
            }
        }
        return result;
    }

    @Override
    public void onOptionClickedDelegate(InventoryClickEvent event, ItemStack itemClicked, Player player)
    {
        ItemDifficultySelectionUI difficultySelector = new ItemDifficultySelectionUI(itemClicked.getType(), this);
        difficultySelector.open(player);
    }
}
