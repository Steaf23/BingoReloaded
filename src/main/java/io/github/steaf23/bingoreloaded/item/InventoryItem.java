package io.github.steaf23.bingoreloaded.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class InventoryItem extends ItemStack
{
    /**
     * Describes the slot the item should be in when put in any inventory.
     */
    private int slot = -1;
    private ChatColor chatColor;

    public InventoryItem(Material material, String name, String... description)
    {
        this(-1, material, name, description);
    }

    public InventoryItem(int slot, Material material, String name, String... description)
    {
        super(material);
        this.slot = slot;

        ItemMeta meta = getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(name);
            if (description.length >= 1 && description[0] != "")
                meta.setLore(List.of(description));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            setItemMeta(meta);
        }
    }

    public InventoryItem(int slot, ItemStack item)
    {
        this(item);
        this.slot = slot;
    }

    public InventoryItem(ItemStack item)
    {
        super(item);
    }


    public InventoryItem withEnchantment(Enchantment enchantment, int level)
    {
        addEnchantment(enchantment, level);
        return this;
    }

    public InventoryItem withIllegalEnchantment(Enchantment enchantment, int level)
    {
        addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public InventoryItem withAmount(int amount)
    {
        setAmount(amount);
        return this;
    }

    public ItemStack getAsStack()
    {
        return new ItemStack(this);
    }


    public InventoryItem copy()
    {
        return new InventoryItem(slot, getType(), getItemMeta().getDisplayName(), getItemMeta().getLore() == null ? new String[]{""} : getItemMeta().getLore().toArray( new String[0]));
    }

    public InventoryItem inSlot(int slot)
    {
        InventoryItem item = new InventoryItem(slot, this);
        item.slot = slot;
        return item;
    }

    public int getSlot()
    {
        return slot;
    }

    public void setDescription(String... description)
    {
        ItemMeta meta = getItemMeta();
        if (meta != null)
        {
            if (description.length >= 1 && description[0] != "")
                meta.setLore(List.of(description));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            setItemMeta(meta);
        }
    }

    public void setName(String name)
    {
        ItemMeta meta = getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(name);
        }
    }
}
