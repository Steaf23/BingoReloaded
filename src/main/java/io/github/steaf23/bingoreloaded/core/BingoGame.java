package io.github.steaf23.bingoreloaded.core;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.core.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.core.data.BingoStatType;
import io.github.steaf23.bingoreloaded.core.data.BingoStatsData;
import io.github.steaf23.bingoreloaded.core.data.ConfigData;
import io.github.steaf23.bingoreloaded.core.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.core.event.BingoEndedEvent;
import io.github.steaf23.bingoreloaded.core.event.BingoStartedEvent;
import io.github.steaf23.bingoreloaded.core.event.CountdownTimerFinishedEvent;
import io.github.steaf23.bingoreloaded.core.event.CardEventManager;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.core.cards.BingoCard;
import io.github.steaf23.bingoreloaded.core.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.core.tasks.statistics.StatisticTracker;
import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.core.player.BingoTeam;
import io.github.steaf23.bingoreloaded.core.player.PlayerKit;
import io.github.steaf23.bingoreloaded.core.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.*;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.util.Vector;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class BingoGame
{
    private boolean inProgress;
    public GameTimer timer;

    private final String worldName;
    private BingoSettings settings;
    private final BingoScoreboard scoreboard;
    private final Map<UUID, Location> deadPlayers;
    private final CardEventManager cardEventManager;
    private final StatisticTracker statTracker;

    public BingoGame(String worldName)
    {
        this.worldName = worldName;
        this.inProgress = false;
        this.scoreboard = new BingoScoreboard(worldName);
        this.deadPlayers = new HashMap<>();
        this.cardEventManager = new CardEventManager(worldName);
        this.statTracker = new StatisticTracker(worldName);
    }

    public void start(BingoSettings settings)
    {
        this.settings = settings;

        if (settings.enableCountdown)
        {
            timer = new CountdownTimer(settings.countdownGameDuration * 60, 5 * 60, 60, getWorldName());
        }
        else
        {
            timer = new CounterTimer(getWorldName());
        }
        timer.setNotifier(time ->
        {
            for (BingoPlayer player : getTeamManager().getParticipants())
            {
                var p = player.gamePlayer();
                p.ifPresent(value -> Message.sendActionMessage(timer.getTimeDisplayMessage(), value));
                statTracker.updateProgress();
            }
        });

        if (!BingoCardsData.getCardNames().contains(settings.card))
        {
            new Message("game.start.no_card").color(ChatColor.RED).arg(settings.card).sendAll(worldName);
            return;
        }

        // Pre-start Setup
        if (getTeamManager().getParticipants().size() == 0)
        {
            Message.log("Could not start bingo since no players have joined!", worldName);
            return;
        }
        if (inProgress)
        {
            Message.log("Could not start bingo here because it already started!", worldName);
            return;
        }

        settings.deathMatchItem = null;
        getTeamManager().updateActivePlayers();
        World world = Bukkit.getWorld(getWorldName());
        if (world == null)
        {
            return;
        }
        world.setStorm(false);
        world.setTime(1000);

        // Start
        inProgress = true;

        BingoCard masterCard = CardBuilder.fromMode(settings.mode, settings.cardSize, getTeamManager().getActiveTeams().size());
        masterCard.generateCard(settings.card, ConfigData.instance.cardSeed);
        getTeamManager().initializeCards(masterCard);

        Set<BingoCard> cards = new HashSet<>();
        for (BingoTeam activeTeam : getTeamManager().getActiveTeams())
        {
            cards.add(activeTeam.card);
        }
        cardEventManager.setCards(cards.stream().toList());

        statTracker.start(getTeamManager().getActiveTeams());

        new Message("game.start.give_cards").sendAll(worldName);
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
        scoreboard.updateTeamScores();
        timer.start();

        var event = new BingoStartedEvent(worldName);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void resume()
    {
        inProgress = true;
        scoreboard.updateTeamScores();
    }

    public void end()
    {
        if (settings != null)
            settings.deathMatchItem = null;

        if(!inProgress)
            return;

        inProgress = false;

        statTracker.reset();
        timer.getTimeDisplayMessage().sendAll(worldName);
        timer.stop();
        settings = null;

        if (!ConfigData.instance.keepScoreboardVisible)
        {
            scoreboard.reset();
        }

        TextComponent[] commandMessage = Message.createHoverCommandMessage("game.end.restart", "/bingo start");
        Set<BingoPlayer> players = getTeamManager().getParticipants();
        players.forEach(p -> {
            p.takeEffects(false);
            p.gamePlayer().ifPresent(player -> {
                player.playSound(player, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 1.0f);
                Message.sendDebug(commandMessage, player);
            });
        });

        String command = ConfigData.instance.sendCommandAfterGameEnded;
        if (!command.equals(""))
        {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public void bingo(BingoTeam team)
    {
        new Message("game.end.bingo").arg(team.getColoredName().asLegacyString()).sendAll(worldName);
        for (BingoPlayer p : getTeamManager().getParticipants())
        {
            if (p.gamePlayer().isEmpty())
                continue;

            Player player = p.gamePlayer().get();
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

        BingoReloaded.scheduleTask(task -> participant.giveEffects(settings.effects), BingoReloaded.ONE_SECOND);
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
            Message.sendDebug(color + "" + countdown, p.gamePlayer().get());
        }

        BingoReloaded.scheduleTask(task -> {
            startDeathMatch(countdown - 1);
        }, BingoReloaded.ONE_SECOND);
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
            case ALONE ->
            {
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
                        }, (long) (Math.max(0, ConfigData.instance.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                    }
                }
            }
            case TEAM ->
            {
                for (BingoTeam t : getTeamManager().getActiveTeams())
                {
                    Location teamLocation = getRandomSpawnLocation(world);

                    Set<BingoPlayer> players = t.getPlayers();
                    players.forEach(p -> teleportPlayerToStart(p, teamLocation));

                    if (t.players.size() > 0)
                    {
                        spawnPlatform(teamLocation, 5);

                        BingoReloaded.scheduleTask(task ->
                        {
                            BingoGame.removePlatform(teamLocation, 5);
                        }, (long) (Math.max(0, ConfigData.instance.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                    }
                }
            }
            case ALL ->
            {
                Location spawnLocation = getRandomSpawnLocation(world);
                Set<BingoPlayer> players = getTeamManager().getParticipants();
                players.forEach(p -> teleportPlayerToStart(p, spawnLocation));
                if (getTeamManager().getParticipants().size() > 0)
                {
                    spawnPlatform(spawnLocation, 5);

                    BingoReloaded.scheduleTask(task ->
                    {
                        BingoGame.removePlatform(spawnLocation, 5);
                    }, (long) (Math.max(0, ConfigData.instance.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                }
            }
            default ->
            {
            }
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
        player.setBedSpawnLocation(to.clone().add(0.0, 2.0, 0.0), true);
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

        deadPlayers.remove(player.playerId());
    }

    public String getWorldName()
    {
        return worldName;
    }

    public boolean isInProgress()
    {
        return inProgress;
    }

    public BingoScoreboard getScoreboard()
    {
        return scoreboard;
    }

    public CardEventManager getCardEventManager()
    {
        return cardEventManager;
    }

    public StatisticTracker getStatisticTracker()
    {
        return statTracker;
    }

// @EventHandlers ========================================================================

    public void handleBingoTaskComplete(final BingoCardTaskCompleteEvent event)
    {
        if (event.getPlayer().gamePlayer().isEmpty())
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
        scoreboard.updateTeamScores();
    }

    public void handlePlayerDropItem(final PlayerDropItemEvent dropEvent)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(dropEvent.getPlayer());
        if (player == null || player.gamePlayer().isEmpty() || !inProgress)
            return;

        if (PlayerKit.cardItem.isKeyEqual(dropEvent.getItemDrop().getItemStack()) ||
                PlayerKit.wandItem.isKeyEqual(dropEvent.getItemDrop().getItemStack()))
        {
            dropEvent.setCancelled(true);
        }
    }

    public void handlePlayerInteract(final PlayerInteractEvent event)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || player.gamePlayer().isEmpty() || !inProgress)
            return;

        if (event.getItem() == null || event.getItem().getType().isAir())
            return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (PlayerKit.cardItem.isKeyEqual(event.getItem()))
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

        if (PlayerKit.wandItem.isKeyEqual(event.getItem()))
        {
            event.setCancelled(true);
            if (!inProgress)
            {
                new Message("game.player.no_start").send(event.getPlayer());
            }
            player.useGoUpWand(event.getItem());
        }
    }

    public void handleEntityDamage(final EntityDamageEvent event)
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

    public void handlePlayerDeath(final PlayerDeathEvent event)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(event.getEntity());
        if (player == null || player.gamePlayer().isEmpty())
            return;

        if (inProgress)
        {
            for (ItemStack drop : event.getDrops())
            {
                if (PDCHelper.getBoolean(drop.getItemMeta().getPersistentDataContainer(), "kit.kit_item", false)
                        || PlayerKit.cardItem.isKeyEqual(drop))
                {
                    drop.setAmount(0);
                }
            }

            Location deathCoords = event.getEntity().getLocation();
            if (ConfigData.instance.teleportAfterDeath)
            {
                TextComponent[] teleportMsg = Message.createHoverCommandMessage("game.player.respawn", "/bingo back");

                event.getEntity().spigot().sendMessage(teleportMsg);
                deadPlayers.put(player.playerId(), deathCoords);
            }
        }
    }

    public void handlePlayerRespawn(final PlayerRespawnEvent event)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || player.gamePlayer().isEmpty())
            return;

        if (!BingoGameManager.get().isGameWorldActive(event.getPlayer().getWorld()))
            return;

        Message.log("Player " + player.asOnlinePlayer().get().getDisplayName() + " respawned", worldName);

        returnCardToPlayer(player);

        if (ConfigData.instance.teleportAfterDeath)
        {
            if (deadPlayers.containsKey(player.playerId()))
            {
                player.giveKit(settings.kit);
            }
        }
        else
        {
            player.giveKit(settings.kit);
        }
    }

    public void handleCountdownFinished(final CountdownTimerFinishedEvent event)
    {
        if (!inProgress)
            return;

        Set<BingoTeam> tiedTeams = new HashSet<>();
        tiedTeams.add(getTeamManager().getLeadingTeam());

        // Regular bingo cannot draw, so end the game without a winner
        if (settings.mode == BingoGamemode.REGULAR)
        {
            var endedEvent = new BingoEndedEvent(getGameTime(), null, getWorldName());
            Bukkit.getPluginManager().callEvent(endedEvent);
            end();
            return;
        }

        int leadingPoints = getTeamManager().getCompleteCount(getTeamManager().getLeadingTeam());
        for (BingoTeam team : getTeamManager().getActiveTeams())
        {
            if (getTeamManager().getCompleteCount(team) == leadingPoints)
            {
                tiedTeams.add(team);
            }
            else
            {
                team.outOfTheGame = true;
            }
        }

        // If only 1 team is "tied" for first place, make that team win the game
        if (tiedTeams.size() == 1)
        {
            bingo(getTeamManager().getLeadingTeam());
        }
        else
        {
            startDeathMatch(3);
        }
    }
}