package io.github.steaf23.bingoreloaded.lib.inventory.item;

import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.StackHandle;
import io.github.steaf23.bingoreloaded.lib.inventory.item.action.MenuAction;
import io.github.steaf23.bingoreloaded.lib.util.PDCHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A Builder for creating item stacks easily.
 * Has a few extra features such as
 *  - multi-level descriptions that can be added with priorities to influence the order.
 *  - meta modifier function that can be used to quickly change item meta on the item.
 *  - textured variant, represented as a different item that will be displayed instead when a resource pack is enabled.
 */
public class ItemTemplate
{
    public static final ItemTemplate EMPTY = new ItemTemplate(ItemType.AIR);
    public static final Set<ItemType> LEATHER_ARMOR = Set.of(ItemType.of("leather_chestplate"), ItemType.of("leather_boots"), ItemType.of("leather_leggings"), ItemType.of("leather_helmet"));

    // higher priorities appear lower on the item description
    record DescriptionSection(int priority, Component[] text)
    {
    }

    private int slot = 0;
    private ItemType type;
    private Component name;
    private final Map<String, DescriptionSection> description = new HashMap<>();
    private int amount = 1;
    private boolean glowing = false;
    private String compareKey = null;
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
//    private final List<Function<ItemMeta, ItemMeta>> metaModifiers = new ArrayList<>(); REPLACE WITH COMPONENTS
    private Integer maxDamage = null;
    private Integer maxStackSize = null;
    private int currentDamage = 0;
    private int customModelData = 0;
    private ItemTemplate texturedVariant = null;

    private MenuAction action;

    public ItemTemplate(ItemType type) {
        this.type = type;
    }

    public ItemTemplate(ItemType type, @Nullable Component name, Component... lore) {
        this.type = type;
        this.name = name;
        setLore(lore);
    }

    public ItemTemplate(int slot, ItemType type) {
        this.slot = slot;
        this.type = type;
    }

    public ItemTemplate(int slotX, int slotY, ItemType type) {
        this(ItemTemplate.slotFromXY(slotX, slotY), type);
    }

    public ItemTemplate(int slotX, int slotY, ItemType type, Component name, Component... lore) {
        this(ItemTemplate.slotFromXY(slotX, slotY), type, name, lore);
    }

    public ItemTemplate(int slot, ItemType type, Component name, Component... lore) {
        this.slot = slot;
        this.type = type;
        this.name = name;
        setLore(lore);
    }

    public @NotNull String getPlainTextName() {
        return name == null ? "" : PlainTextComponentSerializer.plainText().serialize(name);
    }

    public Component getName() {
        return name;
    }

    public ItemTemplate setName(@Nullable Component name) {
        this.name = name;
        return this;
    }

    /**
     * Inserts standard lore into the template.
     * Lore gets added with priority 0 into the description,
     *      meaning negative description priorities show above this lore and positive priorities show below this lore.
     * @param lore multiline lore.
     */
    public ItemTemplate setLore(Component... lore) {
        if (lore.length == 0)
            return this;

        return addDescription("lore", 0, lore);
    }

    /**
     * Adds a description section to the template's item description.
     * Each section is separated by ann empty line and the first description is shown directly under the item name.
     * @param name identifiable name for the description, can be used to remove the description using removeDescription().
     * @param priority appends this description to the template where higher priorities are displayed further down.
     * @param description multiline description.
     */
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

