package me.steven.bingoreloaded;

import me.steven.bingoreloaded.util.FlexibleColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public enum PlayerKit
{
    HARDCORE(ChatColor.DARK_RED + "Hardcore"),
    NORMAL(ChatColor.YELLOW + "Normal"),
    OVERPOWERED(ChatColor.DARK_PURPLE + "Overpowered"),
    RELOADED(ChatColor.DARK_AQUA + "Reloaded"),
    ;

    PlayerKit(String displayName)
    {
        this.displayName = displayName;
    }

    public List<InventoryItem> getItems(FlexibleColor teamColor)
    {
        InventoryItem helmet = new InventoryItem(39, Material.LEATHER_HELMET, "");
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        if (helmetMeta != null)
        {
            helmetMeta.setColor(teamColor.rgbColor);
        }
        helmet.setItemMeta(helmetMeta);

        InventoryItem boots = new InventoryItem(36, Material.LEATHER_BOOTS, "");
        LeatherArmorMeta bootMeta = (LeatherArmorMeta) boots.getItemMeta();
        if (bootMeta != null)
        {
            bootMeta.setColor(teamColor.rgbColor);
        }
        boots.setItemMeta(bootMeta);

        List<InventoryItem> items;
        switch (this)
        {
            case NORMAL -> {
                items = new ArrayList<>();
                items.add(cardItem.inSlot(8));
                items.add(helmet
                        .withEnchantment(Enchantment.WATER_WORKER, 1));
                items.add(boots
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3));
                items.add(new InventoryItem(0, Material.IRON_PICKAXE, ""));
                items.add(new InventoryItem(1, Material.IRON_AXE, ""));
                items.add(new InventoryItem(2, Material.IRON_SHOVEL, "")
                        .withEnchantment(Enchantment.SILK_TOUCH, 1));
                items.add(new InventoryItem(3, Material.BAKED_POTATO, "")
                        .withAmount(32));
                return items;
            }
            case OVERPOWERED -> {
                items = new ArrayList<>();
                items.add(cardItem.inSlot(8));
                items.add(wandItem.item.inSlot(7));
                items.add(helmet
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.WATER_WORKER, 1)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(boots
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(new InventoryItem(0, Material.NETHERITE_PICKAXE, "")
                        .withEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(1, Material.NETHERITE_AXE, "")
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
                items.add(cardItem.inSlot(8));
                items.add(wandItem.item.inSlot(7));
                items.add(helmet
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.WATER_WORKER, 1)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(boots
                        .withEnchantment(Enchantment.DURABILITY, 3)
                        .withEnchantment(Enchantment.DEPTH_STRIDER, 3)
                        .withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4));
                items.add(new InventoryItem(0, Material.NETHERITE_PICKAXE, "")
                        .withEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(1, Material.NETHERITE_AXE, "")
                        .withEnchantment(Enchantment.LOOT_BONUS_MOBS, 3)
                        .withEnchantment(Enchantment.DAMAGE_ALL, 5)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(2, Material.NETHERITE_SHOVEL, "")
                        .withEnchantment(Enchantment.SILK_TOUCH, 3)
                        .withEnchantment(Enchantment.DIG_SPEED, 5));
                items.add(new InventoryItem(3, Material.ENCHANTED_GOLDEN_APPLE, "")
                        .withAmount(64));
                return items;
            }
            default -> {
                items = new ArrayList<>();
                items.add(cardItem.inSlot(8));
                return items;
            }
        }
    }

    public final ItemCooldownManager wandItem = new ItemCooldownManager(new InventoryItem(
            Material.WARPED_FUNGUS_ON_A_STICK,
            "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "The Go-Up-Wand",
            "Right-Click To Teleport Upwards!"
    ).withEnchantment(Enchantment.DURABILITY, 3), 5000);

    public final InventoryItem cardItem = new InventoryItem(8,
            Material.MAP,
            "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "Bingo Card",
            "Click To Open The Bingo Card!");

    public final String displayName;
}
