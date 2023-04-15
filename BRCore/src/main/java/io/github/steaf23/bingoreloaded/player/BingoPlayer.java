package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGameManager;
import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.item.ItemCooldownManager;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.PDCHelper;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

/**
 * This class describes a player in a single bingo session.
 * This class will still exist if the player leaves the game/world.
 * This instance will be removed when the session gets destroyed.
 */
public class BingoPlayer
{
    public final UUID playerId;
    public final BingoTeam team;
    public final BingoSession session;
    public final String playerName;
    public final String displayName;
    private final ItemCooldownManager itemCooldowns;

    public BingoPlayer(Player player, BingoTeam team, BingoSession session)
    {
        this.playerId = player.getUniqueId();
        this.team = team;
        this.session = session;
        this.playerName = player.getName();
        this.displayName = player.getDisplayName();
        this.itemCooldowns = new ItemCooldownManager();
    }

    @Nullable
    public Optional<Player> gamePlayer()
    {
        if (!offline().isOnline())
            return Optional.ofNullable(null);

        Player player = Bukkit.getPlayer(playerId);
        if (!BingoGameManager.getWorldName(player.getWorld()).equals(session.worldName))
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

        Message.log("Giving kit to " + player.getDisplayName(), session.worldName);

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

        Message.log("Giving card to " + player.getDisplayName(), session.worldName);

        BingoReloadedCore.scheduleTask(task -> {
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

        Message.log("Giving effects to " + player.getDisplayName(), session.worldName);

        BingoReloadedCore.scheduleTask(task -> {
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloadedCore.ONE_SECOND * BingoReloadedCore.get().config().gracePeriod, 100, false, false));
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
                Message.log("Taking effects from " + asOnlinePlayer().get().getDisplayName(), session.worldName);

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

            Message.log("Taking effects from " + asOnlinePlayer().get().getDisplayName(), session.worldName);

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

        new TranslatedMessage("game.item.deathmatch").color(ChatColor.GOLD)
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

        if (!itemCooldowns.isCooldownOver(wand))
        {
            double timeLeft = itemCooldowns.getTimeLeft(wand) / 1000.0;
            new TranslatedMessage("game.item.cooldown").color(ChatColor.RED).arg(String.format("%.2f", timeLeft)).send(player);
            return false;
        }

        BingoReloadedCore.scheduleTask(task -> {
            itemCooldowns.addCooldown(wand, (int)(BingoReloadedCore.get().config().wandCooldown * 1000));

            double distance = 0.0;
            double fallDistance = 5.0;
            // Use the wand
            if (gamePlayer().get().isSneaking())
            {
                distance = -BingoReloadedCore.get().config().wandDown;
                fallDistance = 0.0;
            }
            else
            {
                distance = BingoReloadedCore.get().config().wandUp + 5;
                fallDistance = 5.0;
            }

            Location newLocation = player.getLocation();
            newLocation.setY(newLocation.getY() + distance + fallDistance);
            player.teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            newLocation.setY(newLocation.getY() - fallDistance);

            BingoGame.spawnPlatform(newLocation, 1);

            BingoReloadedCore.scheduleTask(laterTask -> {
                BingoGame.removePlatform(newLocation, 1);
            }, Math.max(0, BingoReloadedCore.get().config().platformLifetime) * BingoReloadedCore.ONE_SECOND);

            player.playSound(player, Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloadedCore.ONE_SECOND * 10, 100, false, false));

            BingoReloadedCore.incrementPlayerStat(player, BingoStatType.WAND_USES);
        });
        return true;
    }

    @Nullable
    public BingoTeam getTeam()
    {
        return team.players.contains(this) ? team : null;
    }
}
