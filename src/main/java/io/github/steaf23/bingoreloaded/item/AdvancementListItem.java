package io.github.steaf23.bingoreloaded.item;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;

public class AdvancementListItem extends InventoryItem
{
    public Advancement advancement;

    public AdvancementListItem(Advancement advancement)
    {
        super(Material.FILLED_MAP, "");
        this.advancement = advancement;
    }
}
