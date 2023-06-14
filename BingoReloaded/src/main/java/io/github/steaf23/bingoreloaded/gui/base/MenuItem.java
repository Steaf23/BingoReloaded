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

@SerializableAs("Bingo.MenuItem")
public class MenuItem extends ItemStack
{
    /**
     * Describes the slot the item should be in when put in any inventory.
     */
    private int slot = -1;
    private ChatColor chatColor;

    public MenuItem(Material material, String name, String... description)
    {
        this(-1, material, name, description);
    }

    public MenuItem(int slotX, int slotY, Material material, String name, String... description)
    {
        this(slotFromXY(slotX, slotY), material, name, description);
    }

    public MenuItem(int slot, Material material, String name, String... description)
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

    public MenuItem(int slot, ItemStack item)
    {
        this(item);
        this.slot = slot;
    }

    public MenuItem(@NonNull ItemStack item)
    {
        super(item);
    }


    public MenuItem withEnchantment(Enchantment enchantment, int level)
    {
        addEnchantment(enchantment, level);
        return this;
    }

    public MenuItem withIllegalEnchantment(Enchantment enchantment, int level)
    {
        addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public MenuItem withAmount(int amount)
    {
        setAmount(amount);
        return this;
    }

    public ItemStack getAsStack()
    {
        return new ItemStack(this);
    }

    public MenuItem copy()
    {
        return new MenuItem(slot,
                getType(),
                getItemMeta().getDisplayName(),
                getItemMeta().getLore() == null ? new String[]{""} : getItemMeta().getLore().toArray( new String[0]))
                .setGlowing(isGlowing())
                .setKey(getKey());
    }

    public MenuItem setSlot(int newSlot)
    {
        this.slot = newSlot;
        return this;
    }

    public MenuItem copyToSlot(int slot)
    {
        MenuItem item = new MenuItem(slot, this);
        item.slot = slot;
        return item;
    }

    public MenuItem copyToSlot(int slotX, int slotY)
    {
        return copyToSlot(slotFromXY(slotX, slotY));
    }

    public int getSlot()
    {
        return slot;
    }

    public MenuItem setDescription(String... description)
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

    public MenuItem setName(String name)
    {
        ItemMeta meta = getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(name);
        }
        return this;
    }

    public MenuItem setGlowing(boolean value)
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
    public MenuItem setKey(@Nullable String key)
    {
        if  (key == null)
        {
            return this;
        }

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
                .getOrDefault(PDCHelper.createKey("item.compare_key"), PersistentDataType.STRING, ""));
    }

    @Nullable
    public String getKey()
    {
        return this.getItemMeta().getPersistentDataContainer()
                .getOrDefault(PDCHelper.createKey("item.compare_key"), PersistentDataType.STRING, "");
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

    public static MenuItem deserialize(Map<String, Object> data)
    {
        ItemStack stack = (ItemStack)data.get("stack");
        int slot = (int)data.get("slot");
        return new MenuItem(slot, stack);
    }

    public static int slotFromXY(int slotX, int slotY)
    {
        return 9 * slotY + slotX;
    }
}
