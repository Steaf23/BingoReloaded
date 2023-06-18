package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class MaterialHelper {

    public static boolean isTool(Material material) {
        return isWoodenTool(material) ||
                isStoneTool(material) ||
                isIronTool(material) ||
                isGoldenTool(material) ||
                isDiamondTool(material) ||
                isNetheriteTool(material);
    }

    public static boolean isArmor(Material material) {
        return isLeatherArmor(material) ||
                isChainmailArmor(material) ||
                isIronArmor(material) ||
                isGoldenArmor(material) ||
                isDiamondArmor(material) ||
                isNetheriteArmor(material);
    }

    private static Boolean isWoodenTool(Material material) {
        return material == Material.WOODEN_SWORD ||
                material == Material.WOODEN_SHOVEL ||
                material == Material.WOODEN_PICKAXE ||
                material == Material.WOODEN_AXE ||
                material == Material.WOODEN_HOE;
    }

    private static Boolean isStoneTool(Material material) {
        return material == Material.STONE_SWORD ||
                material == Material.STONE_SHOVEL ||
                material == Material.STONE_PICKAXE ||
                material == Material.STONE_AXE ||
                material == Material.STONE_HOE;
    }

    private static Boolean isGoldenTool(Material material) {
        return material == Material.GOLDEN_SWORD ||
                material == Material.GOLDEN_SHOVEL ||
                material == Material.GOLDEN_PICKAXE ||
                material == Material.GOLDEN_AXE ||
                material == Material.GOLDEN_HOE;
    }

    private static Boolean isIronTool(Material material) {
        return material == Material.IRON_SWORD ||
                material == Material.IRON_SHOVEL ||
                material == Material.IRON_PICKAXE ||
                material == Material.IRON_AXE ||
                material == Material.IRON_HOE;
    }

    private static Boolean isDiamondTool(Material material) {
        return material == Material.DIAMOND_SWORD ||
                material == Material.DIAMOND_SHOVEL ||
                material == Material.DIAMOND_PICKAXE ||
                material == Material.DIAMOND_AXE ||
                material == Material.DIAMOND_HOE;
    }

    private static Boolean isNetheriteTool(Material material) {
        return material == Material.NETHERITE_SWORD ||
                material == Material.NETHERITE_SHOVEL ||
                material == Material.NETHERITE_PICKAXE ||
                material == Material.NETHERITE_AXE ||
                material == Material.NETHERITE_HOE;
    }

    private static Boolean isLeatherArmor(Material material) {
        return material == Material.LEATHER_BOOTS ||
                material == Material.LEATHER_LEGGINGS ||
                material == Material.LEATHER_CHESTPLATE ||
                material == Material.LEATHER_HELMET;
    }

    private static Boolean isChainmailArmor(Material material) {
        return material == Material.CHAINMAIL_BOOTS ||
                material == Material.CHAINMAIL_LEGGINGS ||
                material == Material.CHAINMAIL_CHESTPLATE ||
                material == Material.CHAINMAIL_HELMET;
    }

    private static Boolean isIronArmor(Material material) {
        return material == Material.IRON_BOOTS ||
                material == Material.IRON_LEGGINGS ||
                material == Material.IRON_CHESTPLATE ||
                material == Material.IRON_HELMET;
    }

    private static Boolean isGoldenArmor(Material material) {
        return material == Material.GOLDEN_BOOTS ||
                material == Material.GOLDEN_LEGGINGS ||
                material == Material.GOLDEN_CHESTPLATE ||
                material == Material.GOLDEN_HELMET;
    }

    private static Boolean isDiamondArmor(Material material) {
        return material == Material.DIAMOND_BOOTS ||
                material == Material.DIAMOND_LEGGINGS ||
                material == Material.DIAMOND_CHESTPLATE ||
                material == Material.DIAMOND_HELMET;
    }

    private static Boolean isNetheriteArmor(Material material) {
        return material == Material.NETHERITE_BOOTS ||
                material == Material.NETHERITE_LEGGINGS ||
                material == Material.NETHERITE_CHESTPLATE ||
                material == Material.NETHERITE_HELMET;
    }
}
