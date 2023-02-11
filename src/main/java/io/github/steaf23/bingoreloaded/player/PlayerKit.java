package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.sql.Array;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum PlayerKit
{
    HARDCORE(TranslationData.itemName("menu.kits.hardcore"), EnumSet.noneOf(EffectOptionFlags.class)),
    NORMAL(TranslationData.itemName("menu.kits.normal"), EnumSet.of(EffectOptionFlags.SPEED, EffectOptionFlags.NO_FALL_DAMAGE)),
    OVERPOWERED(TranslationData.itemName("menu.kits.overpowered"), EnumSet.allOf(EffectOptionFlags.class)),
    RELOADED(TranslationData.itemName("menu.kits.reloaded"), EnumSet.allOf(EffectOptionFlags.class)),
    CUSTOM_1(TranslationData.itemName("menu.kits.custom") + "1", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_2(TranslationData.itemName("menu.kits.custom") + "2", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_3(TranslationData.itemName("menu.kits.custom") + "3", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_4(TranslationData.itemName("menu.kits.custom") + "4", EnumSet.noneOf(EffectOptionFlags.class)),
    CUSTOM_5(TranslationData.itemName("menu.kits.custom") + "5", EnumSet.noneOf(EffectOptionFlags.class)),
    ;

    public static final InventoryItem cardItem = createCardItem();

    public static final InventoryItem wandItem = createGoUpWand();
    public final String displayName;
    public final EnumSet<EffectOptionFlags> defaultEffects;

    PlayerKit(String displayName, EnumSet<EffectOptionFlags> defaultEffects)
    {
        this.displayName = displayName;
        this.defaultEffects = defaultEffects;
    }

    public List<InventoryItem> getItems(FlexColor teamColor)
    {
        InventoryItem helmet = new InventoryItem(39, Material.LEATHER_HELMET, "");
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        if (helmetMeta != null)
        {
            helmetMeta.setColor(FlexColor.toBukkitColor(teamColor.chatColor.getColor()));
        }
        helmet.setItemMeta(helmetMeta);

        InventoryItem boots = new InventoryItem(36, Material.LEATHER_BOOTS, "");
        LeatherArmorMeta bootMeta = (LeatherArmorMeta) boots.getItemMeta();
        if (bootMeta != null)
        {
            bootMeta.setColor(FlexColor.toBukkitColor(teamColor.chatColor.getColor()));
        }
        boots.setItemMeta(bootMeta);

        List<InventoryItem> items;
        switch (this)
        {
            case NORMAL -> {
                items = new ArrayList<>();
                items.add(helmet
                        .withEnchantment(Enchantment.WATER_WORKER, 1));
                items.add(boots
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3));
                items.add(new InventoryItem(1, Material.IRON_PICKAXE, ""));
                items.add(new InventoryItem(0, Material.IRON_AXE, ""));
                items.add(new InventoryItem(2, Material.IRON_SHOVEL, "")
                        .withEnchantment(Enchantment.SILK_TOUCH, 1));
                items.add(new InventoryItem(3, Material.COOKED_PORKCHOP, "")
                        .withAmount(32));
                return items;
            }
            case OVERPOWERED -> {
                items = new ArrayList<>();
                items.add(wandItem.inSlot(8));
                items.add(helmet
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.WATER_WORKER, 1)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(boots
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(new InventoryItem(1, Material.NETHERITE_PICKAXE, "")
                        .withEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(0, Material.NETHERITE_AXE, "")
                        .withIllegalEnchantment(Enchantment.LOOT_BONUS_MOBS, 3)
                        .withIllegalEnchantment(Enchantment.DAMAGE_ALL, 5)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(2, Material.NETHERITE_SHOVEL, "")
                        .withEnchantment(Enchantment.SILK_TOUCH, 1)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(3, Material.GOLDEN_CARROT, "")
                        .withAmount(64));
                return items;
            }
            case RELOADED -> {
                items = new ArrayList<>();
                items.add(wandItem.inSlot(8));
                items.add(helmet
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.WATER_WORKER, 1)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(boots
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(new InventoryItem(38, Material.ELYTRA, "")
                        .withIllegalEnchantment(Enchantment.DURABILITY, 10));
                items.add(new InventoryItem(0, Material.NETHERITE_AXE, "")
                        .withIllegalEnchantment(Enchantment.LOOT_BONUS_MOBS, 3)
                        .withIllegalEnchantment(Enchantment.DAMAGE_ALL, 5)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(1, Material.NETHERITE_PICKAXE, "")
                        .withIllegalEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3)
                        .withIllegalEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(2, Material.NETHERITE_SHOVEL, "")
                        .withEnchantment(Enchantment.SILK_TOUCH, 1)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(3, Material.ENCHANTED_GOLDEN_APPLE, "")
                        .withAmount(64));
                return items;
            }
            case CUSTOM_1 -> {
                items = new ArrayList<>();
                items.add(helmet
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.WATER_WORKER, 1)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(boots
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(new InventoryItem(1, Material.NETHERITE_PICKAXE, "")
                        .withEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(0, Material.NETHERITE_AXE, "")
                        .withIllegalEnchantment(Enchantment.LOOT_BONUS_MOBS, 3)
                        .withIllegalEnchantment(Enchantment.DAMAGE_ALL, 5)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(2, Material.NETHERITE_SHOVEL, "")
                        .withEnchantment(Enchantment.SILK_TOUCH, 1)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(3, Material.GOLDEN_CARROT, "")
                        .withAmount(64));
                return items;
            }
            default -> {
                //TODO: Implement CUSTOM kit
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
                    case "custom" -> CUSTOM_1;
                    default -> HARDCORE;
                };
    }

    private static InventoryItem createGoUpWand()
    {
        InventoryItem wand = new InventoryItem(
                (int)(ConfigData.instance.wandCooldown * 1000),
                Material.WARPED_FUNGUS_ON_A_STICK,
                "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + TranslationData.itemName("items.wand"),
                TranslationData.itemDescription("items.wand")).withEnchantment(Enchantment.DURABILITY, 3);
        wand.setKey("wand");
        return wand;
    }

    private static InventoryItem createCardItem()
    {
        InventoryItem card = new InventoryItem(
                Material.MAP,
                "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + TranslationData.itemName("items.card"),
                TranslationData.itemDescription("items.card"));
        card.setKey("card");
        return card;
    }
}
