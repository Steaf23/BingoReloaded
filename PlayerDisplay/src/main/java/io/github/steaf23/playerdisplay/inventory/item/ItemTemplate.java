package io.github.steaf23.playerdisplay.inventory.item;

import com.google.common.collect.ImmutableMultimap;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.inventory.item.action.MenuAction;
import io.github.steaf23.playerdisplay.util.ExtraMath;
import io.github.steaf23.playerdisplay.util.PDCHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class ItemTemplate
{
    public static final ItemTemplate EMPTY = new ItemTemplate(Material.AIR);
    public static final Set<Material> LEATHER_ARMOR = Set.of(Material.LEATHER_CHESTPLATE, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_HELMET);

    // higher priorities appear lower on the item description
    record DescriptionSection(int priority, Component[] text)
    {
    }

    private int slot = 0;
    private Material material;
    private Component name;
    private final Map<String, DescriptionSection> description = new HashMap<>();
    private int amount = 1;
    private boolean glowing = false;
    private String compareKey = null;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private final List<Function<ItemMeta, ItemMeta>> metaModifiers = new ArrayList<>();
    private Integer maxDamage = null;
    private int currentDamage = 0;

    private MenuAction action;

    public ItemTemplate(Material material) {
        this.material = material;
    }

    public ItemTemplate(Material material, @Nullable Component name, Component... lore) {
        this.material = material;
        this.name = name;
        setLore(lore);
    }

    public ItemTemplate(int slot, Material material) {
        this.slot = slot;
        this.material = material;
    }

    public ItemTemplate(int slotX, int slotY, Material material) {
        this(ItemTemplate.slotFromXY(slotX, slotY), material);
    }

    public ItemTemplate(int slotX, int slotY, Material material, Component name, Component... lore) {
        this(ItemTemplate.slotFromXY(slotX, slotY), material, name, lore);
    }

    public ItemTemplate(int slot, Material material, Component name, Component... lore) {
        this.slot = slot;
        this.material = material;
        this.name = name;
        setLore(lore);
    }

    public @NotNull String getPlainTextName() {
        return name == null ? "" : ((TextComponent) name).content();
    }

    public Component getName() {
        return name;
    }

    public ItemTemplate setName(@Nullable Component name) {
        this.name = name;
        return this;
    }

    public ItemTemplate setLore(Component... lore) {
        if (lore.length == 0)
            return this;

        return addDescription("lore", 0, lore);
    }

    public ItemTemplate addDescription(String name, int priority, Component... description) {
        if (description.length < 1) {
            return this;
        }

        this.description.put(name, new DescriptionSection(priority, description));
        return this;
    }

    public int getSlot() {
        return slot;
    }

    public ItemTemplate setSlot(int slotX, int slotY) {
        return setSlot(ItemTemplate.slotFromXY(slotX, slotY));
    }

    public ItemTemplate setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public ItemTemplate setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public ItemTemplate setAmount(int amount) {
        this.amount = ExtraMath.clamped(amount, 1, 64);
        return this;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public ItemTemplate setGlowing(boolean enable) {
        this.glowing = enable;
        return this;
    }

    /**
     * Additional key that can be used for item comparison, saved in pdc
     *
     * @param key
     */
    public ItemTemplate setCompareKey(String key) {
        this.compareKey = key;
        return this;
    }

    public String getCompareKey() {
        return compareKey == null ? "" : compareKey;
    }

    public boolean isCompareKeyEqual(ItemStack other) {
        return ItemTemplate.isCompareKeyEqual(other, compareKey);
    }

    public static boolean isCompareKeyEqual(ItemStack other, String compareKey) {
        if (compareKey == null) {
            return false;
        }
        if (other == null || !other.hasItemMeta())
            return false;

        return compareKey.equals(other.getItemMeta().getPersistentDataContainer()
                .get(PDCHelper.createKey("item.compare_key"), PersistentDataType.STRING));
    }

    public ItemTemplate setLeatherColor(@NotNull TextColor color) {
        return addMetaModifier(meta -> {
            if (meta instanceof LeatherArmorMeta armorMeta) {
                armorMeta.setColor(Color.fromRGB(color.red(), color.green(), color.blue()));
                return armorMeta;
            }
            return meta;
        });
    }

    public ItemTemplate setAction(@Nullable MenuAction action) {
        this.action = action;
        if (action == null) {
            return this;
        }
        action.setItem(this);
        return this;
    }

    public ItemTemplate addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public void setDamage(int byAmount) {
        currentDamage = Math.max(currentDamage - byAmount, 0);
    }

    public void setMaxDamage(@Nullable Integer damage) {
        maxDamage = damage;
        currentDamage = maxDamage == null ? 0 : maxDamage;
    }

    public void useItem(BasicMenu.ActionArguments arguments) {
        if (action == null) {
            return;
        }
        action.use(arguments);
    }

    public boolean isEmpty() {
        return material.isAir();
    }

    public Material getMaterial() {
        return material;
    }

    public ItemTemplate copy() {
        ItemTemplate copy = new ItemTemplate(slot, material, name);
        copy.description.putAll(description);
        copy.amount = amount;
        copy.glowing = glowing;
        copy.compareKey = compareKey;
        copy.action = action;
        copy.enchantments.putAll(enchantments);
        copy.metaModifiers.addAll(metaModifiers);
        copy.maxDamage = maxDamage;
        copy.currentDamage = currentDamage;
        return copy;
    }

    public ItemTemplate copyToSlot(int slotX, int slotY) {
        return copyToSlot(ItemTemplate.slotFromXY(slotX, slotY));
    }

    public ItemTemplate copyToSlot(int slot) {
        ItemTemplate copy = copy();
        copy.slot = slot;
        return copy;
    }

    public ItemTemplate addMetaModifier(Function<ItemMeta, ItemMeta> metaModifier) {
        this.metaModifiers.add(metaModifier);
        return this;
    }

    public ItemStack buildItem() {
        return buildItem(true);
    }

    public ItemStack buildItem(boolean hideAttributes) {
        // To create the description, sort the sections based on priority and place all lines under each other.
        List<Component> descriptionList = new ArrayList<>();
        description.values().stream().sorted(Comparator.comparingInt(a -> a.priority)).forEach(section -> {
            descriptionList.addAll(Arrays.stream(section.text).map(c -> c.colorIfAbsent(NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList());
            descriptionList.add(Component.text(" "));
        });

        if (!descriptionList.isEmpty()) {
            descriptionList.removeLast();
        }
        ItemStack stack = new ItemStack(material, amount);

        if (glowing) {
            stack.addUnsafeEnchantment(Enchantment.UNBREAKING, 1);
        }

        ItemMeta stackMeta = stack.getItemMeta();
        if (stackMeta == null) {
            return stack;
        }

        if (name != null) {
            stackMeta.displayName(name.colorIfAbsent(NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        if (!descriptionList.isEmpty()) {
            stackMeta.lore(descriptionList);
        }

        if (maxDamage != null) {
            // NOTE: still broken in older versions (pre 1.20.6-1.21-ish) for spawn eggs.
            if (stackMeta instanceof Damageable) {
                ((Damageable)stackMeta).setMaxDamage(maxDamage);
                ((Damageable)stackMeta).setDamage(currentDamage);
            }
        }
        PersistentDataContainer pdc = stackMeta.getPersistentDataContainer();
        pdc = PDCHelper.addStringToPdc(pdc, "compare_key", compareKey);

        stackMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE);
        if (hideAttributes) {
            //FIXME: maybe change in the future if we need to have items to be used by the player with invisible attributes
            stackMeta.setAttributeModifiers(ImmutableMultimap.of());
            stackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        for (Function<ItemMeta, ItemMeta> modifier : metaModifiers) {
            stackMeta = modifier.apply(stackMeta);
        }
        stack.setItemMeta(stackMeta);
        stack.addUnsafeEnchantments(enchantments);
        return stack;
    }

    public static int slotFromXY(int slotX, int slotY) {
        return 9 * slotY + slotX;
    }

    public static ItemTemplate createColoredLeather(TextColor color, Material leatherMaterial) {
        if (!LEATHER_ARMOR.contains(leatherMaterial)) {
            leatherMaterial = Material.LEATHER_CHESTPLATE;
        }

        //TODO: test this
        ItemTemplate item = new ItemTemplate(leatherMaterial, Component.text(color.asHexString()).color(color));
        item.setLeatherColor(color);
        return item;
    }
}
