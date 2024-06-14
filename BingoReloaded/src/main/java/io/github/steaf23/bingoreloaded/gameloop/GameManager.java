package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.PlayerSerializationData;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.event.BingoEventListener;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PrepareNextBingoGameEvent;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.team.BasicTeamManager;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.easymenulib.inventory.Menu;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.packetevents.PacketEvents;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameManager
{
    protected final Map<String, BingoSession> sessions;

    private final JavaPlugin plugin;
    private final ConfigData config;
    private final MenuBoard menuBoard;
    private final HUDRegistry hudRegistry;

    private final PlayerSerializationData playerData;
    private final BingoEventListener eventListener;

    private boolean teleportingPlayer;

    public GameManager(@NotNull JavaPlugin plugin, ConfigData config, MenuBoard menuBoard, HUDRegistry hudRegistry) {
        this.plugin = plugin;
        this.config = config;
        this.menuBoard = menuBoard;
        this.hudRegistry = hudRegistry;

        this.sessions = new HashMap<>();
        this.playerData = new PlayerSerializationData();
        this.eventListener = new BingoEventListener(this, config.disableAdvancements, config.disableStatistics);

        this.teleportingPlayer = false;

        Bukkit.getPluginManager().registerEvents(eventListener, plugin);
    }

    public boolean createSession(String sessionName) {
        if (sessions.containsKey(sessionName)) {
            Message.log("An instance of Bingo already exists in world '" + sessionName + "'!");
            return false;
        }

        BingoSession session = new BingoSession(this, menuBoard, hudRegistry, WorldData.createWorldGroup(plugin, sessionName), config);
        sessions.put(sessionName, session);
        return true;
    }

    public boolean destroySession(String sessionName) {
        if (!sessions.containsKey(sessionName)) {
            return false;
        }

        endGame(sessionName);
        WorldGroup group = WorldData.getWorldGroup(plugin, sessionName);
        if (group == null) {
            Message.error("Could not destroy worlds from session properly. (Please report!)");
            return false;
        }
        WorldData.destroyWorldGroup(plugin, WorldData.getWorldGroup(plugin, sessionName));
        sessions.get(sessionName).destroy();
        sessions.remove(sessionName);
        return true;
    }

    public boolean startGame(String sessionName) {
        if (!sessions.containsKey(sessionName)) {
            Message.log("Cannot start a game that doesn't exist, create it first using '/autobingo <world> create'!");
            return false;
        }

        if (isSessionRunning(sessionName)) {
            Message.log("Could not start bingo because the game is already running on world '" + sessionName + "'!");
            return false;
        }

        sessions.get(sessionName).startGame();
        return true;
    }

    public boolean endGame(String sessionName) {
        if (!isSessionRunning(sessionName)) {
            Message.log("Could not end bingo because no game was started on world '" + sessionName + "'!");
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

    public @Nullable BingoSession getSessionFromWorld(@NotNull World world) {
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
        HandlerList.unregisterAll(eventListener);

        for (String session : sessions.keySet()) {
            sessions.get(session).destroy();
        }
    }

    public ConfigData getGameConfig() {
        return config;
    }

    public boolean isSessionRunning(String sessionName) {
        return sessions.containsKey(sessionName) && sessions.get(sessionName).isRunning();
    }

    public boolean canPlayerOpenMenu(Player player, Menu menu) {
        return getSessionFromWorld(player.getWorld()) != null;
    }

    public boolean teleportPlayerToSession(Player player, String sessionName) {
        WorldGroup bingoWorld = WorldData.getWorldGroup(plugin, sessionName);
        if (bingoWorld == null) {
            return false;
        }

        bingoWorld.teleportPlayer(player);
        return true;
    }

    public @Nullable BingoSession getSessionOfPlayer(Player player) {
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
        World sourceWorld = event.getFrom().getWorld();
        World targetWorld = event.getTo().getWorld();

        // If the world didn't change, the event is not interesting for us
        if (sourceWorld == targetWorld) {
            return;
        }

        if (teleportingPlayer) {
            teleportingPlayer = false;
            return;
        }

        if (sourceWorld == null) {
            Message.error("Source world is invalid (Please report!)");
            return;
        }
        if (targetWorld == null) {
            Message.error("Target world is invalid (Please report!)");
            return;
        }

        BingoSession sourceSession = getSessionFromWorld(sourceWorld);
        BingoSession targetSession = getSessionFromWorld(targetWorld);

        // We could have gone through a portal, so still both worlds could be in the same session, so we can return.
        if (sourceSession == targetSession) {
            return;
        }

        if (sourceSession != null) {
            if (config.savePlayerInformation && targetSession == null) {
                teleportingPlayer = true;
                if (playerData.loadPlayer(event.getPlayer()) == null) {
                    // Player data was not saved for some reason?
                    Message.error("No saved player data could be found for " + event.getPlayer().getDisplayName() + ", resetting data (Please report!).");
                    // Using the boolean we can check if we were already teleporting the player.
                    SerializablePlayer.reset(plugin, event.getPlayer(), event.getTo()).apply(event.getPlayer());
                }
                event.setCancelled(true);
            }
            sourceSession.removePlayer(event.getPlayer());
        }

        if (targetSession != null) {
            if (config.savePlayerInformation && sourceSession == null) {
                // Only save player data if it does not pertain to a bingo world
                SerializablePlayer serializablePlayer = SerializablePlayer.fromPlayer(plugin, event.getPlayer());
                serializablePlayer.location = event.getFrom();
                playerData.savePlayer(serializablePlayer, false);
            }

            // If the player was already playing in another session, remove them from that game first
            BingoSession previousSession = getSessionOfPlayer(event.getPlayer());
            if (previousSession != null) {
                BingoParticipant participant = previousSession.teamManager.getPlayerAsParticipant(event.getPlayer());
                if (participant != null) {
                    previousSession.removeParticipant(participant);
                }
                else {
                    // FIXME: remove when refactoring auto team
                    // Maybe we can cheat it by creating a new team and then seeing if the player can be removed from automatic players...
                    BingoPlayer playerProxy = new BingoPlayer(event.getPlayer(), previousSession);
                    previousSession.removeParticipant(playerProxy);
                }
            }

            // set spawn point of player in session world
            event.getPlayer().setRespawnLocation(targetSession.getOverworld().getSpawnLocation(), true);
            targetSession.addPlayer(event.getPlayer());
        }
    }

    public void handlePlayerJoinsServer(final PlayerJoinEvent event) {
        BingoSession targetSession = getSessionFromWorld(event.getPlayer().getWorld());

        if (targetSession != null) {
            targetSession.addPlayer(event.getPlayer());
        }
    }

    public void handlePlayerQuitsServer(final PlayerQuitEvent event) {
        BingoSession sourceSession = getSessionFromWorld(event.getPlayer().getWorld());

        if (sourceSession != null) {
            sourceSession.removePlayer(event.getPlayer());
        }
    }

    public void handlePrepareNextBingoGame(final PrepareNextBingoGameEvent event) {
        if (config.savePlayerInformation && config.loadPlayerInformationStrategy == ConfigData.LoadPlayerInformationStrategy.AFTER_GAME) {
            for (BingoParticipant participant : event.getSession().teamManager.getParticipants()) {
                participant.sessionPlayer().ifPresent(player -> {
                    event.getSession().teamManager.removeMemberFromTeam(participant);
                    playerData.loadPlayer(player);
                });
            }
        }
    }
}
