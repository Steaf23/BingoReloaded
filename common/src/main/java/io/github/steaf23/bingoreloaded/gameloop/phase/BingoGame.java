package io.github.steaf23.bingoreloaded.gameloop.phase;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.BingoEvents;
import io.github.steaf23.bingoreloaded.lib.api.BiomeType;
import io.github.steaf23.bingoreloaded.lib.api.InteractAction;
import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.MapRenderer;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.cards.CardFactory;
import io.github.steaf23.bingoreloaded.cards.LockoutTaskCard;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.data.BingoCardData;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gui.hud.BingoGameHUDGroup;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.event.EventResults;
import io.github.steaf23.bingoreloaded.lib.world.BlockHelper;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.player.PlayerRespawnManager;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.tracker.TaskProgressTracker;
import io.github.steaf23.bingoreloaded.util.ActionBarManager;
import io.github.steaf23.bingoreloaded.util.BingoPlayerSender;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BingoGame implements GamePhase
{
    private final ServerSoftware platform;
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
    private final Map<UUID, WorldPosition> playerSpawnPoints;

    private GameTask deathMatchTask;

    private final Runnable onGameEndedCallback;

    public BingoGame(ServerSoftware platform, @NotNull BingoSession session, @NotNull BingoSettings settings, @NotNull BingoConfigurationData config, Runnable onGameEndedCallback) {
		this.platform = platform;
		this.session = session;
        this.config = config;
        this.teamManager = session.teamManager;
        this.scoreboard = session.scoreboard;
        this.settings = settings;
        this.actionBarManager = new ActionBarManager(session);
        this.progressTracker = new TaskProgressTracker(platform, this);
		this.onGameEndedCallback = onGameEndedCallback;

		this.respawnManager = new PlayerRespawnManager(platform, config.getOptionValue(BingoOptions.TELEPORT_AFTER_DEATH_PERIOD));
        this.playerSpawnPoints = new HashMap<>();
    }

    private void start() {
        this.gameStarted = false;
        // Create timer
        if (settings.useCountdown())
            timer = new CountdownTimer(settings.countdownDuration() * 60, 5 * 60, 60, this::onCountdownTimerFinished);
        else
            timer = new CounterTimer();
        timer.addNotifier(time ->
        {
            Component timerMessage = timer.getTimeDisplayMessage(false);
            actionBarManager.requestMessage(p -> timerMessage, 0);
            actionBarManager.update();
            getProgressTracker().updateStatisticProgress();
            scoreboard.updateVisible();
        });

        deathMatchTask = null;
        WorldHandle world = session.getOverworld();
        if (world == null) {
            return;
        }
        world.setStorming(false);
        world.setTimeOfDay(1000);

        // Generate cards
        boolean useAdvancements = !(platform.areAdvancementsDisabled() || config.getOptionValue(BingoOptions.DISABLE_ADVANCEMENTS));

        GameTask.TaskDisplayMode advancementDisplayMode = config.getOptionValue(BingoOptions.SHOW_UNIQUE_ADVANCEMENT_ITEMS) ? GameTask.TaskDisplayMode.UNIQUE_TASK_ITEMS : GameTask.TaskDisplayMode.GENERIC_TASK_ITEMS;
        GameTask.TaskDisplayMode statisticDisplayMode = config.getOptionValue(BingoOptions.SHOW_UNIQUE_STATISTIC_ITEMS) ? GameTask.TaskDisplayMode.UNIQUE_TASK_ITEMS : GameTask.TaskDisplayMode.GENERIC_TASK_ITEMS;
        Set<TaskCard> uniqueCards = CardFactory.generateCardsForGame(this, session.getMenuBoard(),
                useAdvancements, !config.getOptionValue(BingoOptions.DISABLE_STATISTICS),
                advancementDisplayMode, statisticDisplayMode);

        //FIXME: REFACTOR move to paper
//        if (config.getOptionValue(BingoOptions.USE_MAP_RENDERER)) {
//            getTeamManager().getActiveTeams().forEach(team -> {
//                Optional<TaskCard> card = team.getCard();
//                card.ifPresentOrElse(
//                        taskCard -> renderers.put(team, new BingoCardMapRenderer(BingoReloaded.getInstance(), taskCard, team)),
//                        () -> ConsoleMessenger.bug("Could not generate card for team " + PlainTextComponentSerializer.plainText().serialize(team.getColoredName()), this)
//                );
//            });
//        }

        BingoMessage.GIVE_CARDS.sendToAudience(session);
        teleportPlayersToStart(world);

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

        getTeamManager().getParticipants().forEach(p ->
        {
            if (p.sessionPlayer().isPresent()) {
                PlayerHandle player = p.sessionPlayer().get();

                p.giveKit(settings.kit());
//                returnCardToPlayer(settings.kit().getCardSlot(), p, renderers.get(p.getTeam()));
                returnCardToPlayer(settings.kit().getCardSlot(), p, null);
                player.setLevel(0);
                player.setExp(0.0f);
                scoreboard.addPlayer(player);
            } else if (!p.alwaysActive()) {
                // If the player is not online, we can remove them from the game, as they probably did not intend on playing in this session
                session.removeParticipant(p);
            }
        });

        for (TaskCard card : uniqueCards) {
            card.getTasks().forEach(t -> getProgressTracker().startTrackingTask(t));
        }

        // Post-start Setup
        scoreboard.setup(settings);

        //FIXME: REFACTOR add bingo started event
//        var event = new BingoStartedEvent(session);
//        Bukkit.getPluginManager().callEvent(event);

        // Countdown before the game actually starts
        startingTimer = new CountdownTimer(Math.max(1, config.getOptionValue(BingoOptions.STARTING_COUNTDOWN_TIME)), 6, 3, this::onStartingTimerFinished);
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
                playSound(Sound.sound(Key.key("minecraft:block_note_block_bit"), Sound.Source.UI, 1.2f - time / 10.0f + 0.2f, pitch));
                playSound(Sound.sound(Key.key("minecraft:block_note_block_pling"), Sound.Source.UI, 1.2f - time / 10.0f + 0.2f, pitch));
            }
        });
        platform.runTask(BingoReloaded.ONE_SECOND, task -> startingTimer.start());
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

        playSound(Sound.sound(Key.key("minecraft:entity_lightning_bolt_thunder"), Sound.Source.UI, 1.0f, 1.0f));

        //FIXME: REFACTOR add command after game feature
