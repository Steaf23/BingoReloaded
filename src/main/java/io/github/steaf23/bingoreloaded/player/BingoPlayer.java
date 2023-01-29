package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.BingoStatsData;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.item.ItemCooldownManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;
import java.util.UUID;

public record BingoPlayer(UUID playerId, BingoTeam team, String worldName)
{
    public Player player()
    {
        return Bukkit.getPlayer(playerId);
    }

    public boolean isOnline()
    {
        return Bukkit.getPlayer(playerId) != null;
    }

    public OfflinePlayer offline()
    {
        return Bukkit.getOfflinePlayer(playerId);
    }

    public boolean isInBingoWorld(String world)
    {
        return isOnline() &&
                GameWorldManager.getWorldName(player().getWorld()).equals(world) &&
                GameWorldManager.get().doesGameWorldExist(player().getWorld().getName());

    }

    public void giveKit(PlayerKit kit)
    {
        if (!isInBingoWorld(worldName))
            return;

        var items = kit.getItems(team.getColor());
        player().closeInventory();
        Inventory inv = player().getInventory();
        inv.clear();
        items.forEach(i -> inv.setItem(i.getSlot(), i));
    }

    public void giveBingoCard()
    {
        if (!isInBingoWorld(worldName))
            return;

        while (player().getInventory().contains(PlayerKit.cardItem.getAsStack()))
            player().getInventory().remove(PlayerKit.cardItem.getAsStack());

        player().getInventory().setItem(8, PlayerKit.cardItem.getAsStack());
    }

    public void giveEffects(EnumSet<EffectOptionFlags> effects)
    {
        if (!isInBingoWorld(worldName))
            return;

        takeEffects();

        if (effects.contains(EffectOptionFlags.NIGHT_VISION))
            player().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, false, false));
        if (effects.contains(EffectOptionFlags.WATER_BREATHING))
            player().addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100000, 1, false, false));
        if (effects.contains(EffectOptionFlags.FIRE_RESISTANCE))
            player().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000, 1, false, false));

        player().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 2, 100, false, false));
        player().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2, 100, false, false));
        player().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * ConfigData.instance.gracePeriod, 100, false, false));
    }

    public void takeEffects()
    {
        if (!isInBingoWorld(worldName))
            return;

        for (PotionEffectType effect : PotionEffectType.values())
        {
            player().removePotionEffect(effect);
        }
    }

    public void showDeathMatchItem(Material deathMatchItem)
    {
        if (!isInBingoWorld(worldName))
            return;

        String itemKey = deathMatchItem.isBlock() ? "block" : "item";
        itemKey += ".minecraft." + deathMatchItem.getKey().getKey();

        new BingoMessage("game.item.deathmatch").color(ChatColor.GOLD)
                .component(new TranslatableComponent(itemKey))
                .send(player());
    }

    public boolean useGoUpWand(ItemStack wand)
    {
        if (!isInBingoWorld(worldName))
            return false;

        if (!wand.equals(PlayerKit.wandItem.getAsStack()))
            return false;

        if (ItemCooldownManager.isCooldownOver(playerId, wand))
        {
            ItemCooldownManager.addCooldown(playerId, wand, (int)(ConfigData.instance.wandCooldown * 1000));

            double distance = 0.0;
            double fallDistance = 5.0;
            // Use the wand
            if (player().isSneaking())
            {
                distance = -ConfigData.instance.wandDown;
                fallDistance = 0.0;
            }
            else
            {
                distance = ConfigData.instance.wandUp + 5;
                fallDistance = 5.0;
            }

            Location newLocation = player().getLocation();
            newLocation.setY(newLocation.getY() + distance + fallDistance);
            player().teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            newLocation.setY(newLocation.getY() - fallDistance);

            BingoGame.spawnPlatform(newLocation, 1);

            Bukkit.getScheduler().runTaskLater(BingoReloaded.get(), task -> {
                BingoGame.removePlatform(newLocation, 1);
            }, Math.max(0, ConfigData.instance.platformLifetime) * BingoReloaded.ONE_SECOND);

            player().playSound(player(), Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
            player().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * 10, 100, false, false));

            BingoStatsData.incrementPlayerStat(this, BingoStatType.WAND_USES);
            return true;
        }

        double timeLeft = ItemCooldownManager.getTimeLeft(playerId, wand) / 1000.0;
        new BingoMessage("game.item.cooldown").color(ChatColor.RED).arg(String.format("%.2f", timeLeft)).send(player());
        return false;
    }
}
