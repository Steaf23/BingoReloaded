package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.BingoStatsData;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.item.ItemCooldownManager;
import io.github.steaf23.bingoreloaded.item.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.PDCHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import javax.naming.Name;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

/**
 * This Record describes a player in a game of bingo.
 * The same BingoPlayer instance can be used for multiple games
 * as long as they are taking place in the same world and the player stays in the same team.
 * This record will still exist if the player leaves the game/world.
 * This record will exist during at least the currently ongoing game.
 * @param playerId UUID of the player
 * @param team Team of the player
 * @param worldName World name of the game this player can play in.
 */
public record BingoPlayer(UUID playerId, BingoTeam team, String worldName, String playerName, String displayName)
{
    @Nullable
    public Optional<Player> gamePlayer()
    {
        if (!offline().isOnline())
            return Optional.ofNullable(null);

        Player player = Bukkit.getPlayer(playerId);
        if (!GameWorldManager.getWorldName(player.getWorld()).equals(worldName))
        {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(player);
    }

    @Nullable
    public Optional<Player> asOnlinePlayer()
    {
        return Optional.ofNullable(Bukkit.getPlayer(playerId));
    }

    public OfflinePlayer offline()
    {
        return Bukkit.getOfflinePlayer(playerId);
    }

    public void giveKit(PlayerKit kit)
    {
        if (gamePlayer().isEmpty())
            return;

        Player player = gamePlayer().get();

        Message.log("Giving kit to " + player.getDisplayName(), worldName);

        var items = kit.getItems(team.getColor());
        player.closeInventory();
        Inventory inv = player.getInventory();
        inv.clear();
        items.forEach(i ->
        {
            var meta = i.getItemMeta();

            // Show enchantments except on the wand
            if (!PlayerKit.wandItem.isKeyEqual(i))
            {
                meta.removeItemFlags(ItemFlag.values());
            }
            var pdc = meta.getPersistentDataContainer();
            pdc = PDCHelper.setBoolean(pdc, "kit.kit_item", true);

            i.setItemMeta(meta);
            inv.setItem(i.getSlot(), i);
        });
    }

    public void giveBingoCard()
    {
        if (gamePlayer().isEmpty())
            return;

        Player player = gamePlayer().get();

        Message.log("Giving card to " + player.getDisplayName(), worldName);

        BingoReloaded.scheduleTask(task -> {
            for (ItemStack itemStack : player.getInventory())
            {
                if (PlayerKit.cardItem.isKeyEqual(itemStack))
                {
                    player.getInventory().remove(itemStack);
                    break;
                }
            }

            player.getInventory().setItemInOffHand(PlayerKit.cardItem.inSlot(8));
        });
    }

    public void giveEffects(EnumSet<EffectOptionFlags> effects)
    {
        if (gamePlayer().isEmpty())
            return;

        takeEffects(false);
        Player player = gamePlayer().get();

        Message.log("Giving effects to " + player.getDisplayName(), worldName);

        BingoReloaded.scheduleTask(task -> {
            if (effects.contains(EffectOptionFlags.NIGHT_VISION))
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, false, false));
            if (effects.contains(EffectOptionFlags.WATER_BREATHING))
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100000, 1, false, false));
            if (effects.contains(EffectOptionFlags.FIRE_RESISTANCE))
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100000, 1, false, false));
            if (effects.contains(EffectOptionFlags.SPEED))
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 1, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 2, 100, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2, 100, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * ConfigData.instance.gracePeriod, 100, false, false));
        });
    }

    /**
     *
     * @param force ignore if the player is actually in the world playing the game at this moment.
     */
    public void takeEffects(boolean force)
    {
        if (force)
        {
            if (offline().isOnline())
            {
                Message.log("Taking effects from " + asOnlinePlayer().get().getDisplayName(), worldName);

                for (PotionEffectType effect : PotionEffectType.values())
                {
                    Bukkit.getPlayer(playerId).removePotionEffect(effect);
                }
            }
        }
        else
        {
            if (gamePlayer().isEmpty())
                return;

            Message.log("Taking effects from " + asOnlinePlayer().get().getDisplayName(), worldName);

            for (PotionEffectType effect : PotionEffectType.values())
            {
                gamePlayer().get().removePotionEffect(effect);
            }
        }
    }

    public void showDeathMatchItem(Material deathMatchItem)
    {
        if (gamePlayer().isEmpty())
            return;

        String itemKey = deathMatchItem.isBlock() ? "block" : "item";
        itemKey += ".minecraft." + deathMatchItem.getKey().getKey();

        new Message("game.item.deathmatch").color(ChatColor.GOLD)
                .component(new TranslatableComponent(itemKey))
                .send(gamePlayer().get());
    }

    public boolean useGoUpWand(ItemStack wand)
    {
        if (gamePlayer().isEmpty())
             return false;

        Player player = gamePlayer().get();
        if (!PlayerKit.wandItem.isKeyEqual(wand))
            return false;

        if (!ItemCooldownManager.isCooldownOver(playerId, wand))
        {
            double timeLeft = ItemCooldownManager.getTimeLeft(playerId, wand) / 1000.0;
            new Message("game.item.cooldown").color(ChatColor.RED).arg(String.format("%.2f", timeLeft)).send(player);
            return false;
        }

        BingoReloaded.scheduleTask(task -> {
            ItemCooldownManager.addCooldown(playerId, wand, (int)(ConfigData.instance.wandCooldown * 1000));

            double distance = 0.0;
            double fallDistance = 5.0;
            // Use the wand
            if (gamePlayer().get().isSneaking())
            {
                distance = -ConfigData.instance.wandDown;
                fallDistance = 0.0;
            }
            else
            {
                distance = ConfigData.instance.wandUp + 5;
                fallDistance = 5.0;
            }

            Location newLocation = player.getLocation();
            newLocation.setY(newLocation.getY() + distance + fallDistance);
            player.teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            newLocation.setY(newLocation.getY() - fallDistance);

            BingoGame.spawnPlatform(newLocation, 1);

            BingoReloaded.scheduleTask(laterTask -> {
                BingoGame.removePlatform(newLocation, 1);
            }, Math.max(0, ConfigData.instance.platformLifetime) * BingoReloaded.ONE_SECOND);

            player.playSound(player, Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * 10, 100, false, false));

            BingoStatsData.incrementPlayerStat(player, BingoStatType.WAND_USES);
        });
        return true;
    }

    @Nullable
    public BingoTeam getTeam()
    {
        return team().players.contains(this) ? team() : null;
    }
}
