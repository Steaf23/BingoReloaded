package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum PlayerKit
{
    HARDCORE("hardcore", ChatColor.BOLD + BingoTranslation.KIT_HARDCORE_NAME.translate(), EnumSet.noneOf(EffectOptionFlags.class)),
    NORMAL("normal", ChatColor.BOLD + BingoTranslation.KIT_NORMAL_NAME.translate(), EnumSet.of(EffectOptionFlags.SPEED, EffectOptionFlags.NO_FALL_DAMAGE)),
    OVERPOWERED("overpowered", ChatColor.BOLD + BingoTranslation.KIT_OVERPOWERED_NAME.translate(), EnumSet.allOf(EffectOptionFlags.class)),
    RELOADED("reloaded", ChatColor.BOLD + BingoTranslation.KIT_RELOADED_NAME.translate(), EnumSet.allOf(EffectOptionFlags.class)),
    CUSTOM_1("custom_1", ChatColor.BOLD + BingoTranslation.KIT_CUSTOM_NAME.translate() + " 1", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_2("custom_2", ChatColor.BOLD + BingoTranslation.KIT_CUSTOM_NAME.translate() + " 2", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_3("custom_3", ChatColor.BOLD + BingoTranslation.KIT_CUSTOM_NAME.translate() + " 3", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_4("custom_4", ChatColor.BOLD + BingoTranslation.KIT_CUSTOM_NAME.translate() + " 4", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_5("custom_5", ChatColor.BOLD + BingoTranslation.KIT_CUSTOM_NAME.translate() + " 5", EnumSet.noneOf(EffectOptionFlags.class)),
    ;

    public static final MenuItem WAND_ITEM = new MenuItem(
            Material.WARPED_FUNGUS_ON_A_STICK,
            "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + BingoTranslation.WAND_ITEM_NAME.translate(),
            BingoTranslation.WAND_ITEM_DESC.translate().split("\\n")
    ).withEnchantment(Enchantment.DURABILITY, 3).setCompareKey("wand");
    public static final MenuItem CARD_ITEM = new MenuItem(
            Material.MAP,
            "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + BingoTranslation.CARD_ITEM_NAME.translate(),
            BingoTranslation.CARD_ITEM_DESC.translate()
    ).setCompareKey("card");

    public static final MenuItem VOTE_ITEM = new MenuItem(
            Material.EMERALD,
            "" + ChatColor.GREEN + ChatColor.BOLD + BingoTranslation.VOTE_ITEM_NAME.translate(),
            BingoTranslation.VOTE_ITEM_DESC.translate().split("\\n")
    ).setCompareKey("vote");

    public static final MenuItem TEAM_ITEM = new MenuItem(
            Material.WHITE_GLAZED_TERRACOTTA,
            "" + ChatColor.AQUA + ChatColor.BOLD + BingoTranslation.TEAM_ITEM_NAME.translate(),
            BingoTranslation.TEAM_ITEM_DESC.translate().split("\\n")
    ).setCompareKey("team");

    public final String configName;
    public final String displayName;
    public final EnumSet<EffectOptionFlags> defaultEffects;

    private static final YmlDataManager customKitData = BingoReloaded.createYmlDataManager("data/kits.yml");

    PlayerKit(String configName, String displayName, EnumSet<EffectOptionFlags> defaultEffects)
    {
        this.configName = configName;
        this.displayName = displayName;
        this.defaultEffects = defaultEffects;
    }

    public List<MenuItem> getItems(FlexColor teamColor)
    {
        MenuItem helmet = new MenuItem(39, Material.LEATHER_HELMET, "");
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        if (helmetMeta != null)
        {
            helmetMeta.setColor(FlexColor.toBukkitColor(teamColor.chatColor.getColor()));
        }
        helmet.setItemMeta(helmetMeta);

        MenuItem boots = new MenuItem(36, Material.LEATHER_BOOTS, "");
        LeatherArmorMeta bootMeta = (LeatherArmorMeta) boots.getItemMeta();
        if (bootMeta != null)
        {
            bootMeta.setColor(FlexColor.toBukkitColor(teamColor.chatColor.getColor()));
        }
        boots.setItemMeta(bootMeta);

        List<MenuItem> items;
        switch (this)
        {
            case NORMAL -> {
                items = new ArrayList<>();
                items.add(helmet
                        .withEnchantment(Enchantment.WATER_WORKER, 1));
                items.add(boots
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3));
                items.add(new MenuItem(1, Material.IRON_PICKAXE, ""));
                items.add(new MenuItem(0, Material.IRON_AXE, ""));
                items.add(new MenuItem(2, Material.IRON_SHOVEL, "")
                        .withEnchantment(Enchantment.SILK_TOUCH, 1));
                items.add(new MenuItem(3, Material.COOKED_PORKCHOP, "")
                        .withAmount(32));
                return items;
            }
            case OVERPOWERED -> {
                items = new ArrayList<>();
                items.add(WAND_ITEM.copyToSlot(8));
                items.add(helmet
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.WATER_WORKER, 1)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(boots
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(new MenuItem(1, Material.NETHERITE_PICKAXE, "")
                        .withEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new MenuItem(0, Material.NETHERITE_AXE, "")
                        .withIllegalEnchantment(Enchantment.LOOT_BONUS_MOBS, 3)
                        .withIllegalEnchantment(Enchantment.DAMAGE_ALL, 5)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new MenuItem(2, Material.NETHERITE_SHOVEL, "")
                        .withEnchantment(Enchantment.SILK_TOUCH, 1)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new MenuItem(3, Material.GOLDEN_CARROT, "")
                        .withAmount(64));
                return items;
            }
            case RELOADED -> {
                items = new ArrayList<>();
                items.add(WAND_ITEM.copyToSlot(8));
                items.add(helmet
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.WATER_WORKER, 1)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(boots
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(new MenuItem(38, Material.ELYTRA, "")
                        .withIllegalEnchantment(Enchantment.DURABILITY, 10));
                items.add(new MenuItem(0, Material.NETHERITE_AXE, "")
                        .withIllegalEnchantment(Enchantment.LOOT_BONUS_MOBS, 3)
                        .withIllegalEnchantment(Enchantment.DAMAGE_ALL, 5)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new MenuItem(1, Material.NETHERITE_PICKAXE, "")
                        .withIllegalEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3)
                        .withIllegalEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new MenuItem(2, Material.NETHERITE_SHOVEL, "")
                        .withEnchantment(Enchantment.SILK_TOUCH, 1)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new MenuItem(3, Material.ENCHANTED_GOLDEN_APPLE, "")
                        .withAmount(64));
                return items;
            }
            case CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5 -> {
                CustomKit kit = customKitData.getConfig().getSerializable(configName, CustomKit.class);
                if (kit != null)
                {
                    items = kit.items();
                }
                else
                {
                    items = new ArrayList<>();
                }
                return items;
            }
            default -> {
                items = new ArrayList<>();
                return items;
            }
        }
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
            case "custom", "custom1" -> CUSTOM_1;
            case "custom2" -> CUSTOM_2;
            case "custom3" -> CUSTOM_3;
            case "custom4" -> CUSTOM_4;
            case "custom5" -> CUSTOM_5;
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

    public static CustomKit getCustomKit(PlayerKit slot)
    {
        return customKitData.getConfig().getSerializable(slot.configName, CustomKit.class);
    }

    private static MenuItem createGoUpWand()
    {
        MenuItem wand = new MenuItem(
                Material.WARPED_FUNGUS_ON_A_STICK,
                "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + BingoTranslation.WAND_ITEM_NAME.translate(),
                BingoTranslation.WAND_ITEM_DESC.translate().split("\\n")).withEnchantment(Enchantment.DURABILITY, 3);
        wand.setCompareKey("wand");
        return wand;
    }

    private static MenuItem createCardItem()
    {
        MenuItem card = new MenuItem(
                Material.MAP,
                "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + BingoTranslation.CARD_ITEM_NAME.translate(),
                BingoTranslation.CARD_ITEM_DESC.translate());
        card.setCompareKey("card");
        return card;
    }
}
