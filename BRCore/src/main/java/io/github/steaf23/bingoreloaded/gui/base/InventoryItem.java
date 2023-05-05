package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.util.PDCHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("Bingo.InventoryItem")
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

    public InventoryItem(int slotX, int slotY, Material material, String name, String... description)
    {
        this(9 * slotY + slotX, material, name, description);
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
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
            setItemMeta(meta);
        }
    }

    public InventoryItem(int slot, ItemStack item)
    {
        this(item);
        this.slot = slot;
    }

    public InventoryItem(@NonNull ItemStack item)
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
        return new InventoryItem(slot,
                getType(),
                getItemMeta().getDisplayName(),
                getItemMeta().getLore() == null ? new String[]{""} : getItemMeta().getLore().toArray( new String[0]))
                .setGlowing(isGlowing())
                .setKey(getKey() == null ? "" : getKey());
    }

    public InventoryItem copyToSlot(int slot)
    {
        InventoryItem item = new InventoryItem(slot, this);
        item.slot = slot;
        return item;
    }

    public InventoryItem copyToSlot(int slotX, int slotY)
    {
        return copyToSlot(9 * slotY + slotX);
    }

    public int getSlot()
    {
        return slot;
    }

    public InventoryItem setDescription(String... description)
    {
        ItemMeta meta = getItemMeta();
        if (meta != null)
        {
            if (description.length >= 1 && description[0] != "")
                meta.setLore(List.of(description));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            setItemMeta(meta);
        }
        return this;
    }

    public InventoryItem setName(String name)
    {
        ItemMeta meta = getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(name);
        }
        return this;
    }

    public InventoryItem setGlowing(boolean value)
    {
        if (value)
        {
            addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }
        else
        {
            removeEnchantment(Enchantment.DURABILITY);
        }
        return this;
    }

    public boolean isGlowing()
    {
        return getItemMeta().hasEnchant(Enchantment.DURABILITY);
    }

    /**
     * Additional key that can be used for item comparison, saved in pdc
     * @param key
     */
    public InventoryItem setKey(@Nullable String key)
    {
        var meta = this.getItemMeta();
        var pdc = meta.getPersistentDataContainer();
        if (key.equals(""))
            pdc.remove(PDCHelper.createKey("item.compare_key"));
        else
            pdc.set(PDCHelper.createKey("item.compare_key"), PersistentDataType.STRING, key);
        this.setItemMeta(meta);
        return this;
    }

    public boolean isKeyEqual(ItemStack other)
    {
        if (other == null || other.getItemMeta() == null)
            return false;

        String key = getKey();
        if (key == null)
            return false;

        return getKey().equals(other.getItemMeta().getPersistentDataContainer()
                .get(PDCHelper.createKey("item.compare_key"), PersistentDataType.STRING));
    }

    @Nullable
    public String getKey()
    {
        return this.getItemMeta().getPersistentDataContainer()
                .get(PDCHelper.createKey("item.compare_key"), PersistentDataType.STRING);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize()
    {
        return new HashMap<>(){{
            put("slot", slot);
            put("stack", getAsStack());
        }};
    }

    public static InventoryItem deserialize(Map<String, Object> data)
    {
        ItemStack stack = (ItemStack)data.get("stack");
        int slot = (int)data.get("slot");
        return new InventoryItem(slot, stack);
    }
}
