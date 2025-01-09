package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.CardFactory;
import io.github.steaf23.bingoreloaded.cards.LockoutTaskCard;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.event.BingoDeathmatchTaskCompletedEvent;
import io.github.steaf23.bingoreloaded.event.BingoEndedEvent;
import io.github.steaf23.bingoreloaded.event.BingoPlaySoundEvent;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.BingoStartedEvent;
import io.github.steaf23.bingoreloaded.event.BingoTaskProgressCompletedEvent;
import io.github.steaf23.bingoreloaded.event.CountdownTimerFinishedEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantJoinedTeamEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantLeftTeamEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.hud.BingoGameHUDGroup;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.gui.map.BingoCardMapRenderer;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.PlayerRespawnManager;
import io.github.steaf23.bingoreloaded.player.team.BasicTeamManager;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.SoloTeamManager;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.TaskGenerator;
import io.github.steaf23.bingoreloaded.tasks.tracker.TaskProgressTracker;
import io.github.steaf23.bingoreloaded.util.ActionBarManager;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import io.github.steaf23.bingoreloaded.util.MaterialHelper;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import io.github.steaf23.playerdisplay.util.PDCHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public class BingoGame implements GamePhase {
    private final BingoSession session;
    private final BingoSettings settings;
    private final BingoGameHUDGroup scoreboard;
    private final TeamManager teamManager;
    private final PlayerRespawnManager respawnManager;
    private final TaskProgressTracker progressTracker;
    private final BingoConfigurationData config;
    private GameTimer timer;
    private CountdownTimer startingTimer;
    private boolean gameStarted;
    private final ActionBarManager actionBarManager;
    //Used to override bed spawns if they get broken to reset spawn point to game spawn point.
    private final Map<UUID, Location> playerSpawnPoints;

    private GameTask deathMatchTask;

    private Location spawnLocation;

    public TaskCard masterCard;
    public TaskGenerator.GeneratorSettings generatorSettings;

    Map<BingoTeam, BingoCardMapRenderer> renderers = new HashMap<>();

    public BingoGame(@NotNull BingoSession session, @NotNull BingoSettings settings, @NotNull BingoConfigurationData config) {
        this.session = session;
        this.config = config;
        this.teamManager = session.teamManager;
        this.scoreboard = session.scoreboard;
        this.settings = settings;
        this.actionBarManager = new ActionBarManager(session);
        this.progressTracker = new TaskProgressTracker(this);

        this.respawnManager = new PlayerRespawnManager(BingoReloaded.getInstance(), config.getOptionValue(BingoOptions.TELEPORT_AFTER_DEATH_PERIOD));
        this.playerSpawnPoints = new HashMap<>();
    }

    private void start() {
        this.gameStarted = false;
        // Create timer
        if (settings.useCountdown())
            timer = new CountdownTimer(settings.countdownDuration() * 60, 5 * 60, 60, session);
        else
            timer = new CounterTimer();

        Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        getTeamManager().getParticipants().forEach(p -> {
            if (p.sessionPlayer().isPresent()) {
                Player player = p.sessionPlayer().get();
                if (p.getTeam() == null) {
                    Team team = mainScoreboard.getEntityTeam(player);
                    if (team == null) {
                        return;
                    }
                    ((BasicTeamManager) teamManager).activateTeamFromId(team.getName());
                }
            }
        });

        timer.addNotifier(time ->
        {
            Component timerMessage = timer.getTimeDisplayMessage(false);
            actionBarManager.requestMessage(p -> timerMessage, 0);
            actionBarManager.update();
            getProgressTracker().updateStatisticProgress();
            scoreboard.updateVisible();

            session.getOverworld().getPlayers().forEach(p -> {
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(p);
                BingoParticipant participant = teamManager.getPlayerAsParticipant(p);
                // Check New Player joined a team (/team join)
                if (participant == null && (team != null || teamManager instanceof SoloTeamManager)) {
                    // Setup Team
                    BingoPlayer player = new BingoPlayer(p, session);
                    BingoTeam bTeam = null;
                    boolean newTeam = false;

                    if (teamManager instanceof BasicTeamManager manager) {
                        // If there is no team, create it
                        if (!manager.getActiveTeams().containsId(team.getName())) {
                            if (manager.activateTeamFromId(team.getName()) != null) {
                                newTeam = true;
                            } else {
                                team.removeEntity(p);
                                return;
                            }
                        }
                        // Join to team
                        manager.addMemberToTeam(player, team.getName());
                        bTeam = player.getTeam();
                        assert bTeam != null;
                        // Send Message
                        BingoMessage.JOIN.sendToAudience(player, NamedTextColor.GREEN, bTeam.getColoredName());
                    } else if (teamManager instanceof SoloTeamManager manager) {
                        // Setup Solo Team
                        manager.setupParticipant(player);
                        bTeam = player.getTeam();
                        assert bTeam != null;
                        newTeam = true;
                    }

                    if (newTeam) {
                        // Setup Card
                        TaskCard card = CardFactory.generateCardForTeam(bTeam, this);
                        card.getTasks().forEach(t -> getProgressTracker().addTask(t));
                        if (config.getOptionValue(BingoOptions.USE_MAP_RENDERER)) {
                            renderers.put(bTeam, new BingoCardMapRenderer(BingoReloaded.getInstance(), card, bTeam));
                        }
                    }

                    session.participantMap.put(player.getId(), player);

                    // Setup Participant
                    setupParticipant(player);
                    scoreboard.updateTeamScores();

                    // Teleport Player
                    switch (config.getOptionValue(BingoOptions.PLAYER_TELEPORT_STRATEGY)) {
                        case ALONE -> {
                            Location platformLocation = getRandomSpawnLocation(session.getOverworld());
                            createPlatform(platformLocation, p.getWorld());
                            teleportPlayerToStart(player, platformLocation);
                        }
                        case TEAM -> teleportPlayerToStart(player, bTeam.teamLocation);
                        default -> teleportPlayerToStart(player, this.spawnLocation);
                    }
                } else if (participant != null && team == null) { // Check Player leaved a team (/team leave)
                    session.removeParticipant(participant);
                    BingoMessage.LEAVE.sendToAudience(participant);
                }
            });
        });

        deathMatchTask = null;
        World world = session.getOverworld();
        if (world == null) {
            return;
        }
        world.setStorm(false);
        world.setTime(1000);

        // Generate cards
        Set<TaskCard> uniqueCards = CardFactory.generateCardsForGame(this, session.getMenuBoard());
        for (TaskCard card : uniqueCards) {
            card.getTasks().forEach(t -> getProgressTracker().addTask(t));
        }

        if (config.getOptionValue(BingoOptions.USE_MAP_RENDERER)) {
            getTeamManager().getActiveTeams().forEach(t -> {
                Optional<TaskCard> card = t.getCard();
                card.ifPresentOrElse(
                        c -> renderers.put(t, new BingoCardMapRenderer(BingoReloaded.getInstance(), c, t)),
                        () -> ConsoleMessenger.bug("Could not generate card for team " + PlainTextComponentSerializer.plainText().serialize(t.getColoredName()), this)
                );
            });
        }

        // Show settings to player inside a hover message
        Component hoverMessage = Component.text()
                .append(BingoMessage.OPTIONS_GAMEMODE.asPhrase()).append(Component.text(": "))
                .append(settings.mode().asComponent())
                .append(Component.text(" "))
                .append(settings.size().asComponent()).append(Component.text("\n"))
                .append(BingoMessage.OPTIONS_KIT.asPhrase()).append(Component.text(": "))
                .append(settings.kit().getDisplayName()).append(Component.text("\n"))
                .append(BingoMessage.OPTIONS_EFFECTS.asPhrase()).append(Component.text(": \n"))
                .append(Component.join(JoinConfiguration.separator(Component.text("\n")), EffectOptionFlags.effectsToText(settings.effects()))).append(Component.text("\n"))
                .append(BingoMessage.DURATION.asPhrase(settings.useCountdown() ?
                        GameTimer.getTimeAsComponent(settings.countdownDuration() * 60L) : Component.text("∞")))
                .build();
        BingoPlayerSender.sendMessage(BingoMessage.createHoverCommandMessage(
                Component.empty(),
                BingoMessage.SETTINGS_HOVER.asPhrase(),
                HoverEvent.showText(hoverMessage),
                Component.empty(),
                ""), session);

        getTeamManager().getParticipants().forEach(this::setupParticipant);
        BingoMessage.GIVE_CARDS.sendToAudience(session);
        teleportPlayersToStart(world);

        // Post-start Setup
        scoreboard.setup(settings);

        var event = new BingoStartedEvent(session);
        Bukkit.getPluginManager().callEvent(event);

        // Countdown before the game actually starts
        startingTimer = new CountdownTimer(Math.max(1, config.getOptionValue(BingoOptions.STARTING_COUNTDOWN_TIME)), 6, 3, session);
        startingTimer.addNotifier(time -> {
            Component timeComponent = Component.text(time);
            if (time == 0) {
                timeComponent = Component.text("GO").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD);
                BingoPlayerSender.sendTitle(timeComponent, session);
            }

            TextColor color = NamedTextColor.WHITE;
            float pitch;
            if (time <= startingTimer.lowThreshold) {
                pitch = 1.414214f;
                color = NamedTextColor.RED;
            } else if (time <= startingTimer.medThreshold) {
                pitch = 1.059463f;
                color = NamedTextColor.GOLD;
            } else {
                pitch = 0.890899f;
            }

            BingoPlayerSender.sendTitle(timeComponent.color(color), session);

            if (time <= startingTimer.lowThreshold && time > 0) {
                var soundEvent = new BingoPlaySoundEvent(session, Sound.BLOCK_NOTE_BLOCK_BIT, 1.2f - time / 10.0f + 0.2f, pitch);
                var soundEvent2 = new BingoPlaySoundEvent(session, Sound.BLOCK_NOTE_BLOCK_PLING, 1.2f - time / 10.0f + 0.2f, pitch);
                Bukkit.getPluginManager().callEvent(soundEvent);
                Bukkit.getPluginManager().callEvent(soundEvent2);
            }
        });
        BingoReloaded.scheduleTask(task -> startingTimer.start(), BingoReloaded.ONE_SECOND);
    }

    private void setupParticipant(BingoParticipant participant) {
        BingoTeam team = participant.getTeam();

        if (participant.sessionPlayer().isEmpty()) {
            if (!participant.alwaysActive()) {
                // If the player is not online, we can remove them from the game, as they probably did not intend on playing in this session
                session.removeParticipant(participant);
            }
            return;
        }

        if (team == null) return;

        Player player = participant.sessionPlayer().get();

        // Give Kit and Card
        participant.giveKit(settings.kit());
        returnCardToPlayer(settings.kit().getCardSlot(), participant, renderers.get(team));
        player.setLevel(0);
        player.setExp(0.0f);

        // Add to Scoreboard
        scoreboard.addPlayer(player);

        // Start Task Tracking
        for (GameTask task : progressTracker.progressMap.keySet()) {
            progressTracker.startTrackingTaskForParticipant(task, participant);
        }
    }

    public boolean hasStarted() {
        return gameStarted;
    }

    public void end(@Nullable BingoTeam winningTeam) {
        // If the starting timer was still running
        if (startingTimer != null) {
            startingTimer.stop();
        } else {
            ConsoleMessenger.bug("Could not stop the starting timer. This means something bad happened when ending a game, reload the plugin to continue without problems", this);
        }
        BingoPlayerSender.sendMessage(timer.getTimeDisplayMessage(false), session);
        timer.stop();

        if (!config.getOptionValue(BingoOptions.KEEP_SCOREBOARD_VISIBLE)) {
            scoreboard.setup(settings);
        }

        getTeamManager().getParticipants().forEach(p -> {
            p.takeEffects(false);
            p.sessionPlayer().ifPresent(player -> {
                int tasksCompleted = p.getAmountOfTaskCompleted();
                if (tasksCompleted > BingoReloaded.getPlayerStat(player, BingoStatType.RECORD_TASKS)) {
                    BingoReloaded.setPlayerStat(player, BingoStatType.RECORD_TASKS, tasksCompleted);
                }
            });
        });

        var soundEvent = new BingoPlaySoundEvent(session, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
        Bukkit.getPluginManager().callEvent(soundEvent);

        String command = config.getOptionValue(BingoOptions.SEND_COMMAND_AFTER_GAME_ENDS);
        if (!command.isEmpty()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        session.sendMessage(Component.text(" "));
        var event = new BingoEndedEvent(getGameTime(), winningTeam, session);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void bingo(@NotNull BingoTeam team) {
        BingoMessage.BINGO.sendToAudience(session, team.getColoredName());
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

    public BingoConfigurationData getConfig() {
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

    public void returnCardToPlayer(int cardSlot, BingoParticipant participant, @Nullable MapRenderer cardRenderer) {
        if (participant.sessionPlayer().isEmpty())
            return;

        participant.giveBingoCard(cardSlot, cardRenderer);
        participant.sessionPlayer().get().setGameMode(GameMode.SURVIVAL);

        BingoReloaded.scheduleTask(task -> participant.giveEffects(settings.effects(), config.getOptionValue(BingoOptions.GRACE_PERIOD)), BingoReloaded.ONE_SECOND);
    }

    public void startDeathMatch(int seconds) {
        BingoMessage.DEATHMATCH_START.sendToAudience(session);

        var soundEvent = new BingoPlaySoundEvent(session, Sound.ENTITY_PARROT_IMITATE_GHAST);
        Bukkit.getPluginManager().callEvent(soundEvent);

        startDeathMatchRecurse(seconds);
    }

    //FIXME: don't use recursion to create tasks..
    private void startDeathMatchRecurse(int countdown) {
        if (countdown == 0) {
            deathMatchTask = new GameTask(new BingoCardData().getRandomItemTask(settings.card()), GameTask.TaskDisplayMode.NON_ITEMS_UNIQUE);

            BingoPlayerSender.sendTitle(
                    Component.text("GO").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                    BingoMessage.DEATHMATCH_SEARCH.asPhrase().color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.ITALIC),
                    session
            );

            for (BingoParticipant p : getTeamManager().getParticipants()) {
                if (p.sessionPlayer().isEmpty())
                    continue;

                p.showDeathMatchTask(deathMatchTask);
            }

            var event = new BingoPlaySoundEvent(session, Sound.ENTITY_GHAST_SHOOT);
            Bukkit.getPluginManager().callEvent(event);
            return;
        }

        Component countdownComponent = Component.text(countdown);
        NamedTextColor color = switch (countdown) {
            case 1 -> NamedTextColor.RED;
            case 2 -> NamedTextColor.GOLD;
            default -> NamedTextColor.GREEN;
        };

        BingoPlayerSender.sendTitle(countdownComponent.color(color), session);
        BingoPlayerSender.sendMessage(countdownComponent.color(color), session);

        BingoReloaded.scheduleTask(task -> startDeathMatchRecurse(countdown - 1), BingoReloaded.ONE_SECOND);
    }

    public void teleportPlayerAfterDeath(Player player) {
        if (player == null) return;
        respawnManager.removeDeadPlayer(player.getUniqueId()).ifPresentOrElse(location -> player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN),
                () -> BingoMessage.RESPAWN_EXPIRED.sendToAudience(player, NamedTextColor.RED));
    }

    public static void spawnPlatform(Location platformLocation, int size, boolean clearArea) {
        for (int x = -size; x < size + 1; x++) {
            for (int z = -size; z < size + 1; z++) {
                Location blockLoc = platformLocation.clone();
                blockLoc.setX(blockLoc.getBlockX() + x);
                blockLoc.setZ(blockLoc.getBlockZ() + z);
                if (!platformLocation.getWorld().getType(blockLoc).isSolid()) {
                    platformLocation.getWorld().setType(blockLoc, Material.WHITE_STAINED_GLASS);
                }
            }
        }

        if (!clearArea) {
            return;
        }
        for (int y = 1; y < 6; y++) {
            for (int x = -size; x < size + 1; x++) {
                for (int z = -size; z < size + 1; z++) {
                    platformLocation.getWorld().setType(
                            (int) platformLocation.getBlockX() + x,
                            (int) platformLocation.getBlockY() + y,
                            (int) platformLocation.getBlockZ() + z,
                            Material.AIR);
                }
            }
        }
    }

    public static void removePlatform(Location platformLocation, int size) {
        for (int x = -size; x < size + 1; x++) {
            for (int z = -size; z < size + 1; z++) {
                Location blockLoc = platformLocation.clone();
                blockLoc.setX(blockLoc.getBlockX() + x);
                blockLoc.setZ(blockLoc.getBlockZ() + z);
                if (platformLocation.getWorld().getType(blockLoc) == Material.WHITE_STAINED_GLASS) {
                    platformLocation.getWorld().setType(blockLoc, Material.AIR);
                }
            }
        }
    }

    private void teleportPlayersToStart(World world) {
        switch (config.getOptionValue(BingoOptions.PLAYER_TELEPORT_STRATEGY)) {
            case ALONE -> {
                for (BingoParticipant p : getTeamManager().getParticipants()) {
                    Location spawnLocation = getRandomSpawnLocation(world);
                    createPlatform(spawnLocation, world);
                    teleportPlayerToStart(p, spawnLocation);
                }
            }
            case TEAM -> {
                for (BingoTeam t : getTeamManager().getActiveTeams()) {
                    t.teamLocation = getRandomSpawnLocation(world);
                    Set<BingoParticipant> players = t.getMembers();
                    if (!players.isEmpty()) {
                        createPlatform(t.teamLocation, world);
                    }
                    for (BingoParticipant p : players) {
                        teleportPlayerToStart(p, t.teamLocation);
                    }
                }
            }
            case ALL -> {
                this.spawnLocation = getRandomSpawnLocation(world);
                createPlatform(this.spawnLocation, world);
                for (BingoParticipant p : getTeamManager().getParticipants()) {
                    teleportPlayerToStart(p, this.spawnLocation);
                }
            }
        }
    }

    private void createPlatform(Location location, World world) {
        int gracePeriod = config.getOptionValue(BingoOptions.GRACE_PERIOD);

        spawnPlatform(location, 5, true);

        BingoReloaded.scheduleTask(task ->
                BingoGame.removePlatform(location, 5), (long) (Math.max(0, gracePeriod - 5)) * BingoReloaded.ONE_SECOND);
    }

    private void teleportPlayerToStart(BingoParticipant participant, Location to) {
        if (participant.sessionPlayer().isEmpty())
            return;

        int spread = 5;

        Player player = participant.sessionPlayer().get();

        Vector placement = Vector.getRandom().multiply(spread * 2).add(new Vector(-spread, -spread, -spread));
        Location playerLocation = to.clone().add(placement);
        playerLocation.setY(playerLocation.getY() + 5.0);
        player.teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        Location spawnLocation = to.clone().add(0.0, 2.0, 0.0);
        player.setRespawnLocation(spawnLocation, true);
        playerSpawnPoints.put(player.getUniqueId(), spawnLocation);
    }

    private Location getRandomSpawnLocation(World world) {
        int teleportMaxDistance = config.getOptionValue(BingoOptions.TELEPORT_MAX_DISTANCE);

        Vector position = Vector.getRandom().multiply(teleportMaxDistance * 2).subtract(new Vector(teleportMaxDistance, teleportMaxDistance, teleportMaxDistance));
        Location location = new Location(world, position.getX(), world.getHighestBlockYAt(position.getBlockX(), position.getBlockZ()), position.getZ());

        //find a not ocean biome to start the game in
        while (isOceanBiome(world.getBiome(location))) {
            position = Vector.getRandom().multiply(teleportMaxDistance);
            location = new Location(world, position.getBlockX(), world.getHighestBlockYAt(position.getBlockX(), position.getBlockZ()), position.getBlockZ());
        }

        return location;
    }

    /**
     * Counts RIVER as ocean biome!
     *
     * @param biome biome to check
     * @return true if this plugin consider biome to be an ocean-like biome
     */
    private static boolean isOceanBiome(Biome biome) {
        return biome == Biome.OCEAN ||
                biome == Biome.RIVER ||
                biome == Biome.DEEP_COLD_OCEAN ||
                biome == Biome.COLD_OCEAN ||
                biome == Biome.DEEP_OCEAN ||
                biome == Biome.FROZEN_OCEAN ||
                biome == Biome.DEEP_FROZEN_OCEAN ||
                biome == Biome.LUKEWARM_OCEAN ||
                biome == Biome.DEEP_LUKEWARM_OCEAN ||
                biome == Biome.WARM_OCEAN;
    }

    public GameTask getDeathMatchTask() {
        return deathMatchTask;
    }

    public TaskProgressTracker getProgressTracker() {
        return progressTracker;
    }

// @EventHandlers ========================================================================

    public void handleBingoTaskComplete(final BingoTaskProgressCompletedEvent event) {
        Component timeString = GameTimer.getTimeAsComponent(getGameTime());
        BingoParticipant participant = event.getTask().getCompletedBy().orElse(null);
        if (participant == null) {
            // I guess it was not actually completed?
            ConsoleMessenger.bug("Task not completed correctly...?", this);
            return;
        }

        BingoTeam team = participant.getTeam();
        if (team == null) {
            ConsoleMessenger.bug("Player " + participant.getName() + " is not on a team.", this);
            return;
        }

        BingoMessage.COMPLETED.sendToAudience(session, NamedTextColor.AQUA,
                event.getTask().data.getName(),
                participant.getDisplayName().color(team.getColor()).decorate(TextDecoration.BOLD),
                timeString.color(NamedTextColor.WHITE));

        var soundEvent = new BingoPlaySoundEvent(session, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE);
        Bukkit.getPluginManager().callEvent(soundEvent);

        scoreboard.updateTeamScores();

        participant.sessionPlayer().ifPresent(player -> {
            BingoReloaded.incrementPlayerStat(player, BingoStatType.TASKS);
        });

        if (participant.getCard().isPresent() && participant.getCard().get().hasTeamWon(participant.getTeam())) {
            bingo(participant.getTeam());
            return;
        }


        // Start death match when all tasks have been completed in lockout
        BingoTeam leadingTeam = teamManager.getActiveTeams().getLeadingTeam();
        if (leadingTeam == null) {
            return;
        }
        Optional<TaskCard> card = teamManager.getActiveTeams().getLeadingTeam().getCard();
        if (!(card.orElse(null) instanceof LockoutTaskCard lockoutCard)) {
            return;
        }

        if (teamManager.getActiveTeams().getTotalCompleteCount() == lockoutCard.size.fullCardSize) {
            startDeathMatch(5);
        }
    }

    public void handleDeathmatchTaskComplete(final BingoDeathmatchTaskCompletedEvent event) {
        BingoParticipant participant = event.getTask().getCompletedBy().orElse(null);
        if (participant == null) {
            // I guess it was not actually completed?
            ConsoleMessenger.bug("Task not completed correctly...?", this);
            return;
        }

        if (participant.getTeam() == null) {
            ConsoleMessenger.bug("Player " + participant.getName() + " completing Deathmatch task is not in a team?", this);
            return;
        }

        bingo(participant.getTeam());
    }

    @Override
    public void handlePlayerInteract(final PlayerInteractEvent event) {
        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(event.getPlayer());
        if (participant == null || participant.sessionPlayer().isEmpty())
            return;

        // Spectators should not be able to interact with custom items.
        if (participant.sessionPlayer().get().getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        if (event.getItem() == null || event.getItem().getType().isAir())
            return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (PlayerKit.WAND_ITEM.isCompareKeyEqual(event.getItem())) {
            if (!gameStarted)
                return;

            event.setCancelled(true);
            ((BingoPlayer) participant).useGoUpWand(event.getItem(),
                    config.getOptionValue(BingoOptions.GO_UP_WAND_COOLDOWN),
                    config.getOptionValue(BingoOptions.GO_UP_WAND_DOWN_DISTANCE),
                    config.getOptionValue(BingoOptions.GO_UP_WAND_UP_DISTANCE),
                    config.getOptionValue(BingoOptions.GO_UP_WAND_PLATFORM_LIFETIME));
        } else if (PlayerKit.CARD_ITEM.isCompareKeyEqual(event.getItem())) {
            // Show bingo card to player
            event.setCancelled(true);
            // Prevents cards from being opened when Wand is used
            if (!PlayerKit.WAND_ITEM.isCompareKeyEqual(event.getPlayer().getInventory().getItemInMainHand())) {
                participant.showCard(deathMatchTask);
            }
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
        if (config.getOptionValue(BingoOptions.TELEPORT_AFTER_DEATH)) {
            Arrays.stream(BingoMessage.RESPAWN.convertForPlayer(event.getPlayer())).reduce(Component::append).ifPresent(hoverable -> {
                BingoPlayerSender.sendMessage(BingoMessage.createHoverCommandMessage(
                                Component.empty(),
                                hoverable,
                                null,
                                Component.empty(),
                                "/bingo back"),
                        event.getPlayer());
                respawnManager.addPlayer(event.getEntity().getUniqueId(), deathCoords);
            });
        }
    }

    public void handlePlayerRespawn(final PlayerRespawnEvent event) {
        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(event.getPlayer());
        if (participant == null || participant.sessionPlayer().isEmpty())
            return;

        if (!(participant instanceof BingoPlayer player))
            return;

        if (!settings.effects().contains(EffectOptionFlags.KEEP_INVENTORY)) {
            returnCardToPlayer(settings.kit().getCardSlot(), player, renderers.get(player.getTeam()));
            player.giveKit(settings.kit());
        } else {
            player.giveEffects(settings.effects(), 0);
        }

        boolean correctRespawnPoint = !event.isBedSpawn() && !event.isAnchorSpawn() && event.getPlayer().getRespawnLocation() == null;
        if (correctRespawnPoint && playerSpawnPoints.containsKey(player.getId())) {
            Location newSpawnLocation = playerSpawnPoints.get(player.getId());
            event.setRespawnLocation(newSpawnLocation);
            event.getPlayer().setRespawnLocation(newSpawnLocation, true);
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
            if (settings.mode() == BingoGamemode.REGULAR || leadingTeam == null) {
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
    public @NotNull BingoSession getSession() {
        return session;
    }

    // Take care of player effects =======================
    @Override
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {
        BingoParticipant participant = teamManager.getPlayerAsParticipant(event.getPlayer());
        if (!(participant instanceof BingoPlayer player))
            return;

        player.giveEffects(settings.effects(), config.getOptionValue(BingoOptions.GRACE_PERIOD));
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

        player.giveEffects(settings.effects(), config.getOptionValue(BingoOptions.GRACE_PERIOD));
    }

    @Override
    public void handleParticipantLeftTeam(ParticipantLeftTeamEvent event) {
        if (!(event.getParticipant() instanceof BingoPlayer player))
            return;

        player.takeEffects(false);
    }
}
