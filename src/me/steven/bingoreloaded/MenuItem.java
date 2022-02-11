package me.steven.bingoreloaded;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MenuItem extends ItemStack
{
    public MenuItem(Material material, String name, String... description)
    {
        super(material);

        ItemMeta meta = getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(name);
            meta.setLore(List.of(description));
        }
        setItemMeta(meta);
    }

    public MenuItem(ItemStack item)
    {
        super(item);
    }
}
