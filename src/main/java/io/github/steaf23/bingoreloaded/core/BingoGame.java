package io.github.steaf23.bingoreloaded.core;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.cards.BingoCard;
import io.github.steaf23.bingoreloaded.core.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.core.data.BingoStatType;
import io.github.steaf23.bingoreloaded.core.data.ConfigData;
import io.github.steaf23.bingoreloaded.core.data.TranslationData;
import io.github.steaf23.bingoreloaded.core.event.*;
import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.core.player.BingoTeam;
import io.github.steaf23.bingoreloaded.core.player.PlayerKit;
import io.github.steaf23.bingoreloaded.core.player.TeamManager;
import io.github.steaf23.bingoreloaded.core.tasks.statistics.StatisticTracker;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.item.ItemText;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.PDCHelper;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
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
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.*;

public class BingoGame
{
    private final BingoSession session;

    private final String worldName;
    private BingoSettings settings;
    private final BingoScoreboard scoreboard;
    private final TeamManager teamManager;
    private final Map<UUID, Location> deadPlayers;
    private final CardEventManager cardEventManager;
    private final StatisticTracker statTracker;
    private GameTimer timer;

    public BingoGame(BingoSession session) {
        this.session = session;
        this.worldName = session.worldName;
        this.teamManager = session.teamManager;
        this.scoreboard = session.scoreboard;
        this.settings = session.settings;
        this.deadPlayers = new HashMap<>();
        this.cardEventManager = new CardEventManager(worldName);
        if (BingoReloaded.get().config().useStatistics)
            this.statTracker = new StatisticTracker(worldName);
        else
            this.statTracker = null;

        start();
    }

