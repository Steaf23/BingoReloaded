package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.item.ItemCooldownManager;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
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
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

/**
 * This class describes a player in a single bingo session.
 * This class will still exist if the player leaves the game/world.
 * This instance will be removed when the session gets destroyed.
 */
public class BingoPlayer implements BingoParticipant
{
    private BingoTeam team;
    public final String playerName;
    private final BingoSession session;
    private final UUID playerId;
    private final String displayName;
    private final ItemCooldownManager itemCooldowns;

    private final int POTION_DURATION = 1728000; // 24 Hours

    public BingoPlayer(Player player, BingoSession session)
    {
        this.playerId = player.getUniqueId();
        this.session = session;
        this.playerName = player.getName();
        this.displayName = player.getDisplayName();
        this.itemCooldowns = new ItemCooldownManager();
    }

    /**
     * @return the player that belongs to this BingoPlayer, if this player is in a session world, otherwise returns null
     */
    public Optional<Player> sessionPlayer()
    {
        if (!offline().isOnline())
            return Optional.empty();

        Player player = Bukkit.getPlayer(playerId);
        if (!session.hasPlayer(player))
        {
            return Optional.empty();
        }
        return Optional.ofNullable(player);
    }

    @Override
    public UUID getId()
    {
        return playerId;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
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

    @Override
    public void giveKit(PlayerKit kit)
    {
        if (sessionPlayer().isEmpty())
            return;

        Player player = sessionPlayer().get();

        var items = kit.getItems(team.getColor());
        player.closeInventory();
        Inventory inv = player.getInventory();
        inv.clear();
        items.forEach(i ->
        {
            var meta = i.getItemMeta();

            // Show enchantments except on the wand
            if (!PlayerKit.WAND_ITEM.isCompareKeyEqual(i))
            {
                meta.removeItemFlags(ItemFlag.values());
            }
            var pdc = meta.getPersistentDataContainer();
            pdc = PDCHelper.setBoolean(pdc, "kit.kit_item", true);

            i.setItemMeta(meta);
            inv.setItem(i.getSlot(), i);
        });
    }

    @Override
    public void giveBingoCard()
    {
        if (sessionPlayer().isEmpty())
            return;

        Player player = sessionPlayer().get();

        BingoReloaded.scheduleTask(task -> {
            for (ItemStack itemStack : player.getInventory())
            {
                if (PlayerKit.CARD_ITEM.isCompareKeyEqual(itemStack))
                {
                    player.getInventory().remove(itemStack);
                    break;
                }
            }

            player.getInventory().setItemInOffHand(PlayerKit.CARD_ITEM.copyToSlot(8));
        });
    }

    @Override
    public void giveEffects(EnumSet<EffectOptionFlags> effects, int gracePeriod)
    {
        if (sessionPlayer().isEmpty())
            return;

        takeEffects(false);
        Player player = sessionPlayer().get();

        BingoReloaded.scheduleTask(task -> {
            if (effects.contains(EffectOptionFlags.NIGHT_VISION))
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, POTION_DURATION, 1, false, false));
            if (effects.contains(EffectOptionFlags.WATER_BREATHING))
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, POTION_DURATION, 1, false, false));
            if (effects.contains(EffectOptionFlags.FIRE_RESISTANCE))
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, POTION_DURATION, 1, false, false));
            if (effects.contains(EffectOptionFlags.SPEED))
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, POTION_DURATION, 1, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 2, 100, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2, 100, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * gracePeriod, 100, false, false));
        });
    }

    /**
     *
     * @param force ignore if the player is actually in the world playing the game at this moment.
     */
    @Override
    public void takeEffects(boolean force)
    {
        if (force)
        {
            if (offline().isOnline())
            {
                for (PotionEffectType effect : PotionEffectType.values())
                {
                    Bukkit.getPlayer(playerId).removePotionEffect(effect);
                }
            }
        }
        else
        {
            if (sessionPlayer().isEmpty())
                return;

            for (PotionEffectType effect : PotionEffectType.values())
            {
                sessionPlayer().get().removePotionEffect(effect);
            }
        }
    }

    public void showDeathMatchTask(BingoTask task)
    {
        if (sessionPlayer().isEmpty())
            return;

        String itemKey = task.material.isBlock() ? "block" : "item";
        itemKey += ".minecraft." + task.material.getKey().getKey();

        new TranslatedMessage(BingoTranslation.DEATHMATCH_ITEM).color(ChatColor.GOLD)
                .component(new TranslatableComponent(itemKey))
                .send(sessionPlayer().get());
    }

    @Override
    public boolean alwaysActive()
    {
        return false;
    }

    public boolean useGoUpWand(ItemStack wand, double wandCooldownSeconds, int downDistance, int upDistance, int platformLifetimeSeconds)
    {
        if (sessionPlayer().isEmpty())
             return false;

        Player player = sessionPlayer().get();
        if (!PlayerKit.WAND_ITEM.isCompareKeyEqual(wand))
            return false;

        if (!itemCooldowns.isCooldownOver(wand.getType()))
        {
            double timeLeft = itemCooldowns.getTimeLeft(wand.getType()) / 1000.0;
            new TranslatedMessage(BingoTranslation.COOLDOWN).color(ChatColor.RED).arg(String.format("%.2f", timeLeft)).send(player);
            return false;
        }

        BingoReloaded.scheduleTask(task -> {
            itemCooldowns.addCooldown(wand.getType(), (int)(wandCooldownSeconds * 1000));

            double distance = 0.0;
            double fallDistance = 5.0;
            // Use the wand
            if (sessionPlayer().get().isSneaking())
            {
                distance = -downDistance;
                fallDistance = 0.0;
            }
            else
            {
                distance = upDistance + 5;
                fallDistance = 5.0;
            }

            Location newLocation = player.getLocation();
            newLocation.setY(newLocation.getY() + distance + fallDistance);
            player.teleport(newLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            newLocation.setY(newLocation.getY() - fallDistance);

            BingoGame.spawnPlatform(newLocation, 1);

            BingoReloaded.scheduleTask(laterTask -> {
                BingoGame.removePlatform(newLocation, 1);
            }, Math.max(0, platformLifetimeSeconds) * BingoReloaded.ONE_SECOND);

            player.playSound(player, Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BingoReloaded.ONE_SECOND * 10, 100, false, false));

            BingoReloaded.incrementPlayerStat(player, BingoStatType.WAND_USES);
        });
        return true;
    }

    @Override
    public BingoSession getSession()
    {
        return session;
    }

    @Nullable
    @Override
    public BingoTeam getTeam()
    {
        return team;
    }

    @Override
    public void setTeam(BingoTeam team) {
        this.team = team;
    }
}
