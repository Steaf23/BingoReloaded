package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.item.ItemCooldownManager;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.playerdisplay.util.PDCHelper;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    private final Component displayName;
    private final ItemCooldownManager itemCooldowns;

    private final int POTION_DURATION = 1728000; // 24 Hours

    public BingoPlayer(Player player, BingoSession session)
    {
        this.playerId = player.getUniqueId();
        this.session = session;
        this.playerName = player.getName();
        this.displayName = player.displayName();
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
        return Optional.of(player);
    }

    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public UUID getId()
    {
        return playerId;
    }

    @Override
    public Component getDisplayName()
    {
        return displayName;
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
            var meta = i.stack().getItemMeta();

            // Show enchantments except on the wand
            if (!PlayerKit.WAND_ITEM.isCompareKeyEqual(i.stack()))
            {
                meta.removeItemFlags(ItemFlag.values());
            }
            var pdc = meta.getPersistentDataContainer();
            pdc.set(PDCHelper.createKey("kit.kit_item"), PersistentDataType.BOOLEAN, true);

            i.stack().setItemMeta(meta);
            inv.setItem(i.slot(), i.stack());
        });
    }

    @Override
    public void giveBingoCard(int cardSlot)
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
            ItemStack existingItem = player.getInventory().getItem(cardSlot);
            player.getInventory().setItem(cardSlot, PlayerKit.CARD_ITEM.buildItem());
            if (existingItem != null && !existingItem.getType().isAir()) {
                Map<Integer, ItemStack> leftOver = player.getInventory().addItem(existingItem);
                for (ItemStack stack : leftOver.values()) {
                    player.getWorld().dropItem(player.getLocation(), stack);
                }
            }
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, BingoReloaded.ONE_SECOND * gracePeriod, 100, false, false));
        });
    }

    /**
     * @param force ignore if the player is actually in the world playing the game at this moment.
     */
    @Override
    public void takeEffects(boolean force)
    {
        if (force)
        {
            if (offline().isOnline())
            {
                Bukkit.getPlayer(playerId).clearActivePotionEffects();
            }
        }
        else
        {
            if (sessionPlayer().isEmpty())
                return;

            sessionPlayer().get().clearActivePotionEffects();
        }
    }

    public void showDeathMatchTask(BingoTask task)
    {
        if (sessionPlayer().isEmpty())
            return;

        String itemKey = task.material.isBlock() ? "block" : "item";
        itemKey += ".minecraft." + task.material.getKey().getKey();
        sessionPlayer().get()
                .sendMessage(BingoMessage.DEATHMATCH_ITEM.asPhrase(Component.translatable(itemKey))
                        .color(NamedTextColor.GOLD));
    }

    @Override
    public void showCard(BingoTask deathMatchTask) {
        BingoTeam playerTeam = getTeam();
        if (playerTeam == null) {
            return;
        }
        BingoCard card = playerTeam.getCard();

        sessionPlayer().ifPresent(player -> {
            if (deathMatchTask != null) {
                showDeathMatchTask(deathMatchTask);
                return;
            }

            // if the player is actually participating, show it
            if (card == null) {
                BingoMessage.NO_PLAYER_CARD.sendToAudience(player);
                return;
            }

            card.showInventory(player);
        });
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
            player.sendMessage(BingoMessage.COOLDOWN.asPhrase(Component.text(String.format("%.2f", timeLeft)))
                    .color(NamedTextColor.RED));
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

            BingoGame.spawnPlatform(newLocation, 1, true);

            BingoReloaded.scheduleTask(laterTask -> {
                BingoGame.removePlatform(newLocation, 1);
            }, (long) Math.max(0, platformLifetimeSeconds) * BingoReloaded.ONE_SECOND);

            player.playSound(player, Sound.ENTITY_SHULKER_TELEPORT, 0.8f, 1.0f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, BingoReloaded.ONE_SECOND * 10, 100, false, false));

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

    @Override
    public String toString() {
        return playerName;
    }

    @Override
    public @NotNull Audience audience() {
        return sessionPlayer().isPresent() ? sessionPlayer().get() : Audience.empty();
    }
}