//        String command = config.getOptionValue(BingoOptions.SEND_COMMAND_AFTER_GAME_ENDS);
//        if (!command.isEmpty()) {
//            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
//        }

        session.sendMessage(Component.text(" "));
        onGameEndedCallback.run();
    }

    public void bingo(@NotNull BingoTeam team) {
        BingoMessage.BINGO.sendToAudience(session, team.getColoredName());
        for (BingoParticipant p : getTeamManager().getParticipants()) {
            if (p.sessionPlayer().isEmpty())
                continue;

            PlayerHandle player = p.sessionPlayer().get();

            if (team.equals(p.getTeam())) {
                BingoReloaded.incrementPlayerStat(player, BingoStatType.WINS);
            } else {
                BingoReloaded.incrementPlayerStat(player, BingoStatType.LOSSES);
            }
        }
        playSound(Sound.sound(Key.key("minecraft:ui_toast_challenge_complete"), Sound.Source.UI, 0.75f, 1.0f));
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
        participant.sessionPlayer().get().setGamemode(PlayerGamemode.SURVIVAL);

        platform.runTask(task -> participant.giveEffects(settings.effects(), config.getOptionValue(BingoOptions.GRACE_PERIOD)));
    }

    public void startDeathMatch(int seconds) {
        BingoMessage.DEATHMATCH_START.sendToAudience(session);

        playSound(Sound.sound(Key.key("minecraft:entity_parrot_imitate_ghast"), Sound.Source.UI, 1.0f, 1.0f));
        startDeathMatchRecurse(seconds);
    }

    //FIXME: don't use recursion to create tasks..
    private void startDeathMatchRecurse(int countdown) {
        if (countdown == 0) {
            deathMatchTask = new GameTask(new BingoCardData().getRandomItemTask(settings.card()), GameTask.TaskDisplayMode.UNIQUE_TASK_ITEMS);

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

            playSound(Sound.sound(Key.key("minecraft:entity_ghast_shoot"), Sound.Source.UI, 1.0f, 1.0f));

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

        platform.runTask(BingoReloaded.ONE_SECOND, task -> startDeathMatchRecurse(countdown - 1));
    }

    public void teleportPlayerAfterDeath(PlayerHandle player) {
        if (player == null) return;
        respawnManager.removeDeadPlayer(player.uniqueId()).ifPresentOrElse(player::teleportAsync,
                () -> BingoMessage.RESPAWN_EXPIRED.sendToAudience(player, NamedTextColor.RED));
    }

    public static void spawnPlatform(WorldPosition platformLocation, int size, boolean clearArea) {
        BlockHelper.buildPlatform(ItemType.of("minecraft:white_stained_glass"), platformLocation, size, size, true, null);

        if (!clearArea) {
            return;
        }
        for (int y = 1; y < 6; y++) {
            for (int x = -size; x < size + 1; x++) {
                for (int z = -size; z < size + 1; z++) {
                    BlockHelper.placeBlock(ItemType.AIR, platformLocation.clone()
                            .moveXBlocks(x)
                            .moveYBlocks(y)
                            .moveZBlocks(z),
                            false,
                            null);
                }
            }
        }
    }

    public static void removePlatform(WorldPosition platformLocation, int size) {
        BlockHelper.buildPlatform(ItemType.AIR, platformLocation, size, size, true, ItemType.of("minecraft:white_stained_glass"));
    }

    private void teleportPlayersToStart(WorldHandle world) {
        int gracePeriod = config.getOptionValue(BingoOptions.GRACE_PERIOD);

        // Platform should at least last as long as the starting countdown time.
        int platformLifetime = Math.max(config.getOptionValue(BingoOptions.STARTING_COUNTDOWN_TIME), Math.max(0, gracePeriod - 5)) * BingoReloaded.ONE_SECOND;
        switch (config.getOptionValue(BingoOptions.PLAYER_TELEPORT_STRATEGY)) {
            case ALONE -> {
                for (BingoParticipant p : getTeamManager().getParticipants()) {
                    WorldPosition platformLocation = getRandomSpawnLocation(world);
                    if (!getTeamManager().getParticipants().isEmpty()) {
                        spawnPlatform(platformLocation.clone(), 5, true);

                        platform.runTask(platformLifetime, task ->
                                BingoGame.removePlatform(platformLocation, 5));
                    }
                    teleportPlayerToStart(p, platformLocation, 5);
                }
            }
            case TEAM -> {
                for (BingoTeam t : getTeamManager().getActiveTeams()) {
                    WorldPosition teamLocation = getRandomSpawnLocation(world);

                    Set<BingoParticipant> players = t.getMembers();
                    if (!players.isEmpty()) {
                        spawnPlatform(teamLocation, 5, true);

                        platform.runTask(platformLifetime, task ->
                                BingoGame.removePlatform(teamLocation, 5));
                    }
                    players.forEach(p -> teleportPlayerToStart(p, teamLocation, 5));
                }
            }
            case ALL -> {
                WorldPosition spawnLocation = getRandomSpawnLocation(world);
                if (!getTeamManager().getParticipants().isEmpty()) {
                    spawnPlatform(spawnLocation, 5, true);

                    platform.runTask(platformLifetime, task ->
                            BingoGame.removePlatform(spawnLocation, 5));
                }

                Set<BingoParticipant> players = getTeamManager().getParticipants();
                players.forEach(p -> teleportPlayerToStart(p, spawnLocation, 5));
            }
            default -> {
            }
        }
    }

    private void teleportPlayerToStart(BingoParticipant participant, WorldPosition to, int spread) {
        if (participant.sessionPlayer().isEmpty())
            return;
        PlayerHandle player = participant.sessionPlayer().get();

        WorldPosition playerLocation = BlockHelper.getRandomPosWithinRange(to, spread, spread);
        playerLocation.moveYBlocks(5);
        player.teleportAsync(playerLocation);

        WorldPosition spawnLocation = to.clone().moveYBlocks(2);
        player.setRespawnPoint(spawnLocation, true);
        playerSpawnPoints.put(player.uniqueId(), spawnLocation);
    }

    private WorldPosition getRandomSpawnLocation(WorldHandle world) {
        int teleportMaxDistance = config.getOptionValue(BingoOptions.TELEPORT_MAX_DISTANCE);

        WorldPosition randomPosition = BlockHelper.getRandomPosWithinRange(new WorldPosition(world, 0.0D, 0.0D, 0.0D), teleportMaxDistance, teleportMaxDistance);
        WorldPosition location = new WorldPosition(world, randomPosition.x(), BlockHelper.getHighestBlockYAtPos(randomPosition), randomPosition.z());

        //find a not ocean biome to start the game in
        BiomeType biome = world.biomeAtPos(location);
        while (biome.isOcean() || biome.isRiver()) {
            randomPosition = BlockHelper.getRandomPosWithinRange(new WorldPosition(world, 0.0D, 0.0D, 0.0D), teleportMaxDistance, teleportMaxDistance);
            location = new WorldPosition(world, randomPosition.x(), BlockHelper.getHighestBlockYAtPos(randomPosition), randomPosition.z());
        }

        return location;
    }

    public GameTask getDeathMatchTask() {
        return deathMatchTask;
    }

    public TaskProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public void playSound(Sound sound) {
        session.playSound(sound);
    }

// @EventHandlers ========================================================================

    public void onBingoTaskCompleted(@NotNull BingoParticipant participant, GameTask task) {
        Component timeString = GameTimer.getTimeAsComponent(getGameTime());
        BingoTeam team = task.getCompletedByTeam().orElse(null);

        if (team == null) {
            ConsoleMessenger.bug("Player " + participant.getName() + " is not in a team?", this);
            return;
        }

        BingoMessage.COMPLETED.sendToAudience(session, NamedTextColor.AQUA,
                task.data.getName(),
                participant.getDisplayName().color(team.getColor()).decorate(TextDecoration.BOLD),
                timeString.color(NamedTextColor.WHITE));

        playSound(Sound.sound(Key.key("minecraft:entity_dragon_fireball_explode"), Sound.Source.UI, 1.0f, 1.0f));

        scoreboard.updateTeamScores();

        participant.sessionPlayer().ifPresent(player -> {
            BingoReloaded.incrementPlayerStat(player, BingoStatType.TASKS);
        });

        if (participant.getCard().isPresent() && participant.getCard().get().hasTeamWon(team)) {
            bingo(team);
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

    public void onDeathmatchTaskComplete(BingoParticipant participant, GameTask deathMatchTask) {
        BingoTeam team = deathMatchTask.getCompletedByTeam().orElse(null);
        if (participant == null) {
            // I guess it was not actually completed?
            ConsoleMessenger.bug("Task not completed correctly...?", this);
            return;
        }

        if (team == null) {
            ConsoleMessenger.bug("Player " + participant.getName() + " completing Deathmatch task is not in a team?", this);
            return;
        }

        bingo(team);
    }

    public EventResult<?> handlePlayerFallDamage(PlayerHandle player) {

        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(player);
        if (participant == null || participant.sessionPlayer().isEmpty()) {
            return EventResult.PASS;
        }

        if (!getTeamManager().getParticipants().contains(participant)) {
            return EventResult.PASS;
        }

        if (settings.effects().contains(EffectOptionFlags.NO_FALL_DAMAGE)) {
            return EventResult.CANCEL;
        } else {
            return EventResult.PASS;
        }
    }

    public EventResult<EventResults.PlayerDeathResult> handlePlayerDeath(PlayerHandle player, Collection<? extends StackHandle> droppedItems) {
        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(player);
        if (participant == null || participant.sessionPlayer().isEmpty())
            return new EventResult<>(false, null);

        boolean keepInventory = false;
        if (settings.effects().contains(EffectOptionFlags.KEEP_INVENTORY)) {
            keepInventory = true;
        } else {
            for (StackHandle drop : droppedItems) {
                var kitInfo = drop.getStorage("kit");
                if (kitInfo == null || kitInfo.getBoolean("kit_item", false)
                        || PlayerKit.CARD_ITEM.isCompareKeyEqual(drop)) {
                    drop.setAmount(0);
                }
            }
        }

        WorldPosition deathCoords = player.position();
        if (config.getOptionValue(BingoOptions.TELEPORT_AFTER_DEATH)) {
            Arrays.stream(BingoMessage.RESPAWN.convertForPlayer(player)).reduce(Component::append).ifPresent(hoverable -> {
                BingoPlayerSender.sendMessage(BingoMessage.createHoverCommandMessage(
                                Component.empty(),
                                hoverable,
                                null,
                                Component.empty(),
                                "/bingo back"),
                        player);
                respawnManager.addPlayer(player.uniqueId(), deathCoords);
            });
        }

        return EventResults.playerDeathResult(false, keepInventory);
    }

    public EventResult<EventResults.PlayerRespawnResult> handlePlayerRespawn(PlayerHandle player, boolean isBedSpawn, boolean isAnchorSpawn) {
        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(player);
        if (participant == null || participant.sessionPlayer().isEmpty())
            return EventResults.playerRespawnResult(false, false, null);

        if (!(participant instanceof BingoPlayer bingoPlayer))
            return EventResults.playerRespawnResult(false, false, null);

        if (!settings.effects().contains(EffectOptionFlags.KEEP_INVENTORY)) {
            returnCardToPlayer(settings.kit().getCardSlot(), bingoPlayer, renderers.get(bingoPlayer.getTeam()));
            bingoPlayer.giveKit(settings.kit());
        } else {
            bingoPlayer.giveEffects(settings.effects(), 0);
        }

        boolean correctRespawnPoint = !isBedSpawn && !isAnchorSpawn && player.respawnPoint() == null;
        if (correctRespawnPoint && playerSpawnPoints.containsKey(bingoPlayer.getId())) {
            WorldPosition newSpawnLocation = playerSpawnPoints.get(bingoPlayer.getId());
            player.setRespawnPoint(newSpawnLocation, true);
            return EventResults.playerRespawnResult(false, true, newSpawnLocation);
        } else {
            return EventResults.playerRespawnResult(false, false, null);
        }
    }

    public void onStartingTimerFinished() {
        timer.start();
        gameStarted = true;
        playSound(Sound.sound(Key.key("minecraft:entity_firework_rocket_large_blast"), Sound.Source.UI, 1.0f, 1.0f));
        playSound(Sound.sound(Key.key("minecraft:entity_firework_rocket_launch"), Sound.Source.UI, 1.0f, 1.0f));
    }

    public void onCountdownTimerFinished() {
        BingoTeam leadingTeam = getTeamManager().getActiveTeams().getLeadingTeam();

        Set<BingoTeam> tiedTeams = new HashSet<>();
        tiedTeams.add(leadingTeam);

        // Regular bingo cannot draw, so end the game without a winner
        if (settings.mode() == BingoGamemode.REGULAR || leadingTeam == null) {
            end((BingoTeam)null);
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
    }

    public EventResult<?> handlePlayerMove(final PlayerHandle player, WorldPosition from, WorldPosition to) {
        if (gameStarted)
            return EventResult.PASS;

        BingoParticipant participant = teamManager.getPlayerAsParticipant(player);
        if (participant == null)
            return EventResult.PASS;

        return EventResult.CANCEL;
    }

    public EventResult<?> handlePlayerStackDamaged(PlayerHandle player, StackHandle item) {
        if (settings.effects().contains(EffectOptionFlags.NO_DURABILITY)) {
            // Only disable durability for tools and armor due to some advancements being dependent on durability
            // decreasing, for example "this boat has legs" https://bugs.mojang.com/browse/MC-183764
            if (item.isTool() || item.isArmor()) {
                return EventResult.CANCEL;
            }
        }

        return EventResult.PASS;
    }

    @Override
    public void setup() {
        start();
    }

    @Override
    public void end() {
        end((BingoTeam)null);
    }

    @Override
    public EventResult<?> handlePlayerInteracted(PlayerHandle player, @Nullable StackHandle stack, InteractAction action) {
        BingoParticipant participant = getTeamManager().getPlayerAsParticipant(player);
        if (participant == null || participant.sessionPlayer().isEmpty())
            return EventResult.PASS;

        // Spectators should not be able to interact with custom items.
        if (participant.sessionPlayer().get().gamemode() == PlayerGamemode.SPECTATOR) {
            return EventResult.PASS;
        }

        if (stack == null || stack.type().isAir())
            return EventResult.PASS;

        if (!action.rightClick())
            return EventResult.PASS;

        if (PlayerKit.WAND_ITEM.isCompareKeyEqual(stack)) {
            if (!gameStarted)
                return EventResult.PASS;

            ((BingoPlayer) participant).useGoUpWand(stack,
                    config.getOptionValue(BingoOptions.GO_UP_WAND_COOLDOWN),
                    config.getOptionValue(BingoOptions.GO_UP_WAND_DOWN_DISTANCE),
                    config.getOptionValue(BingoOptions.GO_UP_WAND_UP_DISTANCE),
                    config.getOptionValue(BingoOptions.GO_UP_WAND_PLATFORM_LIFETIME));
            return EventResult.CANCEL;
        } else if (PlayerKit.CARD_ITEM.isCompareKeyEqual(stack)) {
            // Show bingo card to player
            participant.showCard(deathMatchTask);
            return EventResult.CANCEL;
        }

        return EventResult.PASS;
    }

    @Override
    public @NotNull BingoSession getSession() {
        return session;
    }

    // Take care of player effects =======================
    @Override
    public void handlePlayerJoinedSessionWorld(BingoEvents.PlayerSessionEvent event) {
        BingoParticipant participant = teamManager.getPlayerAsParticipant(event.player());
        if (!(participant instanceof BingoPlayer player))
            return;

        player.giveEffects(settings.effects(), config.getOptionValue(BingoOptions.GRACE_PERIOD));
    }

    @Override
    public void handlePlayerLeftSessionWorld(BingoEvents.PlayerSessionEvent event) {
        BingoParticipant participant = teamManager.getPlayerAsParticipant(event.player());
        if (!(participant instanceof BingoPlayer player))
            return;

        player.takeEffects(false);
    }

    @Override
    public void handleSettingsUpdated(BingoSettings newSettings) {

    }

    @Override
    public void handleParticipantJoinedTeam(BingoEvents.TeamParticipantEvent event) {
        if (!(event.participant() instanceof BingoPlayer player))
            return;

        player.giveEffects(settings.effects(), config.getOptionValue(BingoOptions.GRACE_PERIOD));
    }

    @Override
    public void handleParticipantLeftTeam(BingoEvents.TeamParticipantEvent event) {
        if (!(event.participant() instanceof BingoPlayer player))
            return;

        player.takeEffects(false);
    }
}
