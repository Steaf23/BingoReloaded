package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.TimedItem;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum PlayerKit
{
    HARDCORE(TranslationData.translate("kits.hardcore"), EnumSet.noneOf(EffectOptionFlags.class)),
    NORMAL(TranslationData.translate("kits.normal"), EnumSet.of(EffectOptionFlags.CARD_SPEED, EffectOptionFlags.NO_FALL_DAMAGE)),
    OVERPOWERED(TranslationData.translate("kits.overpowered"), EnumSet.allOf(EffectOptionFlags.class)),
    RELOADED(TranslationData.translate("kits.reloaded"), EnumSet.allOf(EffectOptionFlags.class)),
    ;

    public final TimedItem wandItem = new TimedItem(new InventoryItem(
            Material.WARPED_FUNGUS_ON_A_STICK,
            "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "The Go-Up-Wand",
            "Right-Click To Teleport Upwards!"
    ).withEnchantment(Enchantment.DURABILITY, 3), (int)(ConfigData.getConfig().wandCooldown * 1000))
    {
        @Override
        public void use(Player player)
        {
            if (player.isSneaking())
            {
                teleportPlayerUp(player, -ConfigData.getConfig().wandDown, 0);
            }
            else
            {
                teleportPlayerUp(player, ConfigData.getConfig().wandUp, 5);
            }

            player.playSound(player, Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * 10, 100, false, false));
        }

        private static void teleportPlayerUp(Player player, int distance, int fallDistance)
        {
            Location newLocation = player.getLocation();
            newLocation.setY(newLocation.getY() + distance + fallDistance);
            player.teleport(newLocation, PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
            newLocation.setY(newLocation.getY() - fallDistance);
            BingoGame.spawnPlatform(newLocation, 1);
        }
    };

    public final InventoryItem cardItem = new InventoryItem(8,
            Material.MAP,
            "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + ChatColor.BOLD + "Bingo Card",
            "Click To Open The Bingo Card!");

    public final String displayName;
    public final EnumSet<EffectOptionFlags> defaultEffects;

    PlayerKit(String displayName, EnumSet<EffectOptionFlags> defaultEffects)
    {
        this.displayName = displayName;
        this.defaultEffects = defaultEffects;
    }

    public List<InventoryItem> getItems(FlexibleColor teamColor)
    {
        InventoryItem helmet = new InventoryItem(39, Material.LEATHER_HELMET, "");
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        if (helmetMeta != null)
        {
            helmetMeta.setColor(FlexibleColor.toBukkitColor(teamColor.chatColor.getColor()));
        }
        helmet.setItemMeta(helmetMeta);

        InventoryItem boots = new InventoryItem(36, Material.LEATHER_BOOTS, "");
        LeatherArmorMeta bootMeta = (LeatherArmorMeta) boots.getItemMeta();
        if (bootMeta != null)
        {
            bootMeta.setColor(FlexibleColor.toBukkitColor(teamColor.chatColor.getColor()));
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
                items.add(wandItem.item.inSlot(7));
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
                items.add(wandItem.item.inSlot(7));
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
                    default -> HARDCORE;
                };
    }
}