    public ItemTemplate setItemType(ItemType type) {
        this.type = type;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * Sets new amount of the resulting item stack from this template.
     * @param amount value is clamped between 1 and 64 before being applied.
     */
    public ItemTemplate setAmount(int amount) {
        this.amount = Math.clamp(amount, 1, 64);
        return this;
    }

    public boolean isGlowing() {
        return glowing;
    }

    /**
     * @param enable if true, adds enchantment glint to the item.
     */
    public ItemTemplate setGlowing(boolean enable) {
        this.glowing = enable;
        return this;
    }

    /**
     * Additional key that can be used for item comparison, saved in PersistentDataContainer of a built StackHandle.
     * @param key identifiable key used for comparisons.
     */
    public ItemTemplate setCompareKey(String key) {
        this.compareKey = key;
        return this;
    }

    /**
     * @return Compare key as set by setCompareKey. Returns an empty string if no compare key was set.
     */
    public String getCompareKey() {
        return compareKey == null ? "" : compareKey;
    }

    public boolean isCompareKeyEqual(StackHandle other) {
        return ItemTemplate.isCompareKeyEqual(other, compareKey);
    }

    /**
     * Compares the compare key of an StackHandle built from an ItemTemplate against the given key string.
     * @return true if the compareKey is present in the given stack.
     */
    public static boolean isCompareKeyEqual(StackHandle stack, String compareKey) {
        if (compareKey == null) {
            return false;
        }
        if (stack == null || !stack.hasItemMeta())
            return false;

        return PDCHelper.getStringFromPdc(stack.getItemMeta().getPersistentDataContainer(), "compare_key").equals(compareKey);
    }

    /**
     * Sets leather color to given color value. Only works when the resulting stack is a piece of leather armor.
     * @param color new color to use.
     */
    public ItemTemplate setLeatherColor(@NotNull TextColor color) {
        return addMetaModifier(meta -> {
            if (meta instanceof LeatherArmorMeta armorMeta) {
                armorMeta.setColor(Color.fromRGB(color.red(), color.green(), color.blue()));
                return armorMeta;
            }
            return meta;
        });
    }

    /**
     * Sets the menu action to perform when interacting with this item in a menu.
     */
    public ItemTemplate setAction(@Nullable MenuAction action) {
        this.action = action;
        if (action == null) {
            return this;
        }
        action.setItem(this);
        return this;
    }

    public MenuAction getAction() {
        return this.action;
    }

    /**
     * All added enchantments get added as unsafe enchantments to the built stack.
     */
    public ItemTemplate addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Damages the item by byAmount. The resulting damage cannot go below 0.
     */
    public ItemTemplate setDamage(int byAmount) {
        currentDamage = Math.max(currentDamage - byAmount, 0);
        return this;
    }

    /**
     * Sets the max damage for this item. Also resets the current amount of damage to the same value.
     * @param damage If null removes max damage from resulting item stack.
     * @return
     */
    public ItemTemplate setMaxDamage(@Nullable Integer damage) {
        maxDamage = damage;
        currentDamage = maxDamage == null ? 0 : maxDamage;
        return this;
    }

    public ItemTemplate setMaxStackSize(int stackSize) {
        maxStackSize = Math.clamp(stackSize, 1, 64);
        return this;
    }

    public ItemTemplate resetMaxStackSize() {
        maxStackSize = null;
        return this;
    }

    public ItemTemplate setCustomModelData(int data) {
        this.customModelData = data;
        return this;
    }

    /**
     * Sets the ItemTemplate to build instead of this template when PlayerDisplay.useCustomTextures() returns true.
     */
    public ItemTemplate setTexturedVariant(ItemTemplate item) {
        this.texturedVariant = item;
        return this;
    }

    public void useItem(MenuAction.ActionArguments arguments) {
        if (action == null) {
            return;
        }
        action.use(arguments);
    }

    public boolean isEmpty() {
        return type.isAir();
    }

    public ItemType getItemType() {
        return type;
    }

    /**
     * Performs deep copy of the item template.
     * Assigns a copy of the textured variant to the copied template.
     * @return The created copy.
     */
    public ItemTemplate copy() {
        ItemTemplate copy = new ItemTemplate(slot, type, name);
        copy.description.putAll(description);
        copy.amount = amount;
        copy.glowing = glowing;
        copy.compareKey = compareKey;
        copy.action = action;
        copy.enchantments.putAll(enchantments);
        copy.metaModifiers.addAll(metaModifiers);
        copy.maxDamage = maxDamage;
        copy.currentDamage = currentDamage;
        copy.customModelData = customModelData;
        copy.texturedVariant = texturedVariant == null ? null : texturedVariant.copy();
        return copy;
    }

    /**
     * Copies template using copy() and sets the item slot
     * @param slotX slot column to copy this template to
     * @param slotY slot row to copy this template to
     * @return The created copy.
     */
    public ItemTemplate copyToSlot(int slotX, int slotY) {
        return copyToSlot(ItemTemplate.slotFromXY(slotX, slotY));
    }

    /**
     * Copies template using copy() and sets the item slot
     * @param slot slot to copy this template to
     * @return The created copy.
     */
    public ItemTemplate copyToSlot(int slot) {
        ItemTemplate copy = copy();
        copy.slot = slot;
        return copy;
    }

    public ItemTemplate addMetaModifier(Function<ItemMeta, ItemMeta> metaModifier) {
        this.metaModifiers.add(metaModifier);
        return this;
    }

    /**
     * @return Item built from this template.
     */
    public StackHandle buildItem() {
        return buildItem(PlayerDisplay.useCustomTextures());
    }

    public StackHandle buildItem(boolean hideAttributes) {
        return buildItem(hideAttributes, PlayerDisplay.useCustomTextures());
    }

    private StackHandle buildItem(boolean hideAttributes, boolean textured) {
        if (textured && texturedVariant != null) {
            return buildItem(hideAttributes, false);
        }

        List<Component> descriptionList = buildDescriptionList();
        StackHandle stack = new StackHandle(type, amount);

        ItemMeta stackMeta = stack.getItemMeta();
        if (stackMeta == null) {
            return stack;
        }

        if (glowing) {
            stackMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        }

        if (name != null) {
            stackMeta.displayName(name.colorIfAbsent(NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        if (!descriptionList.isEmpty()) {
            stackMeta.lore(descriptionList);
        }

        if (maxDamage != null) {
            if (stackMeta instanceof Damageable) {
                ((Damageable)stackMeta).setMaxDamage(maxDamage);
                ((Damageable)stackMeta).setDamage(currentDamage);
            }
        }

        if (maxStackSize != null) {
            stackMeta.setMaxStackSize(maxStackSize);
        }

        if (compareKey != null) {
            PersistentDataContainer pdc = stackMeta.getPersistentDataContainer();
            PDCHelper.addStringToPdc(pdc, "compare_key", compareKey);
        }

        stackMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DYE, ItemFlag.HIDE_STORED_ENCHANTS, ItemFlag.HIDE_ARMOR_TRIM);
        if (hideAttributes) {
            //TODO: change if there is a need for items to be used by the player with hidden attributes
            stackMeta.setAttributeModifiers(ImmutableMultimap.of());
            stackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        stackMeta.setCustomModelData(customModelData);

        for (Function<ItemMeta, ItemMeta> modifier : metaModifiers) {
            stackMeta = modifier.apply(stackMeta);
        }
        for (Enchantment enchantment : enchantments.keySet()) {
            stackMeta.addEnchant(enchantment, enchantments.get(enchantment), true);
        }
        stack.setItemMeta(stackMeta);

        return stack;
    }

    public com.github.retrooper.packetevents.protocol.item.StackHandle buildPacketEventsItem() {
        return buildPacketEventsItem(PlayerDisplay.useCustomTextures());
    }

    private com.github.retrooper.packetevents.protocol.item.StackHandle buildPacketEventsItem(boolean textured) {
        var builder = com.github.retrooper.packetevents.protocol.item.StackHandle.builder();
        return builder
                .type(ItemTypes.getByName(type.getKey().toString()))
                .amount(amount)
                .component(ComponentTypes.ITEM_NAME, name)
                .component(ComponentTypes.LORE, new ItemLore(buildDescriptionList()))
                .build();
    }

    private List<Component> buildDescriptionList() {
        // To create the description, sort the sections based on priority and place all lines under each other.
        List<Component> descriptionList = new ArrayList<>();
        description.values().stream().sorted(Comparator.comparingInt(a -> a.priority)).forEach(section -> {
            descriptionList.addAll(Arrays.stream(section.text).map(c -> c.colorIfAbsent(NamedTextColor.WHITE).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList());
            descriptionList.add(Component.text(" "));
        });

        if (!descriptionList.isEmpty()) {
            descriptionList.removeLast();
        }

        return descriptionList;
    }

    /**
     * @param slotX input column
     * @param slotY input row
     * @return inventory slot index from input column and row
     */
    public static int slotFromXY(int slotX, int slotY) {
        return 9 * slotY + slotX;
    }

    public static ItemTemplate createColoredLeather(TextColor color, ItemType leatherItemType) {
        if (!LEATHER_ARMOR.contains(leatherItemType)) {
            leatherItemType = ItemType.of("leather_chestplate");
        }

        ItemTemplate item = new ItemTemplate(leatherItemType, net.kyori.adventure.text.Component.text(color.asHexString()).color(color));
        item.setLeatherColor(color);
        return item;
    }

    public static StackHandle colorStackHandle(StackHandle input, TextColor color) {
        input = input.clone();
        input.editMeta(meta -> {
            if (meta instanceof LeatherArmorMeta armorMeta) {
                armorMeta.setColor(Color.fromRGB(color.red(), color.green(), color.blue()));
            }
        });
        return input;
    }
}
