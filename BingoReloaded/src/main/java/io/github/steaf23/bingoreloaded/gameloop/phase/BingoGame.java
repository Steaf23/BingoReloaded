package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.CardBuilder;
import io.github.steaf23.bingoreloaded.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gui.hud.BingoGameHUDGroup;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.player.*;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.tracker.TaskProgressTracker;
import io.github.steaf23.bingoreloaded.util.ActionBarManager;
import io.github.steaf23.bingoreloaded.util.MaterialHelper;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import io.github.steaf23.easymenulib.util.PDCHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public class BingoGame implements GamePhase
{
    private final BingoSession session;
    private final BingoSettings settings;
    private final BingoGameHUDGroup scoreboard;
    private final TeamManager teamManager;
    private final PlayerRespawnManager respawnManager;
    private final TaskProgressTracker progressTracker;
    private final ConfigData config;
    private GameTimer timer;
    private CountdownTimer startingTimer;
    private boolean gameStarted;
    private final ActionBarManager actionBarManager;

    private BingoTask deathMatchTask;

    public BingoGame(BingoSession session, BingoSettings settings, ConfigData config) {
        this.session = session;
        this.config = config;
        this.teamManager = session.teamManager;
        this.scoreboard = session.scoreboard;
        this.settings = settings;
        this.actionBarManager = new ActionBarManager(session);
        this.progressTracker = new TaskProgressTracker(this);

        this.respawnManager = new PlayerRespawnManager(BingoReloaded.getInstance(), config.teleportAfterDeathPeriod);
    }

    private void start() {
        this.gameStarted = false;
        // Create timer
        if (settings.enableCountdown())
            timer = new CountdownTimer(settings.countdownDuration() * 60, 5 * 60, 60, session);
        else
            timer = new CounterTimer();
        timer.addNotifier(time ->
        {
            Message timerMessage = timer.getTimeDisplayMessage(false);
            actionBarManager.requestMessage(timerMessage::asComponent, 0);
            actionBarManager.update();
            getProgressTracker().updateStatisticProgress();
            scoreboard.updateVisible();
        });

        deathMatchTask = null;
        World world = session.getOverworld();
        if (world == null) {
            return;
        }
        world.setStorm(false);
        world.setTime(1000);

        // Generate cards
        boolean useAdvancements = !(BingoReloaded.areAdvancementsDisabled() || config.disableAdvancements);
        BingoCard masterCard = CardBuilder.fromGame(session.getMenuManager(), this);
        masterCard.generateCard(settings.card(), settings.seed(), useAdvancements, !config.disableStatistics);
        if (masterCard instanceof LockoutBingoCard lockoutCard) {
            lockoutCard.teamCount = getTeamManager().getTeamCount();
        }
        Set<BingoCard> uniqueCards = new HashSet<>();
        getTeamManager().getActiveTeams().forEach(t -> {
            t.outOfTheGame = false;
            t.setCard(masterCard.copy());
            uniqueCards.add(t.getCard());
        });

        for (BingoCard card : uniqueCards) {
            card.getTasks().forEach(t -> getProgressTracker().startTrackingTask(t));
        }

        new TranslatedMessage(BingoTranslation.GIVE_CARDS).sendAll(session);
        teleportPlayersToStart(world);

        BaseComponent hoverMessage = new ComponentBuilder()
                .append(BingoTranslation.OPTIONS_GAMEMODE.translate()).append(": ").append(settings.mode().displayName).append(" ").append(settings.size().toString()).append("\n")
                .append(BingoTranslation.OPTIONS_KIT.translate()).append(": ").append(settings.kit().getDisplayName()).append("\n")
                .append(BingoTranslation.OPTIONS_EFFECTS.translate()).append(": ").append(EffectOptionFlags.effectsToString(settings.effects()))
                .append(BingoTranslation.DURATION.translate(settings.enableCountdown() ? GameTimer.getTimeAsString(settings.countdownDuration() * 60) : "∞"))
                .build();
        BaseComponent[] settingsMessage = Message.createHoverCommandMessage(
                new TextComponent(),
                new TextComponent(BingoTranslation.SETTINGS_HOVER.translate()),
                hoverMessage,
                new TextComponent(), null);

        getTeamManager().getParticipants().forEach(p ->
        {
            if (p.sessionPlayer().isPresent()) {
                Player player = p.sessionPlayer().get();

                p.giveKit(settings.kit());
                returnCardToPlayer(settings.kit().getCardSlot(), p);
                player.setLevel(0);
                player.setExp(0.0f);
                scoreboard.addPlayer(player);
                player.spigot().sendMessage(new TextComponent());
                player.spigot().sendMessage(settingsMessage);
                player.spigot().sendMessage(new TextComponent());
            }
            else {
                // If the player is not online, we can remove them from the game, as they probably did not intend on playing in this session
                session.removeParticipant(p);
            }
        });

        // Post-start Setup
        scoreboard.setup();

        var event = new BingoStartedEvent(session);
        Bukkit.getPluginManager().callEvent(event);

        // Countdown before the game actually starts
        startingTimer = new CountdownTimer(Math.max(1, config.startingCountdownTime), 6, 3, session);
        startingTimer.addNotifier(time -> {
            String timeString = GameTimer.getSecondsString(time);
            if (time == 0)
                timeString = "" + ChatColor.RESET + ChatColor.BOLD + ChatColor.GREEN + "GO";

            ChatColor color = ChatColor.WHITE;
            float pitch;
            if (time <= startingTimer.lowThreshold) {
                pitch = 1.414214f;
                color = ChatColor.RED;
            } else if (time <= startingTimer.medThreshold) {
                pitch = 1.059463f;
                color = ChatColor.GOLD;
            } else {
                pitch = 0.890899f;
            }

            Message timeDisplay = new Message(timeString).bold().color(color);
            teamManager.getParticipants().forEach(p ->
                    p.sessionPlayer().ifPresent(player -> {
                        Message.sendTitleMessage(timeDisplay, new Message(), player);
                    }));
            if (time <= startingTimer.lowThreshold && time > 0) {
                var soundEvent = new BingoPlaySoundEvent(session, Sound.BLOCK_NOTE_BLOCK_BIT, 1.2f - time / 10.0f + 0.2f, pitch);
                var soundEvent2 = new BingoPlaySoundEvent(session, Sound.BLOCK_NOTE_BLOCK_PLING, 1.2f - time / 10.0f + 0.2f, pitch);
                Bukkit.getPluginManager().callEvent(soundEvent);
                Bukkit.getPluginManager().callEvent(soundEvent2);
            }
        });
        BingoReloaded.scheduleTask(task -> startingTimer.start(), BingoReloaded.ONE_SECOND);
    }

    public boolean hasStarted() {
        return gameStarted;
    }

    public void end(@Nullable BingoTeam winningTeam) {
        // If the starting timer was still running
        startingTimer.stop();
        timer.getTimeDisplayMessage(false).sendAll(session);
        timer.stop();

        if (!config.keepScoreboardVisible) {
            scoreboard.setup();
        }

        getTeamManager().getParticipants().forEach(p -> p.takeEffects(false));

        var soundEvent = new BingoPlaySoundEvent(session, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
        Bukkit.getPluginManager().callEvent(soundEvent);

        String command = config.sendCommandAfterGameEnded;
        if (!command.isEmpty()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        new Message(" ").sendAll(session);

        var event = new BingoEndedEvent(getGameTime(), winningTeam, session);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void bingo(BingoTeam team) {
        new TranslatedMessage(BingoTranslation.BINGO).arg(team.getColoredName()).sendAll(session);
        for (BingoParticipant p : getTeamManager().getParticipants()) {
            if (p.sessionPlayer().isEmpty())
                continue;

            Player player = p.sessionPlayer().get();

            if (team.equals(p.getTeam())) {
                BingoReloaded.incrementPlayerStat(player, BingoStatType.WINS);
            } else {
                BingoReloaded.incrementPlayerStat(player, BingoStatType.LOSSES);
            }
        }
        var event = new BingoPlaySoundEvent(session, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.75f, 1.0f);
        Bukkit.getPluginManager().callEvent(event);
        end(team);
    }

    public long getGameTime() {
        if (timer != null) {
            return timer.getTime();
        }
        return 0;
    }

    public BingoSettings getSettings() {
        return settings;
    }

    public ConfigData getConfig() {
        return config;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public GameTimer getTimer() {
        return timer;
    }

    public ActionBarManager getActionBar() {
        return actionBarManager;
    }

    public void returnCardToPlayer(int cardSlot, BingoParticipant participant) {
        if (participant.sessionPlayer().isEmpty())
            return;

        participant.giveBingoCard(cardSlot);
        participant.sessionPlayer().get().setGameMode(GameMode.SURVIVAL);

        BingoReloaded.scheduleTask(task -> participant.giveEffects(settings.effects(), config.gracePeriod), BingoReloaded.ONE_SECOND);
    }

    public void startDeathMatch(int seconds) {
        new TranslatedMessage(BingoTranslation.DEATHMATCH_START).sendAll(session);

        var soundEvent = new BingoPlaySoundEvent(session, Sound.ENTITY_PARROT_IMITATE_GHAST);
        Bukkit.getPluginManager().callEvent(soundEvent);

        startDeathMatchRecurse(seconds);
    }

    private void startDeathMatchRecurse(int countdown) {
        if (countdown == 0) {
            deathMatchTask = new BingoTask(new BingoCardData().getRandomItemTask(settings.card()));

            for (BingoParticipant p : getTeamManager().getParticipants()) {
                if (p.sessionPlayer().isEmpty())
                    continue;

                Player player = p.sessionPlayer().get();

                p.showDeathMatchTask(deathMatchTask);
                Message.sendTitleMessage(
                        "" + ChatColor.BOLD + ChatColor.GOLD + "GO",
                        "" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + BingoTranslation.DEATHMATCH_SEARCH.translate(),
                        p.sessionPlayer().get());
            }

            var event = new BingoPlaySoundEvent(session, Sound.ENTITY_GHAST_SHOOT);
            Bukkit.getPluginManager().callEvent(event);
            return;
        }

        ChatColor color = switch (countdown) {
            case 1 -> ChatColor.RED;
            case 2 -> ChatColor.GOLD;
            default -> ChatColor.GREEN;
        };
        for (BingoParticipant p : getTeamManager().getParticipants()) {
            if (p.sessionPlayer().isEmpty())
                continue;

            Message.sendTitleMessage(color + "" + countdown, "", p.sessionPlayer().get());
            Message.sendDebug(color + "" + countdown, p.sessionPlayer().get());
        }

        BingoReloaded.scheduleTask(task -> startDeathMatchRecurse(countdown - 1), BingoReloaded.ONE_SECOND);
    }

    public void teleportPlayerAfterDeath(Player player) {
        if (player == null) return;
        respawnManager.removeDeadPlayer(player.getUniqueId()).ifPresentOrElse(location -> player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN),
                () -> new TranslatedMessage(BingoTranslation.RESPAWN_EXPIRED).color(ChatColor.RED).send(player));
    }

    public static void spawnPlatform(Location platformLocation, int size, boolean clearArea) {
        for (int x = -size; x < size + 1; x++) {
            for (int z = -size; z < size + 1; z++) {
                if (!platformLocation.getWorld().getType(
                        (int) platformLocation.getX() + x,
                        (int) platformLocation.getY(),
                        (int) platformLocation.getZ() + z).isSolid()) {
                    platformLocation.getWorld().setType(
                            (int) platformLocation.getX() + x,
                            (int) platformLocation.getY(),
                            (int) platformLocation.getZ() + z,
                            Material.WHITE_STAINED_GLASS);
                }
            }
        }

        if (clearArea) {
            for (int y = 1; y < 6; y++) {
                for (int x = -size; x < size + 1; x++) {
                    for (int z = -size; z < size + 1; z++) {
                        platformLocation.getWorld().setType(
                                (int) platformLocation.getX() + x,
                                (int) platformLocation.getY() + y,
                                (int) platformLocation.getZ() + z,
                                Material.AIR);
                    }
                }
            }
        }
    }

    public static void removePlatform(Location platformLocation, int size) {
        for (int x = -size; x < size + 1; x++) {
            for (int z = -size; z < size + 1; z++) {
                if (platformLocation.getWorld().getType(
                        (int) platformLocation.getX() + x,
                        (int) platformLocation.getY(),
                        (int) platformLocation.getZ() + z) == Material.WHITE_STAINED_GLASS) {
                    platformLocation.getWorld().setType(
                            (int) platformLocation.getX() + x,
                            (int) platformLocation.getY(),
                            (int) platformLocation.getZ() + z,
                            Material.AIR);
                }
            }
        }
    }

    private void teleportPlayersToStart(World world) {
        switch (config.playerTeleportStrategy) {
            case ALONE -> {
                for (BingoParticipant p : getTeamManager().getParticipants()) {
                    Location platformLocation = getRandomSpawnLocation(world);
                    teleportPlayerToStart(p, platformLocation, 5);

                    if (!getTeamManager().getParticipants().isEmpty()) {
                        spawnPlatform(platformLocation.clone(), 5, true);

                        BingoReloaded.scheduleTask(task ->
                                BingoGame.removePlatform(platformLocation, 5), (long) (Math.max(0, config.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                    }
                }
            }
            case TEAM -> {
                for (BingoTeam t : getTeamManager().getActiveTeams()) {
                    Location teamLocation = getRandomSpawnLocation(world);

                    Set<BingoParticipant> players = t.getMembers();
                    players.forEach(p -> teleportPlayerToStart(p, teamLocation, 5));

                    if (!players.isEmpty()) {
                        spawnPlatform(teamLocation, 5, true);

                        BingoReloaded.scheduleTask(task ->
                                BingoGame.removePlatform(teamLocation, 5), (long) (Math.max(0, config.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                    }
                }
            }
            case ALL -> {
                Location spawnLocation = getRandomSpawnLocation(world);
                Set<BingoParticipant> players = getTeamManager().getParticipants();
                players.forEach(p -> teleportPlayerToStart(p, spawnLocation, 5));
                if (!getTeamManager().getParticipants().isEmpty()) {
                    spawnPlatform(spawnLocation, 5, true);

                    BingoReloaded.scheduleTask(task ->
                            BingoGame.removePlatform(spawnLocation, 5), (long) (Math.max(0, config.gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
                }
            }
            default -> {
            }
        }
    }

    private static void teleportPlayerToStart(BingoParticipant participant, Location to, int spread) {
        if (participant.sessionPlayer().isEmpty())
            return;
        Player player = participant.sessionPlayer().get();

        Vector placement = Vector.getRandom().multiply(spread * 2).add(new Vector(-spread, -spread, -spread));
        Location playerLocation = to.clone().add(placement);
        playerLocation.setY(playerLocation.getY() + 10.0);
        player.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setRespawnLocation(to.clone().add(0.0, 2.0, 0.0), true);
    }

    private Location getRandomSpawnLocation(World world) {
        Vector position = Vector.getRandom().multiply(config.teleportMaxDistance * 2).subtract(new Vector(config.teleportMaxDistance, config.teleportMaxDistance, config.teleportMaxDistance));
        Location location = new Location(world, position.getX(), world.getHighestBlockYAt(position.getBlockX(), position.getBlockZ()), position.getZ());

        //find a not ocean biome to start the game in
        while (isOceanBiome(world.getBiome(location))) {
            position = Vector.getRandom().multiply(config.teleportMaxDistance);
            location = new Location(world, position.getBlockX(), world.getHighestBlockYAt(position.getBlockX(), position.getBlockZ()), position.getBlockZ());
        }

        return location;
    }

    /**
     * Counts RIVER as ocean biome!
     * @param biome biome to check
     * @return true if this plugin consider biome to be an ocean-like biome
     */
    private static boolean isOceanBiome(Biome biome) {
        return switch (biome) {
            case OCEAN,
                    RIVER,
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

    public BingoTask getDeathMatchTask() {
        return deathMatchTask;
    }

    public TaskProgressTracker getProgressTracker() {
        return progressTracker;
    }

// @EventHandlers ========================================================================

    public void handleBingoTaskComplete(final BingoTaskProgressCompletedEvent event) {
        String timeString = GameTimer.getTimeAsString(getGameTime());
        BingoParticipant participant = event.getTask().getCompletedBy().orElse(null);
        if (participant == null) {
            // I guess it was not actually completed?
            Message.warn("Task not completed correctly...? (Please report!)");
            return;
        }

        new TranslatedMessage(BingoTranslation.COMPLETED).color(ChatColor.AQUA)
                .arg(event.getTask().data.getName())
                .arg(ChatComponentUtils.convert(participant.getDisplayName(), participant.getTeam().getColor(), ChatColor.BOLD))
                .arg(timeString).color(ChatColor.WHITE)
                .sendAll(session);

        var soundEvent = new BingoPlaySoundEvent(session, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE);
        Bukkit.getPluginManager().callEvent(soundEvent);

        scoreboard.updateTeamScores();

        participant.sessionPlayer().ifPresent( player -> {
            BingoReloaded.incrementPlayerStat(player, BingoStatType.TASKS);
        });

        if (participant.getTeam().getCard().hasBingo(participant.getTeam())) {
            bingo(participant.getTeam());
            return;
        }

        // Start death match when all tasks have been completed in lockout
        BingoCard card = teamManager.getActiveTeams().getLeadingTeam().getCard();
        if (!(card instanceof LockoutBingoCard lockoutCard)) {
            return;
        }

        if (teamManager.getActiveTeams().getTotalCompleteCount() == lockoutCard.size.fullCardSize) {
            startDeathMatch(5);
        }
    }

    public void handleDeathmatchTaskComplete(final BingoDeathmatchTaskCompletedEvent event) {
        String timeString = GameTimer.getTimeAsString(getGameTime());
        BingoParticipant participant = event.getTask().getCompletedBy().orElse(null);
        if (participant == null) {
            // I guess it was not actually completed?
            Message.warn("Task not completed correctly...? (Please report!)");
            return;
        }

        bingo(participant.getTeam());
    }

    @Override
    public void handlePlayerInteract(final PlayerInteractEvent event) {
        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(event.getPlayer());
        if (participant == null || participant.sessionPlayer().isEmpty())
            return;

        if (event.getItem() == null || event.getItem().getType().isAir())
            return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (PlayerKit.WAND_ITEM.isCompareKeyEqual(event.getItem())) {
            if (!gameStarted)
                return;

            event.setCancelled(true);
            ((BingoPlayer) participant).useGoUpWand(event.getItem(), config.wandCooldown, config.wandDown, config.wandUp, config.platformLifetime);
        } else if (PlayerKit.CARD_ITEM.isCompareKeyEqual(event.getItem())) {
            // Show bingo card to player
            event.setCancelled(true);
            participant.showCard(deathMatchTask);
        }
    }

    public void handleEntityDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p))
            return;

        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(p);
        if (participant == null || participant.sessionPlayer().isEmpty())
            return;

        if (!getTeamManager().getParticipants().contains(participant))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;

        if (settings.effects().contains(EffectOptionFlags.NO_FALL_DAMAGE)) {
            event.setCancelled(true);
        }
    }

    public void handlePlayerDeath(final PlayerDeathEvent event) {
        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(event.getEntity());
        if (participant == null || participant.sessionPlayer().isEmpty())
            return;

        if (settings.effects().contains(EffectOptionFlags.KEEP_INVENTORY)) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        } else {
            for (ItemStack drop : event.getDrops()) {
                if (drop.getItemMeta().getPersistentDataContainer().getOrDefault(PDCHelper.createKey("kit.kit_item"), PersistentDataType.BOOLEAN, false)
                        || PlayerKit.CARD_ITEM.isCompareKeyEqual(drop)) {
                    drop.setAmount(0);
                }
            }
        }

        Location deathCoords = event.getEntity().getLocation();
        if (config.teleportAfterDeath) {
            BaseComponent[] teleportMsg = Message.createHoverCommandMessage(BingoTranslation.RESPAWN, "/bingo back");

            event.getEntity().spigot().sendMessage(teleportMsg);
            respawnManager.addPlayer(event.getEntity().getUniqueId(), deathCoords);
        }
    }

    public void handlePlayerRespawn(final PlayerRespawnEvent event) {
        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(event.getPlayer());
        if (participant == null || participant.sessionPlayer().isEmpty())
            return;

        if (!(participant instanceof BingoPlayer player))
            return;

        if (!settings.effects().contains(EffectOptionFlags.KEEP_INVENTORY)) {
            returnCardToPlayer(settings.kit().getCardSlot(), player);
            player.giveKit(settings.kit());
        } else {
            player.giveEffects(settings.effects(), 0);
        }
    }

    public void handleCountdownFinished(final CountdownTimerFinishedEvent event) {
        if (!event.getSession().phase().equals(this))
            return;

        if (event.getTimer() == timer) {
            BingoTeam leadingTeam = getTeamManager().getActiveTeams().getLeadingTeam();

            Set<BingoTeam> tiedTeams = new HashSet<>();
            tiedTeams.add(leadingTeam);

            // Regular bingo cannot draw, so end the game without a winner
            if (settings.mode() == BingoGamemode.REGULAR) {
                end(null);
                return;
            }

            int leadingPoints = leadingTeam.getCompleteCount();
            for (BingoTeam team : getTeamManager().getActiveTeams()) {
                if (team.getCompleteCount() == leadingPoints) {
                    tiedTeams.add(team);
                } else {
                    team.outOfTheGame = true;
                }
            }

            // If only 1 team is "tied" for first place, make that team win the game
            if (tiedTeams.size() == 1) {
                bingo(leadingTeam);
            } else {
                startDeathMatch(5);
            }
        } else if (event.getTimer() == startingTimer) {
            timer.start();
            gameStarted = true;
            var soundEvent = new BingoPlaySoundEvent(session, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST);
            var soundEvent2 = new BingoPlaySoundEvent(session, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH);
            Bukkit.getPluginManager().callEvent(soundEvent);
            Bukkit.getPluginManager().callEvent(soundEvent2);
        }
    }

    public void handlePlayerMove(final PlayerMoveEvent event) {
        if (gameStarted)
            return;

        BingoParticipant participant = teamManager.getPlayerAsParticipant(event.getPlayer());
        if (participant == null)
            return;

        Location newLoc = event.getTo();
        newLoc.setX(event.getFrom().getX());
        newLoc.setZ(event.getFrom().getZ());
        event.setTo(newLoc);
    }

    public void handlePlayerItemDamaged(final PlayerItemDamageEvent event) {
        if (settings.effects().contains(EffectOptionFlags.NO_DURABILITY)) {
            // Only disable durability for tools and armor due to some advancements being dependent on durability
            // decreasing, for example "this boat has legs" https://bugs.mojang.com/browse/MC-183764
            Material itemType = event.getItem().getType();
            if (MaterialHelper.isTool(itemType) || MaterialHelper.isArmor(itemType)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void setup() {
        start();
    }

    @Override
    public void end() {
        end(null);
    }


    @Override
    public void handleSettingsUpdated(BingoSettingsUpdatedEvent event) {
    }

    @Override
    public @Nullable BingoSession getSession() {
        return session;
    }

    // Take care of player effects =======================
    @Override
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {
        BingoParticipant participant = teamManager.getPlayerAsParticipant(event.getPlayer());
        if (!(participant instanceof BingoPlayer player))
            return;

        player.giveEffects(settings.effects(), config.gracePeriod);
    }

    @Override
    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {
        BingoParticipant participant = teamManager.getPlayerAsParticipant(event.getPlayer());
        if (!(participant instanceof BingoPlayer player))
            return;

        player.takeEffects(false);
    }

    @Override
    public void handleParticipantJoinedTeam(ParticipantJoinedTeamEvent event) {
        if (!(event.getParticipant() instanceof BingoPlayer player))
            return;

        player.giveEffects(settings.effects(), config.gracePeriod);
    }

    @Override
    public void handleParticipantLeftTeam(ParticipantLeftTeamEvent event) {
        if (!(event.getParticipant() instanceof BingoPlayer player))
            return;

        player.takeEffects(false);
    }
}
