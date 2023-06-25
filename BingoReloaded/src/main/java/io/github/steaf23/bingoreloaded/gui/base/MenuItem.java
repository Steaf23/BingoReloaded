package io.github.steaf23.bingoreloaded.gui.base;

import io.github.steaf23.bingoreloaded.util.FlexColor;
import io.github.steaf23.bingoreloaded.util.PDCHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
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

    public MenuItem(Material material, String name, String... description) {
        this(-1, material, name, description);
    }

    public MenuItem(int slotX, int slotY, Material material, String name, String... description) {
        this(slotFromXY(slotX, slotY), material, name, description);
    }

    public MenuItem(int slot, Material material, String name, String... description) {
        super(material);
        this.slot = slot;

        ItemMeta meta = getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (description.length >= 1 && !description[0].isEmpty())
                meta.setLore(List.of(description));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
            setItemMeta(meta);
        }
    }

    public MenuItem(int slot, ItemStack item) {
        this(item);
        this.slot = slot;
    }

    public MenuItem(@NonNull ItemStack item) {
        super(item);
    }


    public MenuItem withEnchantment(Enchantment enchantment, int level) {
        addEnchantment(enchantment, level);
        return this;
    }

    public MenuItem withIllegalEnchantment(Enchantment enchantment, int level) {
        addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public MenuItem withAmount(int amount) {
        setAmount(amount);
        return this;
    }

    public ItemStack getAsStack() {
        return new ItemStack(this);
    }

    public MenuItem copy() {
        return new MenuItem(slot,
                getType(),
                getItemMeta().getDisplayName(),
                getItemMeta().getLore() == null ? new String[]{""} : getItemMeta().getLore().toArray(new String[0]))
                .setGlowing(isGlowing())
                .setCompareKey(getCompareKey());
    }

    public MenuItem setSlot(int newSlot) {
        this.slot = newSlot;
        return this;
    }

    public MenuItem copyToSlot(int slot) {
        MenuItem item = new MenuItem(slot, this);
        item.slot = slot;
        return item;
    }

    public MenuItem copyToSlot(int slotX, int slotY) {
        return copyToSlot(slotFromXY(slotX, slotY));
    }

    public int getSlot() {
        return slot;
    }

    public MenuItem setDescription(String... description) {
        ItemMeta meta = getItemMeta();
        if (meta != null) {
            if (description.length >= 1 && !description[0].isEmpty())
                meta.setLore(List.of(description));
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            setItemMeta(meta);
        }
        return this;
    }

    public MenuItem setName(String name) {
        ItemMeta meta = getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            setItemMeta(meta);
        }
        return this;
    }

    public MenuItem setGlowing(boolean value) {
        if (value) {
            addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        } else {
            removeEnchantment(Enchantment.DURABILITY);
        }
        return this;
    }

    public boolean isGlowing() {
        return getItemMeta().hasEnchant(Enchantment.DURABILITY);
    }

    public MenuItem addStringToPdc(String key, @Nullable String value) {
        if (key == null) {
            return this;
        }

        var meta = this.getItemMeta();
        var pdc = meta.getPersistentDataContainer();
        if (value == null)
            pdc.remove(PDCHelper.createKey("item." + key));
        else
            pdc.set(PDCHelper.createKey("item." + key), PersistentDataType.STRING, value);
        this.setItemMeta(meta);
        return this;
    }

    public boolean isStringPdcEqual(String key, ItemStack other)
    {
        if (other == null || other.getItemMeta() == null)
            return false;

        String stringPdc = getStringFromPdc(key);
        if (stringPdc == null)
            return false;

        return getCompareKey().equals(other.getItemMeta().getPersistentDataContainer()
                .getOrDefault(PDCHelper.createKey("item." + key), PersistentDataType.STRING, ""));
    }

    public String getStringFromPdc(String key) {
        return this.getItemMeta().getPersistentDataContainer()
                .getOrDefault(PDCHelper.createKey("item." + key), PersistentDataType.STRING, "");
    }

    /**
     * Additional key that can be used for item comparison, saved in pdc
     * @param key
     */
    public MenuItem setCompareKey(@Nullable String key) {
        return addStringToPdc("compare_key", key);
    }

    public boolean isCompareKeyEqual(ItemStack other) {
        return isStringPdcEqual("compare_key", other);
    }

    public String getCompareKey() {
        return getStringFromPdc("compare_key");
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>()
        {{
            put("slot", slot);
            put("stack", getAsStack());
        }};
    }

    public static MenuItem deserialize(Map<String, Object> data) {
        ItemStack stack = (ItemStack) data.get("stack");
        int slot = (int) data.get("slot");
        return new MenuItem(slot, stack);
    }

    public static int slotFromXY(int slotX, int slotY) {
        return 9 * slotY + slotX;
    }

    /**
     * @param part should be one of Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS or Material.LEATHER_BOOTS
     * @return the leather item colored with the given color. If no valid part is given, this will return a colored Material.LEATHER_CHESTPLATE
     */
    public static MenuItem createColoredLeather(ChatColor color, Material part) {
        switch (part) {
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS -> {
            }
            default -> part = Material.LEATHER_CHESTPLATE;
        }

        String hex = FlexColor.asHex(color);
        MenuItem item = new MenuItem(part, ChatColor.of(hex) + hex, "");
        if (item.getItemMeta() instanceof LeatherArmorMeta armorMeta) {
            armorMeta.setColor(org.bukkit.Color.fromRGB(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue()));
            item.setItemMeta(armorMeta);
        }
        return item;
    }
}
