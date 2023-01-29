package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import net.md_5.bungee.api.ChatColor;

import java.util.*;

public class BingoGame implements Listener
{
    private boolean inProgress;
    public GameTimer timer;

    private final String worldName;
    private BingoSettings settings;
    private final BingoScoreboard scoreboard;
    private final Map<UUID, Location> deadPlayers;
    private final TimeNotifier notifier;

    public BingoGame(String worldName)
    {
        this.worldName = worldName;
        this.inProgress = false;
        this.scoreboard = new BingoScoreboard(worldName);
        this.deadPlayers = new HashMap<>();
        this.notifier = new TimeNotifier()
        {
            @Override
            public void timeUpdated(long seconds)
            {
                for (BingoPlayer player : getTeamManager().getParticipants())
                {
                    var p = Optional.ofNullable(player.player());
                    if (player.isInBingoWorld(getWorldName()))
                        Message.sendActionMessage(timer.getTimeDisplayMessage(), p.get());
                }
            }
        };
        BingoReloaded.registerListener(this);
    }

    public void start(BingoSettings settings)
    {
        this.settings = settings;

        if (settings.mode == BingoGamemode.COUNTDOWN)
        {
            timer = new CountdownTimer(settings.countdownGameDuration * 60, 5 * 60, 1 * 60, getWorldName());
        }
        else
        {
            timer = new CounterTimer(getWorldName());
        }
        timer.setNotifier(notifier);

        if (!BingoCardsData.getCardNames().contains(settings.card))
        {
            new Message("game.start.no_card").color(ChatColor.RED).arg(settings.card).sendAll();
            return;
        }

        // Pre-start Setup
        if (getTeamManager().getParticipants().size() == 0)
        {
            new Message("game.start.no_players").color(ChatColor.RED).sendAll();
            return;
        }
        if (inProgress)
        {
            new Message("game.start.already_started").color(ChatColor.RED).sendAll();
            return;
        }

        settings.deathMatchItem = null;
        getTeamManager().updateActivePlayers();
        getTeamManager().removeEmptyTeams();
        World world = Bukkit.getWorld(getWorldName());
        world.setStorm(false);
        world.setTime(1000);

        // Start
        inProgress = true;

        BingoCard masterCard = CardBuilder.fromMode(settings.mode, settings.cardSize, getTeamManager().getActiveTeams().size());
        masterCard.generateCard(settings.card, ConfigData.instance.cardSeed);
        getTeamManager().initializeCards(masterCard);

        new Message("game.start.give_cards").sendAll();
        Set<BingoPlayer> players = getTeamManager().getParticipants();
        players.forEach(p -> p.giveKit(settings.kit));
        players.forEach(this::returnCardToPlayer);
        players.forEach(p -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + p.player().getName() + " everything"));
        players.forEach(p -> {p.player().setLevel(0); p.player().setExp(0.0f);});
        teleportPlayersToStart(world);
        givePlayersEffects();

