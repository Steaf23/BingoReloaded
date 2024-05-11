package io.github.steaf23.easymenulib.menu.item;

import io.github.steaf23.easymenulib.menu.BasicMenu;
import io.github.steaf23.easymenulib.menu.item.action.MenuAction;
import io.github.steaf23.easymenulib.util.FlexColor;
import io.github.steaf23.easymenulib.util.PDCHelper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MenuItem
{
    // higher priorities appear lower on the item description
    record DescriptionSection(int priority, String... text)
    {
    }

    /**
     * Describes the slot the item should be in when put in any inventory.
     */
    private int slot;
    private ItemStack stack;
    private MenuAction action;
    // all description sections, stored by name.
    private final Map<String, DescriptionSection> descriptionSections;

    public MenuItem(Material material, String name, String... description) {
        this(0, material, name, description);
    }

    public MenuItem(int slotX, int slotY, Material material, String name, String... description) {
        this(slotFromXY(slotX, slotY), material, name, description);
    }

    public MenuItem(int slot, Material material, String name, String... description) {
        this.stack = new ItemStack(material);
        this.slot = slot;
        this.descriptionSections = new HashMap<>();

        if (description.length >= 1 && !description[0].isEmpty()) {
            // constructor description is default lore, create with priority 0 as baseline for other sections
            descriptionSections.put("lore", new DescriptionSection(0, description));
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
            stack.setItemMeta(meta);
        }
    }

    /**
     * When constructed from a pre-existing item stack, lore is copied from the stack and used as the lore section of the menu item.
     * @param slot
     * @param item
     */
    public MenuItem(int slot, @NotNull ItemStack item) {
        this.stack = item;
        this.slot = slot;
        this.descriptionSections = new HashMap<>();
    }

    public MenuItem(@NotNull ItemStack item) {
        this(-1, item);
    }

    public MenuItem setAction(MenuAction action) {
        this.action = action;
        action.setItem(this);
        return this;
    }

    public void useItem(BasicMenu.ActionArguments arguments) {
        if (action == null) {
            return;
        }
        action.use(arguments);
    }

    public MenuItem withEnchantment(Enchantment enchantment, int level) {
        stack.addEnchantment(enchantment, level);
        return this;
    }

    public MenuItem withIllegalEnchantment(Enchantment enchantment, int level) {
        stack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public MenuItem setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemStack buildStack() {
        // To create the description, sort the sections based on priority and place all lines under each other.
        List<String> description = new ArrayList<>();
        descriptionSections.values().stream().sorted(Comparator.comparingInt(a -> a.priority)).forEach(section -> {
            description.addAll(Arrays.asList(section.text));
            description.add(" ");
        });

        if (!description.isEmpty())
        {
            description.remove(description.size() - 1);
        }

        ItemStack copy = stack.clone();
        ItemMeta meta = copy.getItemMeta();
        if (meta != null && !description.isEmpty())
        {
            meta.setLore(description);
            copy.setItemMeta(meta);
        }
        return copy;
    }

    public SerializableItem createPlayerItem() {
        return new SerializableItem(slot, buildStack());
    }

    public MenuItem copy() {
        return new MenuItem(slot, stack.clone());
    }

    public MenuItem setSlot(int newSlot) {
        this.slot = newSlot;
        return this;
    }

    public MenuItem copyToSlot(int slot) {
        MenuItem item = new MenuItem(slot, stack.clone());
        item.action = action;
        for (String key : descriptionSections.keySet()) {
            item.descriptionSections.put(key, descriptionSections.get(key));
        }
        return item;
    }

    public MenuItem copyToSlot(int slotX, int slotY) {
        return copyToSlot(slotFromXY(slotX, slotY));
    }

    public int getSlot() {
        return slot;
    }

    public MenuItem addDescription(String name, int priority, String... description) {
        if (description.length < 1 || description[0].isEmpty()) {
            return this;
        }

        descriptionSections.put(name, new DescriptionSection(priority, description));
        return this;
    }

    public MenuItem setLore(String... lore) {
        return addDescription("lore", 0, lore);
    }

    public void removeDescription(String name) {
        descriptionSections.remove(name);
    }

    public MenuItem setName(String name) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            stack.setItemMeta(meta);
        }
        return this;
    }

    public MenuItem setGlowing(boolean value) {
        if (value) {
            stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        } else {
            stack.removeEnchantment(Enchantment.DURABILITY);
        }
        return this;
    }

    public boolean isGlowing() {
        return stack.getItemMeta().hasEnchant(Enchantment.DURABILITY);
    }

    public MenuItem addStringToPdc(String key, @Nullable String value) {
        if (key == null) {
            return this;
        }

        var meta = stack.getItemMeta();
        var pdc = meta.getPersistentDataContainer();
        if (value == null)
            pdc.remove(PDCHelper.createKey("item." + key));
        else
            pdc.set(PDCHelper.createKey("item." + key), PersistentDataType.STRING, value);
        stack.setItemMeta(meta);
        return this;
    }

    public boolean isStringPdcEqual(String key, ItemStack other) {
        if (other == null || !other.hasItemMeta())
            return false;

        String stringPdc = getStringFromPdc(key);
        if (stringPdc == null)
            return false;

        return getCompareKey().equals(other.getItemMeta().getPersistentDataContainer()
                .getOrDefault(PDCHelper.createKey("item." + key), PersistentDataType.STRING, ""));
    }

    public String getStringFromPdc(String key) {
        return stack.getItemMeta().getPersistentDataContainer()
                .getOrDefault(PDCHelper.createKey("item." + key), PersistentDataType.STRING, "");
    }

    /**
     * Additional key that can be used for item comparison, saved in pdc
     *
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

    public int getAmount() {
        return stack.getAmount();
    }

    public Material getMaterial() {
        return stack.getType();
    }

    public String getName() {
        return stack.getItemMeta() == null ? stack.getItemMeta().getDisplayName() : "";
    }

    /**
     * Replaces the existing stack with a completely new stack, losing all data attached to the stack (including PDC!)
     */
    public MenuItem replaceStack(ItemStack newStack) {
        stack = newStack;
        return this;
    }

    public MenuItem replaceStack(MenuItem newStack) {
        stack = newStack.stack;
        return this;
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
        ItemStack stack = item.stack;
        if (stack.getItemMeta() instanceof LeatherArmorMeta armorMeta) {
            armorMeta.setColor(org.bukkit.Color.fromRGB(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue()));
            stack.setItemMeta(armorMeta);
        }
        return item;
    }

    public MenuItem setLeatherColor(ChatColor color) {
        if (stack.getItemMeta() instanceof LeatherArmorMeta armorMeta) {
            armorMeta.setColor(org.bukkit.Color.fromRGB(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue()));
            stack.setItemMeta(armorMeta);
        }
        return this;
    }
}
