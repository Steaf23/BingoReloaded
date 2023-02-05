package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.event.BingoEndedEvent;
import io.github.steaf23.bingoreloaded.event.BingoStartedEvent;
import io.github.steaf23.bingoreloaded.event.UpdateStatisticEvent;
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
import org.bukkit.command.ConsoleCommandSender;
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
                    var p = player.gamePlayer();
                    if (p.isPresent())
                        BingoMessage.sendActionMessage(timer.getTimeDisplayMessage(), p.get());
                }
                updateStatisticTasks();
            }
        };
        BingoReloaded.registerListener(this);
    }

    public void start(BingoSettings settings)
    {
        var event = new BingoStartedEvent(worldName);
        Bukkit.getPluginManager().callEvent(event);
        
        BingoReloaded.scheduleTask(task -> {
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
                new BingoMessage("game.start.no_card").color(ChatColor.RED).arg(settings.card).sendAll(worldName);
                return;
            }

            // Pre-start Setup
            if (getTeamManager().getParticipants().size() == 0)
            {
                new BingoMessage("game.start.no_players").color(ChatColor.RED).sendAll(worldName);
                return;
            }
            if (inProgress)
            {
                new BingoMessage("game.start.already_started").color(ChatColor.RED).sendAll(worldName);
                return;
            }

            settings.deathMatchItem = null;
            getTeamManager().updateActivePlayers();
            World world = Bukkit.getWorld(getWorldName());
            world.setStorm(false);
            world.setTime(1000);

            // Start
            inProgress = true;

            BingoCard masterCard = CardBuilder.fromMode(settings.mode, settings.cardSize, getTeamManager().getActiveTeams().size());
            masterCard.generateCard(settings.card, ConfigData.instance.cardSeed);
            getTeamManager().initializeCards(masterCard);

            new BingoMessage("game.start.give_cards").sendAll(worldName);
            Set<BingoPlayer> players = getTeamManager().getParticipants();
            teleportPlayersToStart(world);
            players.forEach(p ->
            {
                if (p.gamePlayer().isPresent())
                {
                    Player player = p.gamePlayer().get();

                    p.giveKit(settings.kit);
                    returnCardToPlayer(p);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + player.getName() + " everything");
                    player.setLevel(0);
                    player.setExp(0.0f);
                }
            });

            // Post-start Setup
            scoreboard.reset();
            scoreboard.updateItemCount();
            timer.start();
        });
    }

    public void resume()
    {
        inProgress = true;
        scoreboard.updateItemCount();
    }

    public void end()
    {
        BingoReloaded.scheduleTask(task -> {
            if (settings != null)
                settings.deathMatchItem = null;
            if(!inProgress)
                return;

            inProgress = false;
            TextComponent[] commandMessage = BingoMessage.createHoverCommandMessage("game.end.restart", "/bingo start");
            Set<BingoPlayer> players = getTeamManager().getParticipants();
            players.forEach(p -> {
                p.takeEffects(false);
                if (p.gamePlayer().isPresent())
                    BingoMessage.sendDebug(commandMessage, p.gamePlayer().get());
            });
            timer.getTimeDisplayMessage().sendAll(worldName);
            timer.stop();
            RecoveryCardData.markCardEnded(true);
            settings = null;

            if (!ConfigData.instance.keepScoreboardVisible)
            {
                scoreboard.reset();
            }

            String command = ConfigData.instance.sendCommandAfterGameEnded;
            if (!command.equals(""))
            {
                Message.log(command);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        });
    }

    public void bingo(BingoTeam team)
    {
        new BingoMessage("game.end.bingo").arg(team.getColoredName().asLegacyString()).sendAll(worldName);
        for (BingoPlayer p : getTeamManager().getParticipants())
        {
            if (p.gamePlayer().isEmpty())
                continue;
            Player player = p.gamePlayer().get();
            p.gamePlayer().get().playSound(player, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
            p.gamePlayer().get().playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.75f, 1.0f);

            if (getTeamManager().getTeamOfPlayer(p).equals(team))
            {
                BingoStatsData.incrementPlayerStat(player, BingoStatType.WINS);
            }
            else
            {
                BingoStatsData.incrementPlayerStat(player, BingoStatType.LOSSES);
            }
        }
        var event = new BingoEndedEvent(timer.getTime(), team, worldName);
        Bukkit.getPluginManager().callEvent(event);
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
        if (!inProgress || participant.gamePlayer().isEmpty())
            return;

        participant.giveBingoCard();
        participant.gamePlayer().get().setGameMode(GameMode.SURVIVAL);

        BingoReloaded.scheduleTask(task -> {
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
                if (p.gamePlayer().isEmpty())
                    continue;

                p.showDeathMatchItem(settings.deathMatchItem);
                p.gamePlayer().get().sendTitle("" + ChatColor.GOLD + ChatColor.GOLD + "GO", "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + "find the item listed in the chat to win!", -1, -1, -1);
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
            if (p.gamePlayer().isEmpty())
                continue;

            p.gamePlayer().get().sendTitle(color + "" + countdown, "", -1, -1, -1);
            BingoMessage.sendDebug(color + "" + countdown, p.gamePlayer().get());
        }

        BingoReloaded.scheduleTask(task -> {
            startDeathMatch(countdown - 1);
        }, BingoReloaded.ONE_SECOND);
        return;
    }

    public void teleportPlayerAfterDeath(Player player)
    {
        if (player == null) return;
        Location location = deadPlayers.get(player.getUniqueId());
        if (location == null)
        {
            new BingoMessage("menu.effects.disabled").color(ChatColor.RED).send(player);
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
                    teleportPlayerToStart(p, platformLocation);

                    if (getTeamManager().getParticipants().size() > 0)
                    {
                        spawnPlatform(platformLocation.clone(), 5);

                        BingoReloaded.scheduleTask(task ->
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
                    players.forEach(p -> teleportPlayerToStart(p, teamLocation));

                    if (getTeamManager().getParticipants().size() > 0)
                    {
                        spawnPlatform(teamLocation, 5);

                        BingoReloaded.scheduleTask(task ->
                        {
                            BingoGame.removePlatform(teamLocation, 5);
                        }, (Math.max(0, ConfigData.instance.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                    }
                }
                break;

            case ALL:
                Location spawnLocation = getRandomSpawnLocation(world);

                Set<BingoPlayer> players = getTeamManager().getParticipants();
                players.forEach(p -> teleportPlayerToStart(p, spawnLocation));

                if (getTeamManager().getParticipants().size() > 0)
                {
                    spawnPlatform(spawnLocation, 5);

                    BingoReloaded.scheduleTask(task ->
                    {
                        BingoGame.removePlatform(spawnLocation, 5);
                    }, (Math.max(0, ConfigData.instance.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                }
                break;
            default:
                return;
        }
    }

    private static void teleportPlayerToStart(BingoPlayer bingoPlayer, Location to)
    {
        if (bingoPlayer.gamePlayer().isEmpty())
            return;
        Player player = bingoPlayer.gamePlayer().get();

        Location playerLocation = to.clone();
        playerLocation.setY(playerLocation.getY() + 10.0);
        player.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setBedSpawnLocation(to, true);
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

        getTeamManager().removePlayerFromTeam(player);

        if (player.offline().isOnline())
        {
            player.takeEffects(true);
        }

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

    public void updateStatisticTasks()
    {
        var event = new UpdateStatisticEvent(worldName);
        Bukkit.getPluginManager().callEvent(event);
    }

// @EventHandlers ========================================================================

    @EventHandler
    public void onCardSlotCompleteEvent(final BingoCardTaskCompleteEvent event)
    {
        if (!getWorldName().equals(event.worldName) || event.getPlayer().gamePlayer().isEmpty())
            return;

        Player player = event.getPlayer().gamePlayer().get();
        BingoStatsData.incrementPlayerStat(player, BingoStatType.TASKS);
        for (BingoPlayer otherPlayer : getTeamManager().getParticipants())
        {
            if (otherPlayer.gamePlayer().isPresent())
                otherPlayer.gamePlayer().get().playSound(otherPlayer.gamePlayer().get(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 0.8f, 1.0f);
        }
        if (event.hasBingo())
        {
            bingo(event.getPlayer().getTeam());
        }
        scoreboard.updateItemCount();
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent dropEvent)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(dropEvent.getPlayer());
        if (player == null || player.gamePlayer().isEmpty() || !inProgress)
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
        if (player == null || player.gamePlayer().isEmpty() || !inProgress)
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
                new BingoMessage("game.player.no_start").send(event.getPlayer());
            }
        }

        if (event.getItem().equals(PlayerKit.wandItem.getAsStack())
                && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
        {
            if (!inProgress)
            {
                new BingoMessage("game.player.no_start").send(event.getPlayer());
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
        if (player == null || player.gamePlayer().isEmpty() || !inProgress)
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
        if (player == null || player.gamePlayer().isEmpty() || !inProgress)
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
        if (player == null || player.gamePlayer().isEmpty() || !inProgress)
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
    public void onPlayerDeath(final PlayerDeathEvent event)
    {
        if (!getWorldName().equals(GameWorldManager.getWorldName(event.getEntity().getWorld())))
            return;

        BingoPlayer player = getTeamManager().getBingoPlayer(event.getEntity());
        if (player == null || player.gamePlayer().isEmpty())
            return;

        if (inProgress)
        {
            while (event.getDrops().contains(settings.kit.cardItem.getAsStack()))
                event.getDrops().remove(settings.kit.cardItem.getAsStack());

            Location deathCoords = event.getEntity().getLocation();
            if (ConfigData.instance.teleportAfterDeath)
            {
                TextComponent[] teleportMsg = BingoMessage.createHoverCommandMessage("game.player.respawn", "/bingo back");

                event.getEntity().spigot().sendMessage(teleportMsg);
                deadPlayers.put(player.playerId(), deathCoords);
            }
        }
    }

    @EventHandler
    public void onPlayerHoldsCard(final PlayerItemHeldEvent event)
    {
        if (!getWorldName().equals(GameWorldManager.getWorldName(event.getPlayer().getWorld())))
            return;

        if (!inProgress)
            return;

        BingoPlayer player = getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || player.gamePlayer().isEmpty())
        {
            return;
        }

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

    @EventHandler
    public void onPlayerRespawnEvent(final PlayerRespawnEvent event)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(event.getPlayer());
        Message.log(" NO PLAYER");
        if (player == null || player.gamePlayer().isEmpty())
            return;
        Message.log("YES PLAYER");

        if (deadPlayers.containsKey(player.playerId()))
        {
            Message.log("HDJA");
            returnCardToPlayer(player);
            player.giveKit(settings.kit);
        }
    }
}