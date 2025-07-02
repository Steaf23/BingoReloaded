package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.PlayerSerializationData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.event.PrepareNextBingoGameEvent;
import io.github.steaf23.bingoreloaded.event.core.BingoEventListener;
import io.github.steaf23.bingoreloaded.lib.api.Extension;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.event.events.PlayerJoinEvent;
import io.github.steaf23.bingoreloaded.lib.event.events.PlayerQuitEvent;
import io.github.steaf23.bingoreloaded.lib.event.events.PlayerTeleportEvent;
import io.github.steaf23.bingoreloaded.lib.inventory.MenuBoard;
import io.github.steaf23.bingoreloaded.lib.scoreboard.HUDRegistry;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager
{
    protected final Map<String, BingoSession> sessions;

    private final Extension extension;
    private final BingoConfigurationData config;
    private final MenuBoard menuBoard;
    private final HUDRegistry hudRegistry;

    private final PlayerSerializationData playerData;
    private final BingoEventListener eventListener;
    private final WorldData worldData;

    private boolean teleportingPlayer;

    public GameManager(@NotNull Extension extension, BingoConfigurationData config, MenuBoard menuBoard, HUDRegistry hudRegistry) {
        this.extension = extension;
        this.config = config;
        this.menuBoard = menuBoard;
        this.hudRegistry = hudRegistry;

        @Subst("gamemanager:none") String settingsName = config.getOptionValue(BingoOptions.CUSTOM_WORLD_GENERATION);
        Key generationSettings = settingsName.equals("null") ? null : Key.key(settingsName);
        this.worldData = new WorldData(extension, generationSettings);

        this.sessions = new HashMap<>();
        this.playerData = new PlayerSerializationData();
        this.eventListener = new BingoEventListener(this,
                config.getOptionValue(BingoOptions.DISABLE_ADVANCEMENTS),
                config.getOptionValue(BingoOptions.DISABLE_STATISTICS));

        this.teleportingPlayer = false;
        extension.registerListener(eventListener);

        if (config.getOptionValue(BingoOptions.CLEAR_DEFAULT_WORLDS))
        {
            this.worldData.clearWorlds();
        }
    }

    public void setup(List<String> worldNames) {
        for (String world : worldNames) {
            createSession(world);
        }

        ConsoleMessenger.log("Created world(s) " + worldNames);
    }

    public boolean createSession(String sessionName) {
        if (sessions.containsKey(sessionName)) {
            ConsoleMessenger.error("An instance of Bingo already exists in world '" + sessionName + "'!");
            return false;
        }

        BingoSession session = new BingoSession(menuBoard, hudRegistry, worldData.createWorldGroup(sessionName), config);
        sessions.put(sessionName, session);
        return true;
    }

    public boolean destroySession(String sessionName) {
        if (!sessions.containsKey(sessionName)) {
            return false;
        }

        endGame(sessionName);
        WorldGroup group = worldData.getWorldGroup(sessionName);
        if (group == null) {
            ConsoleMessenger.bug("Could not destroy worlds from session properly", this);
            return false;
        }
        worldData.destroyWorldGroup(group);
        sessions.get(sessionName).destroy();
        sessions.remove(sessionName);
        return true;
    }

    public boolean startGame(String sessionName) {
        if (!sessions.containsKey(sessionName)) {
            ConsoleMessenger.log("Cannot start a game that doesn't exist, create it first using '/autobingo <world> create'!");
            return false;
        }

        if (isSessionRunning(sessionName)) {
            ConsoleMessenger.log("Could not start bingo because the game is already running on world '" + sessionName + "'!");
            return false;
        }

        sessions.get(sessionName).startGame();
        return true;
    }

    public boolean endGame(String sessionName) {
        if (!isSessionRunning(sessionName)) {
            ConsoleMessenger.log("Could not end bingo because no game was started on world '" + sessionName + "'!");
            return false;
        }

        BingoSession session = sessions.get(sessionName);
        session.endGame();
        return true;
    }

    public BingoSession getSession(String sessionName) {
        if (sessions.containsKey(sessionName)) {
            return sessions.get(sessionName);
        }
        return null;
    }

    public @Nullable BingoSession getSessionFromWorld(@NotNull WorldHandle world) {
        for (String session : sessions.keySet()) {
            BingoSession s = sessions.get(session);
            if (s.ownsWorld(world)) {
                return s;
            }
        }
        return null;
    }

    public String getNameOfSession(@Nullable BingoSession session) {
        if (session == null) {
            return "";
        }

        return getSessionNames().stream().filter(name -> getSession(name) != null).findFirst().orElse("");
    }

    public void onPluginDisable() {
        extension.unregisterListener(eventListener);

        for (String session : sessions.keySet()) {
            sessions.get(session).destroy();
            sessions.remove(session);
        }
    }

    public BingoConfigurationData getGameConfig() {
        return config;
    }

    public boolean isSessionRunning(String sessionName) {
        return sessions.containsKey(sessionName) && sessions.get(sessionName).isRunning();
    }

    public boolean canPlayerOpenMenus(PlayerHandle player) {
        return getSessionFromWorld(player.world()) != null;
    }

    public boolean teleportPlayerToSession(PlayerHandle player, String sessionName) {
        WorldGroup bingoWorld = worldData.getWorldGroup(sessionName);
        if (bingoWorld == null) {
            return false;
        }

        bingoWorld.teleportPlayer(player);
        return true;
    }

    public @Nullable BingoSession getSessionOfPlayer(PlayerHandle player) {
        for (String sessionName : sessions.keySet()) {
            BingoSession session = sessions.get(sessionName);
            BingoParticipant participant = session.teamManager.getPlayerAsParticipant(player);
            if (participant != null) {
                return session;
            }
        }

        return null;
    }

    public Collection<String> getSessionNames() {
        return sessions.keySet();
    }

    public void handlePlayerTeleport(final PlayerTeleportEvent event) {
        WorldHandle sourceWorld = event.fromPosition().world();
        WorldHandle targetWorld = event.toPosition().world();

        // If the world didn't change, the event is not interesting for us
        if (sourceWorld == targetWorld) {
            return;
        }

        if (teleportingPlayer) {
            teleportingPlayer = false;
            return;
        }

        if (sourceWorld == null) {
            ConsoleMessenger.bug("Source world is invalid", this);
            return;
        }
        if (targetWorld == null) {
            ConsoleMessenger.bug("Target world is invalid", this);
            return;
        }

        BingoSession sourceSession = getSessionFromWorld(sourceWorld);
        BingoSession targetSession = getSessionFromWorld(targetWorld);

        // We could have gone through a portal, so still both worlds could be in the same session, so we can return.
        if (sourceSession == targetSession) {
            return;
        }

        boolean savePlayerInformation = config.getOptionValue(BingoOptions.SAVE_PLAYER_INFORMATION);

        PlayerHandle player = event.player();

        if (sourceSession != null) {
            if (targetSession == null) {
                player.clearInventory(); // If we are leaving a bingo world, we can always clear the player's inventory

                if (savePlayerInformation) {
                    teleportingPlayer = true;
                    // load player will teleport them, so we have to schedule it to make sure to do the right thing
                    BingoReloaded.scheduleTask(t -> {
                        if (playerData.loadPlayer(player) == null) {
//                        // Player data was not saved for some reason?
//                        ConsoleMessenger.bug(Component.text("No saved player data could be found for ").append(event.getPlayer().displayName()).append(Component.text(", resetting data")), this);
//                        // Using the boolean we can check if we were already teleporting the player.
//                        SerializablePlayer.reset(plugin, event.getPlayer(), event.getTo()).apply(event.getPlayer());
                        }
                    });

                    event.setCancelled(true);
                }
            }
            sourceSession.removePlayer(event.player());
        }

        if (targetSession != null) {
            if (savePlayerInformation && sourceSession == null) {
                //FIXME: REFACTOR SerializablePlayer
//                // Only save player data if it does not pertain to a bingo world
//                SerializablePlayer serializablePlayer = SerializablePlayer.fromPlayer(extension, player);
//                serializablePlayer.location = event.fromPosition();
//                playerData.savePlayer(serializablePlayer, false);
            }

            // If the player was already playing in another session, remove them from that game first
            BingoSession previousSession = getSessionOfPlayer(player);
            if (previousSession != null) {
                BingoParticipant participant = previousSession.teamManager.getPlayerAsParticipant(player);
                if (participant != null) {
                    previousSession.removeParticipant(participant);
                }
                else {
                    // Maybe we can cheat it by creating a new team and then seeing if the player can be removed from automatic players...
                    BingoPlayer playerProxy = new BingoPlayer(player, previousSession);
                    previousSession.removeParticipant(playerProxy);
                }
            }

            // set spawn point of player in session world
            player.setRespawnPoint(targetSession.getOverworld().spawnPoint(), true);
            targetSession.addPlayer(player);
        }
    }

    public void handlePlayerJoinsServer(final PlayerJoinEvent event) {
        BingoSession targetSession = getSessionFromWorld(event.player().world());

        if (targetSession != null) {
            targetSession.addPlayer(event.player());
        }
    }

    public void handlePlayerQuitsServer(final PlayerQuitEvent event) {
        BingoSession sourceSession = getSessionFromWorld(event.player().world());

        if (sourceSession != null) {
            sourceSession.removePlayer(event.player());
        }
    }

    public void handlePrepareNextBingoGame(final PrepareNextBingoGameEvent event) {
        if (config.getOptionValue(BingoOptions.SAVE_PLAYER_INFORMATION) &&
                config.getOptionValue(BingoOptions.LOAD_PLAYER_INFORMATION_STRATEGY) == BingoOptions.LoadPlayerInformationStrategy.AFTER_GAME) {
            for (BingoParticipant participant : event.getSession().teamManager.getParticipants()) {
                participant.sessionPlayer().ifPresent(player -> {
                    event.getSession().teamManager.removeMemberFromTeam(participant);
                    playerData.loadPlayer(player);
                });
            }
        }
    }

    public PlayerSerializationData getPlayerData() {
        return playerData;
    }
}
