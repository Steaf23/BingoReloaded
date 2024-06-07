package io.github.steaf23.bingoreloaded.settings;

import com.google.common.collect.ImmutableSet;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import io.github.steaf23.easymenulib.inventory.item.ItemTemplate;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public enum PlayerKit
{
    HARDCORE("hardcore", BingoTranslation.KIT_HARDCORE_NAME.translate(), EnumSet.noneOf(EffectOptionFlags.class)),
    NORMAL("normal", BingoTranslation.KIT_NORMAL_NAME.translate(), EnumSet.of(EffectOptionFlags.SPEED, EffectOptionFlags.NO_FALL_DAMAGE)),
    OVERPOWERED("overpowered", BingoTranslation.KIT_OVERPOWERED_NAME.translate(), EnumSet.allOf(EffectOptionFlags.class)),
    RELOADED("reloaded", BingoTranslation.KIT_RELOADED_NAME.translate(), EnumSet.allOf(EffectOptionFlags.class)),
    CUSTOM_1("custom_1", "CUSTOM 1", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_2("custom_2", "CUSTOM 2", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_3("custom_3", "CUSTOM 3", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_4("custom_4", "CUSTOM 4", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_5("custom_5", "CUSTOM 5", EnumSet.noneOf(EffectOptionFlags.class)),
    ;

    public static final ItemTemplate WAND_ITEM = new ItemTemplate(
            Material.WARPED_FUNGUS_ON_A_STICK,
            ChatComponentUtils.convert(BingoTranslation.WAND_ITEM_NAME.translate(), ChatColor.DARK_PURPLE, ChatColor.ITALIC, ChatColor.BOLD),
            ChatComponentUtils.createComponentsFromString(BingoTranslation.WAND_ITEM_DESC.translate().split("\\n"))
    )
            .addEnchantment(Enchantment.UNBREAKING, 3)
            .setCompareKey("wand");

    public static final ItemTemplate CARD_ITEM = new ItemTemplate(
            Material.GLOBE_BANNER_PATTERN,
            ChatComponentUtils.convert(BingoTranslation.CARD_ITEM_NAME.translate(), ChatColor.DARK_PURPLE, ChatColor.ITALIC, ChatColor.BOLD),
            ChatComponentUtils.createComponentsFromString(BingoTranslation.CARD_ITEM_DESC.translate().split("\\n"))
    )
            .setGlowing(true)
            .setCompareKey("card");

    public static final ItemTemplate VOTE_ITEM = new ItemTemplate(
            Material.EMERALD,
            ChatComponentUtils.convert(BingoTranslation.VOTE_ITEM_NAME.translate(), ChatColor.GREEN, ChatColor.BOLD),
            ChatComponentUtils.createComponentsFromString(BingoTranslation.VOTE_ITEM_DESC.translate().split("\\n"))
    )
            .setCompareKey("vote");

    public static final ItemTemplate TEAM_ITEM = new ItemTemplate(
            Material.WHITE_GLAZED_TERRACOTTA,
            ChatComponentUtils.convert(BingoTranslation.TEAM_ITEM_NAME.translate(), ChatColor.AQUA, ChatColor.BOLD),
            ChatComponentUtils.createComponentsFromString(BingoTranslation.TEAM_ITEM_DESC.translate().split("\\n"))
    )
            .setCompareKey("team");

    public final String configName;
    private final String displayName;
    public final EnumSet<EffectOptionFlags> defaultEffects;

    private static final YmlDataManager customKitData = BingoReloaded.createYmlDataManager("data/kits.yml");

    PlayerKit(String configName, String displayName, EnumSet<EffectOptionFlags> defaultEffects)
    {
        this.configName = configName;
        this.displayName = displayName;
        this.defaultEffects = defaultEffects;
    }

    public String getDisplayName() {
        if (getCustomKit(this) != null) {
            return getCustomKit(this).name();
        }
        return displayName;
    }

    public List<SerializableItem> getItems(ChatColor teamColor)
    {
        ItemTemplate helmet = new ItemTemplate(39, Material.LEATHER_HELMET)
                .setLeatherColor(ChatColor.of(teamColor.getColor()));
        ItemTemplate boots = new ItemTemplate(36, Material.LEATHER_BOOTS)
                .setLeatherColor(ChatColor.of(teamColor.getColor()));

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
                CustomKit kit = customKitData.getConfig().getSerializable(configName, CustomKit.class);
                if (kit != null)
                {
                    return kit.items();
                }
            }
        }

        List<SerializableItem> playerItems = items.stream().map(SerializableItem::fromItemTemplate).collect(Collectors.toList());
        return playerItems;
    }

    public int getCardSlot() {
        if (isCustomKit()) {
            return getCustomKit(this).cardSlot();
        }
        else {
            // off-hand slot: 40
            return 40;
        }
    }

    public boolean isCustomKit() {
        return customKits().contains(this);
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

    public static boolean assignCustomKit(String kitName, PlayerKit slot, Player commandSender)
    {
        if (customKitData.getConfig().contains(slot.configName))
            return false;

        customKitData.getConfig().set(slot.configName, CustomKit.fromPlayerInventory(commandSender, kitName, slot));
        customKitData.saveConfig();
        return true;
    }

    public static boolean removeCustomKit(PlayerKit slot)
    {
        if (!customKitData.getConfig().contains(slot.configName))
            return false;

        customKitData.getConfig().set(slot.configName, null);
        customKitData.saveConfig();

        return true;
    }

    public static @Nullable CustomKit getCustomKit(PlayerKit slot)
    {
        return customKitData.getConfig().getSerializable(slot.configName, CustomKit.class);
    }

    public static Set<PlayerKit> customKits() {
        return ImmutableSet.of(CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5);
    }
}
