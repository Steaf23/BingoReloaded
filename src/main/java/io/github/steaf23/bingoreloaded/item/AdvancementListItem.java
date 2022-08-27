package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.data.AdvancementData;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.inventory.meta.ItemMeta;

public class AdvancementListItem extends InventoryItem
{
    public Advancement advancement;

    public AdvancementListItem(Advancement advancement)
    {
        super(Material.FILLED_MAP, "");
        this.advancement = advancement;

        ItemMeta meta = getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(AdvancementData.getAdvancementTitle(advancement));
            setItemMeta(meta);
        }
    }
}