        // Post-start Setup
        scoreboard.reset();
        scoreboard.updateItemCount();
        timer.start();
    }

    public void resume()
    {
        inProgress = true;
        scoreboard.updateItemCount();
    }

    public void end()
    {
        settings.deathMatchItem = null;
        if(!inProgress)
            return;

        inProgress = false;
        TextComponent[] commandMessage = Message.createHoverCommandMessage("game.end.restart", "/bingo start");
        Set<BingoPlayer> players = getTeamManager().getParticipants();
        players.forEach(p -> {
            if (p.isInBingoWorld(worldName))
                Message.sendDebug(commandMessage, p.player());
        });
        timer.getTimeDisplayMessage().sendAll();
        timer.stop();
        RecoveryCardData.markCardEnded(true);
        players.forEach(p -> p.takeEffects());
        settings = null;
    }

    public void bingo(BingoTeam team)
    {
        new Message("game.end.bingo").arg(team.getColoredName().asLegacyString()).sendAll();
        for (BingoPlayer p : getTeamManager().getParticipants())
        {
            p.player().playSound(p.player(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
            p.player().playSound(p.player(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.75f, 1.0f);

            if (getTeamManager().getTeamOfPlayer(p).equals(team))
            {
                BingoStatsData.incrementPlayerStat(p, BingoStatType.WINS);
            }
            else
            {
                BingoStatsData.incrementPlayerStat(p, BingoStatType.LOSSES);
            }
        }
        end();
    }

    public long getGameTime()
    {
        if (timer != null)
        {
            return timer.getTime();
        }
        return 0;
    }

    public BingoSettings getSettings()
    {
        return settings;
    }

    public TeamManager getTeamManager()
    {
        return scoreboard.getTeamManager();
    }

    public void returnCardToPlayer(BingoPlayer participant)
    {
        if (!inProgress || !participant.isInBingoWorld(getWorldName()))
            return;

        participant.giveBingoCard();

        Bukkit.getScheduler().runTaskLater(BingoReloaded.get(), task -> {
            participant.giveEffects(settings.effects);
        }, BingoReloaded.ONE_SECOND);
    }

    public void startDeathMatch(int countdown)
    {
        if (countdown == 0)
        {
            settings.deathMatchItem = settings.generateDeathMatchItem();

            for (BingoPlayer p : getTeamManager().getParticipants())
            {
                p.showDeathMatchItem(settings.deathMatchItem);
                p.player().sendTitle("" + ChatColor.GOLD + ChatColor.GOLD + "GO", "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + "find the item listed in the chat to win!", -1, -1, -1);
            }
            return;
        }

        ChatColor color = switch (countdown)
                {
                    case 1 -> ChatColor.RED;
                    case 2 -> ChatColor.GOLD;
                    default -> ChatColor.GREEN;
                };
        for (BingoPlayer p : getTeamManager().getParticipants())
        {
            p.player().sendTitle(color + "" + countdown, "", -1, -1, -1);
            Message.sendDebug(color + "" + countdown, p.player());
        }

        Bukkit.getScheduler().runTaskLater(BingoReloaded.get(), task -> {
            startDeathMatch(countdown - 1);
        }, BingoReloaded.ONE_SECOND);
        return;
    }

    public void givePlayersEffects()
    {
        Set<BingoPlayer> players = getTeamManager().getParticipants();
        players.forEach((p) -> {
            p.giveEffects(settings.effects);
            p.player().setGameMode(GameMode.SURVIVAL);
        });
    }

    public void teleportPlayerAfterDeath(Player player)
    {
        if (player == null) return;
        Location location = deadPlayers.get(player.getUniqueId());
        if (location == null)
        {
            new Message("menu.effects.disabled").color(ChatColor.RED).send(player);
            return;
        }

        player.teleport(deadPlayers.get(player.getUniqueId()), PlayerTeleportEvent.TeleportCause.PLUGIN);
        deadPlayers.remove(player.getUniqueId());
    }

    public static void spawnPlatform(Location platformLocation, int size)
    {
        for (int x = -size; x < size + 1; x++)
        {
            for (int z = -size; z < size + 1; z++)
            {
                if (!platformLocation.getWorld().getType(
                     (int)platformLocation.getX() + x,
                    (int)platformLocation.getY(),
                    (int)platformLocation.getZ() + z).isSolid())
                {
                    platformLocation.getWorld().setType(
                            (int)platformLocation.getX() + x,
                            (int)platformLocation.getY(),
                            (int)platformLocation.getZ() + z,
                            Material.WHITE_STAINED_GLASS);
                }
            }
        }
    }

    public static void removePlatform(Location platformLocation, int size)
    {
        for (int x = -size; x < size + 1; x++)
        {
            for (int z = -size; z < size + 1; z++)
            {
                if (platformLocation.getWorld().getType(
                        (int)platformLocation.getX() + x,
                        (int)platformLocation.getY(),
                        (int)platformLocation.getZ() + z) == Material.WHITE_STAINED_GLASS)
                {
                    platformLocation.getWorld().setType(
                            (int)platformLocation.getX() + x,
                            (int)platformLocation.getY(),
                            (int)platformLocation.getZ() + z,
                            Material.AIR);
                }
            }
        }
    }

    private void teleportPlayersToStart(World world)
    {
        switch (ConfigData.instance.playerTeleportStrategy)
        {
            case ALONE:
                for (BingoPlayer p : getTeamManager().getParticipants())
                {
                    Location platformLocation = getRandomSpawnLocation(world);

                    Location playerLocation = platformLocation.clone();
                    playerLocation.setY(playerLocation.getY() + 10.0);
                    p.player().teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    p.player().setBedSpawnLocation(platformLocation, true);

                    if (getTeamManager().getParticipants().size() > 0)
                    {
                        spawnPlatform(platformLocation.clone(), 5);

                        Bukkit.getScheduler().runTaskLater(BingoReloaded.get(), task ->
                        {
                            BingoGame.removePlatform(platformLocation, 5);
                        }, (Math.max(0, ConfigData.instance.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                    }
                }
                break;

            case TEAM:
                for (BingoTeam t: getTeamManager().getActiveTeams())
                {
                    Location teamLocation = getRandomSpawnLocation(world);

                    Set<BingoPlayer> players = getTeamManager().getParticipants();
                    players.forEach(p -> {
                        Location playerLocation = teamLocation.clone();
                        playerLocation.setY(playerLocation.getY() + 10.0);
                        p.player().teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        p.player().setBedSpawnLocation(teamLocation, true);
                    });

                    if (getTeamManager().getParticipants().size() > 0)
                    {
                        spawnPlatform(teamLocation, 5);

                        Bukkit.getScheduler().runTaskLater(BingoReloaded.get(), task ->
                        {
                            BingoGame.removePlatform(teamLocation, 5);
                        }, (Math.max(0, ConfigData.instance.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                    }
                }
                break;

            case ALL:
                Location spawnLocation = getRandomSpawnLocation(world);

                Set<BingoPlayer> players = getTeamManager().getParticipants();
                players.forEach(p -> {
                    Location playerLocation = spawnLocation.clone();
                    playerLocation.setY(playerLocation.getY() + 10.0);
                    p.player().teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    p.player().setBedSpawnLocation(spawnLocation, true);
                });

                if (getTeamManager().getParticipants().size() > 0)
                {
                    spawnPlatform(spawnLocation, 5);

                    Bukkit.getScheduler().runTaskLater(BingoReloaded.get(), task ->
                    {
                        BingoGame.removePlatform(spawnLocation, 5);
                    }, (Math.max(0, ConfigData.instance.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                }
                break;
            default:
                return;
        }
    }

    private static Location getRandomSpawnLocation(World world)
    {
        Vector position = Vector.getRandom().multiply(ConfigData.instance.teleportMaxDistance);
        Location location = new Location(world, position.getX(), world.getHighestBlockYAt(position.getBlockX(), position.getBlockZ()), position.getZ());

        //find a not ocean biome to start the game in
        while (isOceanBiome(world.getBiome(location)))
        {
            position = Vector.getRandom().multiply(ConfigData.instance.teleportMaxDistance);
            location = new Location(world, position.getBlockX(), world.getHighestBlockYAt(position.getBlockX(), position.getBlockZ()), position.getBlockZ());
        }

        return location;
    }

    private static boolean isOceanBiome(Biome biome)
    {
        return switch (biome)
                {
                    case OCEAN,
                            DEEP_COLD_OCEAN,
                            COLD_OCEAN,
                            DEEP_OCEAN,
                            FROZEN_OCEAN,
                            DEEP_FROZEN_OCEAN,
                            LUKEWARM_OCEAN,
                            DEEP_LUKEWARM_OCEAN,
                            WARM_OCEAN -> true;
                    default -> false;
                };
    }

    public void playerQuit(BingoPlayer player)
    {
        if (!getTeamManager().getParticipants().contains(player)) return;

        getTeamManager().removePlayerFromAllTeams(player);
        new Message("game.player.leave").arg(ChatColor.RED + "/bingo join").send(player.player());

        player.takeEffects();
        if (deadPlayers.containsKey(player.playerId()))
        {
            deadPlayers.remove(player.playerId());
        }
    }

    public String getWorldName()
    {
        return worldName;
    }

    public boolean isInProgress()
    {
        return inProgress;
    }

// @EventHandlers ========================================================================

    @EventHandler
    public void onCardSlotCompleteEvent(final BingoCardTaskCompleteEvent event)
    {
        if (!getWorldName().equals(event.getWorldName()))
            return;

        BingoStatsData.incrementPlayerStat(event.getPlayer(), BingoStatType.TASKS);
        for (BingoPlayer p : getTeamManager().getParticipants())
        {
            p.player().playSound(p.player(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
        }
        if (event.hasBingo())
        {
            bingo(event.getPlayer().team());
        }
        scoreboard.updateItemCount();
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent dropEvent)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(dropEvent.getPlayer());
        if (player == null || !inProgress)
            return;

        if (dropEvent.getItemDrop().getItemStack().equals(settings.kit.cardItem.getAsStack()) ||
                dropEvent.getItemDrop().getItemStack().equals(PlayerKit.wandItem.getAsStack()))
        {
            dropEvent.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onItemInteract(final PlayerInteractEvent event)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || !inProgress)
            return;

        if (event.getItem() == null || event.getHand() != EquipmentSlot.HAND || event.getItem().getType().isAir())
            return;

        if (event.getItem().equals(settings.kit.cardItem.getAsStack()))
        {
            event.setCancelled(true);
            BingoTeam playerTeam = getTeamManager().getTeamOfPlayer(player);
            if (playerTeam == null)
            {
                return;
            }

            BingoCard card = playerTeam.card;

            // if the player is actually participating, show it
            if (card != null)
            {
                if (settings.deathMatchItem != null)
                {
                    player.showDeathMatchItem(settings.deathMatchItem);
                    return;
                }
                card.showInventory(event.getPlayer());
            }
            else
            {
                new Message("game.player.no_start").send(event.getPlayer());
            }
        }

        if (event.getItem().equals(PlayerKit.wandItem.getAsStack())
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
        {
            if (!inProgress)
            {
                new Message("game.player.no_start").send(event.getPlayer());
            }
            else if (player.useGoUpWand(event.getItem()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player p))
            return;

        BingoPlayer player = getTeamManager().getBingoPlayer(p);
        if (player == null || !inProgress)
            return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        if (item.equals(PlayerKit.cardItem.getAsStack()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player p))
            return;

        BingoPlayer player = getTeamManager().getBingoPlayer(p);
        if (player == null || !inProgress)
            return;

        if (event.getCursor() == null) return;

        if (event.getCursor().equals(PlayerKit.cardItem.getAsStack()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player p))
            return;

        BingoPlayer player = getTeamManager().getBingoPlayer(p);
        if (player == null || !inProgress)
            return;

        if (!getTeamManager().getParticipants().contains(player))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        if (inProgress && settings.effects.contains(EffectOptionFlags.NO_FALL_DAMAGE))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || !inProgress)
            return;

        Player onlinePlayer = player.player();

        if (inProgress)
        {
            if (getTeamManager().getParticipants().contains(onlinePlayer))
            {
                new Message("game.player.join_back").send(onlinePlayer);
                scoreboard.updateItemCount();
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event)
    {
        if (!getWorldName().equals(GameWorldManager.getWorldName(event.getEntity().getWorld())))
            return;

        if (inProgress)
        {
            if (getTeamManager().getParticipants().contains(event.getEntity()))
            {
                while (event.getDrops().contains(settings.kit.cardItem.getAsStack()))
                    event.getDrops().remove(settings.kit.cardItem.getAsStack());

                Location deathCoords = event.getEntity().getLocation();
                if (ConfigData.instance.teleportAfterDeath)
                {
                    TextComponent[] teleportMsg = Message.createHoverCommandMessage("game.player.respawn", "/bingo back");

                    event.getEntity().spigot().sendMessage(teleportMsg);
                    deadPlayers.put(event.getEntity().getUniqueId(), deathCoords);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerHoldsCard(final PlayerItemHeldEvent event)
    {
        if (!getWorldName().equals(GameWorldManager.getWorldName(event.getPlayer().getWorld())))
            return;

        if (getTeamManager().getParticipants().contains(event.getPlayer()) && inProgress)
        {
            if (event.getNewSlot() == settings.kit.cardItem.getSlot() && settings.effects.contains(EffectOptionFlags.CARD_SPEED))
            {
                event.getPlayer().addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, 100000, 1, false, false));
            }

            if (event.getPreviousSlot() == settings.kit.cardItem.getSlot())
            {
                event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(final PlayerRespawnEvent event)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || !player.isInBingoWorld(getWorldName()))
            return;

        if (getTeamManager().getTeamOfPlayer(player) == null)
        {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

        if (deadPlayers.containsKey(player.playerId()))
        {
            returnCardToPlayer(player);
        }
    }
}