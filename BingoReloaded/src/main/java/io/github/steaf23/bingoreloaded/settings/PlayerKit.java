package io.github.steaf23.bingoreloaded.settings;

import com.google.common.collect.ImmutableSet;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.CustomKitData;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import io.github.steaf23.playerdisplay.inventory.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum PlayerKit
{
    HARDCORE("hardcore", BingoMessage.KIT_HARDCORE_NAME.asPhrase(), EnumSet.noneOf(EffectOptionFlags.class)),
    NORMAL("normal", BingoMessage.KIT_NORMAL_NAME.asPhrase(), EnumSet.of(EffectOptionFlags.SPEED, EffectOptionFlags.NO_FALL_DAMAGE)),
    OVERPOWERED("overpowered", BingoMessage.KIT_OVERPOWERED_NAME.asPhrase(), EnumSet.allOf(EffectOptionFlags.class)),
    RELOADED("reloaded", BingoMessage.KIT_RELOADED_NAME.asPhrase(), EnumSet.allOf(EffectOptionFlags.class)),
    CUSTOM_1("custom_1", Component.text("CUSTOM 1"), EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_2("custom_2", Component.text("CUSTOM 2"), EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_3("custom_3", Component.text("CUSTOM 3"), EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_4("custom_4", Component.text("CUSTOM 4"), EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_5("custom_5", Component.text("CUSTOM 5"), EnumSet.noneOf(EffectOptionFlags.class)),
    ;

    public static final ItemTemplate WAND_ITEM = new ItemTemplate(
            Material.WARPED_FUNGUS_ON_A_STICK,
            BingoMessage.WAND_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
            BingoMessage.WAND_ITEM_DESC.asMultiline())
            .addEnchantment(Enchantment.UNBREAKING, 3)
            .setCompareKey("wand");

    public static final ItemTemplate CARD_ITEM = new ItemTemplate(
            Material.GLOBE_BANNER_PATTERN,
            BingoMessage.CARD_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
            BingoMessage.CARD_ITEM_DESC.asMultiline())
            .setGlowing(true)
            .setCompareKey("card");

    public static final ItemTemplate VOTE_ITEM = new ItemTemplate(
            Material.EMERALD,
            BingoMessage.VOTE_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
            BingoMessage.VOTE_ITEM_DESC.asMultiline())
            .setCompareKey("vote");

    public static final ItemTemplate TEAM_ITEM = new ItemTemplate(
            Material.WHITE_GLAZED_TERRACOTTA,
            BingoMessage.TEAM_ITEM_NAME.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD, TextDecoration.ITALIC),
            BingoMessage.TEAM_ITEM_DESC.asMultiline())
            .setCompareKey("team");

    public final String configName;
    private final Component displayName;
    public final EnumSet<EffectOptionFlags> defaultEffects;
    private static final CustomKitData customKitData = new CustomKitData();

    PlayerKit(String configName, Component displayName, EnumSet<EffectOptionFlags> defaultEffects)
    {
        this.configName = configName;
        this.displayName = displayName;
        this.defaultEffects = defaultEffects;
    }

    public Component getDisplayName() {
        if (isCustomKit()) {
            return customKitData.getCustomKit(this).name();
        }
        return displayName;
    }

    public List<SerializableItem> getItems(TextColor teamColor)
    {
        ItemTemplate helmet = new ItemTemplate(39, Material.LEATHER_HELMET)
                .setLeatherColor(teamColor);
        ItemTemplate boots = new ItemTemplate(36, Material.LEATHER_BOOTS)
                .setLeatherColor(teamColor);

        List<ItemTemplate> items = new ArrayList<>();
        switch (this)
        {
            case NORMAL -> {
                items.add(helmet
                        .addEnchantment(Enchantment.AQUA_AFFINITY, 1));
                items.add(boots
                        .addEnchantment(Enchantment.DEPTH_STRIDER, 3));
                items.add(new ItemTemplate(1, Material.IRON_PICKAXE));
                items.add(new ItemTemplate(0, Material.IRON_AXE));
                items.add(new ItemTemplate(2, Material.IRON_SHOVEL)
                        .addEnchantment(Enchantment.SILK_TOUCH, 1));
                items.add(new ItemTemplate(3, Material.COOKED_PORKCHOP)
                        .setAmount(32));
            }
            case OVERPOWERED -> {
                items.add(WAND_ITEM.copyToSlot(8));
                items.add(helmet
                        .addEnchantment(Enchantment.UNBREAKING, 3)
                        .addEnchantment(Enchantment.AQUA_AFFINITY, 1)
                        .addEnchantment(Enchantment.PROTECTION, 4));
                items.add(boots
                        .addEnchantment(Enchantment.UNBREAKING, 3)
                        .addEnchantment(Enchantment.DEPTH_STRIDER, 3)
                        .addEnchantment(Enchantment.PROTECTION, 4));
                items.add(new ItemTemplate(1, Material.NETHERITE_PICKAXE)
                        .addEnchantment(Enchantment.FORTUNE, 3)
                        .addEnchantment(Enchantment.EFFICIENCY, 5));
                items.add(new ItemTemplate(0, Material.NETHERITE_AXE)
                        .addEnchantment(Enchantment.LOOTING, 3)
                        .addEnchantment(Enchantment.SHARPNESS, 5)
                        .addEnchantment(Enchantment.EFFICIENCY, 5));
                items.add(new ItemTemplate(2, Material.NETHERITE_SHOVEL)
                        .addEnchantment(Enchantment.SILK_TOUCH, 1)
                        .addEnchantment(Enchantment.EFFICIENCY, 5));
                items.add(new ItemTemplate(3, Material.GOLDEN_CARROT)
                        .setAmount(64));
            }
            case RELOADED -> {
                items.add(WAND_ITEM.copyToSlot(8));
                items.add(helmet
                        .addEnchantment(Enchantment.UNBREAKING, 3)
                        .addEnchantment(Enchantment.AQUA_AFFINITY, 1)
                        .addEnchantment(Enchantment.PROTECTION, 4));
                items.add(boots
                        .addEnchantment(Enchantment.UNBREAKING, 3)
                        .addEnchantment(Enchantment.DEPTH_STRIDER, 3)
                        .addEnchantment(Enchantment.PROTECTION, 4));
                items.add(new ItemTemplate(38, Material.ELYTRA)
                        .addEnchantment(Enchantment.UNBREAKING, 10));
                items.add(new ItemTemplate(1, Material.NETHERITE_PICKAXE)
                        .addEnchantment(Enchantment.FORTUNE, 3)
                        .addEnchantment(Enchantment.EFFICIENCY, 5));
                items.add(new ItemTemplate(0, Material.NETHERITE_AXE)
                        .addEnchantment(Enchantment.LOOTING, 3)
                        .addEnchantment(Enchantment.SHARPNESS, 5)
                        .addEnchantment(Enchantment.EFFICIENCY, 5));
                items.add(new ItemTemplate(2, Material.NETHERITE_SHOVEL)
                        .addEnchantment(Enchantment.SILK_TOUCH, 1)
                        .addEnchantment(Enchantment.EFFICIENCY, 5));
                items.add(new ItemTemplate(3, Material.ENCHANTED_GOLDEN_APPLE)
                        .setAmount(64));
            }
            case CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5 -> {
                CustomKit kit = customKitData.getCustomKit(this);
                if (kit != null)
                {
                    // Color colored items according to the team color.
                    return kit.items().stream()
                            .map(item -> new SerializableItem(item.slot(), ItemTemplate.colorItemStack(item.stack(), teamColor)))
                            .toList();
                }
            }
        }

        return items.stream().map(SerializableItem::fromItemTemplate).collect(Collectors.toList());
    }

    public int getCardSlot() {
        if (isCustomKit()) {
            return customKitData.getCustomKit(this).cardSlot();
        }
        else {
            // off-hand slot: 40
            return 40;
        }
    }

    public boolean isCustomKit() {
        return customKits().contains(this);
    }

    public boolean isValid() {
        return !isCustomKit() || (PlayerKit.customKits().contains(this) && customKitData.getCustomKit(this) != null);
    }

    public static PlayerKit fromConfig(String name)
    {
        if (name == null)
            return HARDCORE;
        return switch (name.toLowerCase())
        {
            case "normal" -> NORMAL;
            case "overpowered" -> OVERPOWERED;
            case "reloaded" -> RELOADED;
            case "custom", "custom_1" -> CUSTOM_1;
            case "custom_2" -> CUSTOM_2;
            case "custom_3" -> CUSTOM_3;
            case "custom_4" -> CUSTOM_4;
            case "custom_5" -> CUSTOM_5;
            default -> HARDCORE;
        };
    }

    public static Set<PlayerKit> customKits() {
        return ImmutableSet.of(CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5);
    }
}