    private void start()
    {
        // Create timer
        if (settings.enableCountdown)
            timer = new CountdownTimer(settings.countdownGameDuration * 60, 5 * 60, 60, session);
        else
            timer = new CounterTimer();
        timer.setNotifier(time ->
        {
            Message timerMessage = timer.getTimeDisplayMessage();
            for (BingoPlayer player : getTeamManager().getParticipants())
            {
                var p = player.gamePlayer();
                p.ifPresent(value -> Message.sendActionMessage(timerMessage, value));
            }
            if (statTracker != null)
                statTracker.updateProgress();
        });

        settings.deathMatchItem = null;
        World world = Bukkit.getWorld(getWorldName());
        if (world == null)
        {
            return;
        }
        world.setStorm(false);
        world.setTime(1000);

        // Generate cards
        BingoCard masterCard = CardBuilder.fromMode(settings.mode, settings.cardSize, getTeamManager().getActiveTeams().size());
        masterCard.generateCard(settings.card, BingoReloaded.get().config().cardSeed);
        getTeamManager().initializeCards(masterCard);

        Set<BingoCard> cards = new HashSet<>();
        for (BingoTeam activeTeam : getTeamManager().getActiveTeams())
        {
            cards.add(activeTeam.card);
        }
        cardEventManager.setCards(cards.stream().toList());

        if (statTracker != null)
            statTracker.start(getTeamManager().getActiveTeams());

        new TranslatedMessage("game.start.give_cards").sendAll(session);
        teleportPlayersToStart(world);
        getTeamManager().getParticipants().forEach(p ->
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
        timer.start();

        var event = new BingoStartedEvent(session);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void end(@Nullable BingoTeam winningTeam)
    {
        if (statTracker != null)
            statTracker.reset();
        timer.getTimeDisplayMessage().sendAll(session);
        timer.stop();

        if (!BingoReloaded.get().config().keepScoreboardVisible)
        {
            scoreboard.reset();
        }

        getTeamManager().getParticipants().forEach(p -> {
            p.takeEffects(false);
            p.gamePlayer().ifPresent(player -> {
                player.playSound(player, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 1.0f);
            });
        });

        String command = BingoReloaded.get().config().sendCommandAfterGameEnded;
        if (!command.equals(""))
        {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        var event = new BingoEndedEvent(getGameTime(), winningTeam, session);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void bingo(BingoTeam team)
    {
        new TranslatedMessage("game.end.bingo").arg(team.getColoredName().asLegacyString()).sendAll(session);
        for (BingoPlayer p : getTeamManager().getParticipants())
        {
            if (p.gamePlayer().isEmpty())
                continue;

            Player player = p.gamePlayer().get();
            p.gamePlayer().get().playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.75f, 1.0f);

            if (getTeamManager().getTeamOfPlayer(p).equals(team))
            {
                BingoReloaded.incrementPlayerStat(player, BingoStatType.WINS);
            }
            else
            {
                BingoReloaded.incrementPlayerStat(player, BingoStatType.LOSSES);
            }
        }
        end(team);
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
        return teamManager;
    }

    public void returnCardToPlayer(BingoPlayer participant)
    {
        if (participant.gamePlayer().isEmpty())
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
            new TranslatedMessage("menu.effects.disabled").color(ChatColor.RED).send(player);
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
        ConfigData config = BingoReloaded.get().config();
        switch (config.playerTeleportStrategy)
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
                        }, (long) (Math.max(0, config.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
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
                        }, (long) (Math.max(0, config.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
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
                    }, (long) (Math.max(0, config.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
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
        ConfigData config = BingoReloaded.get().config();
        Vector position = Vector.getRandom().multiply(config.teleportMaxDistance);
        Location location = new Location(world, position.getX(), world.getHighestBlockYAt(position.getBlockX(), position.getBlockZ()), position.getZ());

        //find a not ocean biome to start the game in
        while (isOceanBiome(world.getBiome(location)))
        {
            position = Vector.getRandom().multiply(config.teleportMaxDistance);
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
        deadPlayers.remove(player.playerId);
    }

    public String getWorldName()
    {
        return worldName;
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

        String timeString = GameTimer.getTimeAsString(getGameTime());

        TranslationData translator = BingoReloaded.get().getTranslator();
        new TranslatedMessage("game.item.completed").color(ChatColor.AQUA)
                .component(event.getTask().data.getItemDisplayName(translator).asComponent()).color(event.getTask().nameColor)
                .arg(new ItemText(event.getPlayer().gamePlayer().get().getDisplayName(), event.getPlayer().team.getColor().chatColor, ChatColor.BOLD).asLegacyString())
                .arg(timeString).color(ChatColor.WHITE)
                .sendAll(session);

        Player player = event.getPlayer().gamePlayer().get();
        BingoReloaded.incrementPlayerStat(player, BingoStatType.TASKS);
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
        if (player == null || player.gamePlayer().isEmpty())
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
        if (player == null || player.gamePlayer().isEmpty())
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
                new TranslatedMessage("game.player.no_card").send(event.getPlayer());
            }
        }

        if (PlayerKit.wandItem.isKeyEqual(event.getItem()))
        {
            event.setCancelled(true);
            player.useGoUpWand(event.getItem());
        }
    }

    public void handleEntityDamage(final EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player p))
            return;

        BingoPlayer player = getTeamManager().getBingoPlayer(p);
        if (player == null || player.gamePlayer().isEmpty())
            return;

        if (!getTeamManager().getParticipants().contains(player))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        if (settings.effects.contains(EffectOptionFlags.NO_FALL_DAMAGE))
        {
            event.setCancelled(true);
        }
    }

    public void handlePlayerDeath(final PlayerDeathEvent event)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(event.getEntity());
        if (player == null || player.gamePlayer().isEmpty())
            return;

        for (ItemStack drop : event.getDrops())
        {
            if (PDCHelper.getBoolean(drop.getItemMeta().getPersistentDataContainer(), "kit.kit_item", false)
                    || PlayerKit.cardItem.isKeyEqual(drop))
            {
                drop.setAmount(0);
            }
        }

        Location deathCoords = event.getEntity().getLocation();
        if (BingoReloaded.get().config().teleportAfterDeath)
        {
            TextComponent[] teleportMsg = Message.createHoverCommandMessage("game.player.respawn", "/bingo back");

            event.getEntity().spigot().sendMessage(teleportMsg);
            deadPlayers.put(player.playerId, deathCoords);
        }
    }

    public void handlePlayerRespawn(final PlayerRespawnEvent event, final BingoGameManager gameManager)
    {
        BingoPlayer player = getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || player.gamePlayer().isEmpty())
            return;

        if (!gameManager.isGameWorldActive(event.getPlayer().getWorld()))
            return;

        Message.log("Player " + player.asOnlinePlayer().get().getDisplayName() + " respawned", worldName);

        returnCardToPlayer(player);

        if (BingoReloaded.get().config().teleportAfterDeath)
        {
            if (deadPlayers.containsKey(player.playerId))
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
        if (!event.session.game().equals(this))
            return;

        Set<BingoTeam> tiedTeams = new HashSet<>();
        tiedTeams.add(getTeamManager().getLeadingTeam());

        // Regular bingo cannot draw, so end the game without a winner
        if (settings.mode == BingoGamemode.REGULAR)
        {
            end(null);
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