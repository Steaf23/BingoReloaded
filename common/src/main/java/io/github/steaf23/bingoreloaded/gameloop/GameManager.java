package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.api.BingoEventListener;
import io.github.steaf23.bingoreloaded.data.BingoLobbyData;
import io.github.steaf23.bingoreloaded.data.PlayerSerializationData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.data.world.WorldGroup;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.PlayerGamemode;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
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
import java.util.Optional;

public class GameManager {

	protected final Map<String, BingoSession> sessions;

	private final BingoReloadedRuntime runtime;
	private final BingoConfigurationData config;

	private final PlayerSerializationData playerData;
	private final BingoEventListener eventListener;
	private final WorldData worldData;
	private final BingoLobbyData lobbyData;

	private boolean teleportingPlayer;

	public GameManager(@NotNull BingoReloadedRuntime runtime, BingoConfigurationData config) {
		this.runtime = runtime;
		this.config = config;

		this.lobbyData = new BingoLobbyData();

		@Subst("gamemanager:none") String settingsName = config.getOptionValue(BingoOptions.CUSTOM_WORLD_GENERATION);
		Key generationSettings = settingsName.equals("null") ? null : Key.key(settingsName);
		this.worldData = new WorldData(runtime.getServerSoftware(), generationSettings);

		this.sessions = new HashMap<>();
		this.playerData = new PlayerSerializationData();

		this.teleportingPlayer = false;

		this.eventListener = new BingoEventListener(this,
				config.getOptionValue(BingoOptions.DISABLE_ADVANCEMENTS),
				config.getOptionValue(BingoOptions.DISABLE_STATISTICS));

		if (config.getOptionValue(BingoOptions.CLEAR_DEFAULT_WORLDS)) {
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

		BingoSession session = new BingoSession(this, worldData.createWorldGroup(sessionName), config);
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

	public EventResult<?> handlePlayerTeleport(final PlayerHandle player, final WorldPosition fromPos, final WorldPosition toPos) {
		WorldHandle sourceWorld = fromPos.world();
		WorldHandle targetWorld = toPos.world();

		// If the world didn't change, the event is not interesting for us
		if (sourceWorld == targetWorld) {
			return EventResult.PASS;
		}

		if (teleportingPlayer) {
			teleportingPlayer = false;
			return EventResult.PASS;
		}

		if (sourceWorld == null) {
			ConsoleMessenger.bug("Source world is invalid", this);
			return EventResult.PASS;
		}
		if (targetWorld == null) {
			ConsoleMessenger.bug("Target world is invalid", this);
			return EventResult.PASS;
		}

		BingoSession sourceSession = getSessionFromWorld(sourceWorld);
		BingoSession targetSession = getSessionFromWorld(targetWorld);

		// We could have gone through a portal, so still both worlds could be in the same session, so we can return.
		if (sourceSession == targetSession) {
			return EventResult.PASS;
		}

		boolean savePlayerInformation = config.getOptionValue(BingoOptions.SAVE_PLAYER_INFORMATION);

		boolean cancel = false;
		if (sourceSession != null) {
			if (targetSession == null) {
				player.clearInventory(); // If we are leaving a bingo world, we can always clear the player's inventory

				if (savePlayerInformation) {
					teleportingPlayer = true;
					// load player will teleport them, so we have to schedule it to make sure to do the right thing
					runtime.getServerSoftware().runTask(t -> {
						if (playerData.loadPlayer(player) == null) {
//                        // Player data was not saved for some reason?
//                        ConsoleMessenger.bug(Component.text("No saved player data could be found for ").append(event.getPlayer().displayName()).append(Component.text(", resetting data")), this);
//                        // Using the boolean we can check if we were already teleporting the player.
//                        SerializablePlayer.reset(plugin, event.getPlayer(), event.getTo()).apply(event.getPlayer());
						}
					});

					cancel = true;
				}
			}
			sourceSession.removePlayer(player);
		}

		if (targetSession != null) {
			if (savePlayerInformation && sourceSession == null) {
                // Only save player data if it does not pertain to a bingo world
                SerializablePlayer serializablePlayer = SerializablePlayer.fromPlayer(runtime.getServerSoftware(), player);
                serializablePlayer.location = toPos;
                playerData.savePlayer(serializablePlayer, false);
			}

			// If the player was already playing in another session, remove them from that game first
			BingoSession previousSession = getSessionOfPlayer(player);
			if (previousSession != null) {
				BingoParticipant participant = previousSession.teamManager.getPlayerAsParticipant(player);
				if (participant != null) {
					previousSession.removeParticipant(participant);
				} else {
					// Maybe we can cheat it by creating a new team and then seeing if the player can be removed from automatic players...
					BingoPlayer playerProxy = new BingoPlayer(player, previousSession);
					previousSession.removeParticipant(playerProxy);
				}
			}

			// set spawn point of player in session world
			player.setRespawnPoint(targetSession.getOverworld().spawnPoint(), true);
			targetSession.addPlayer(player);
		}

		return new EventResult<>(cancel, null);
	}

	public EventResult<?> handlePlayerJoinsServer(final PlayerHandle player) {
		BingoSession targetSession = getSessionFromWorld(player.world());

		if (targetSession != null) {
			targetSession.addPlayer(player);
		}

		return EventResult.PASS;
	}

	public EventResult<?> handlePlayerQuitsServer(final PlayerHandle player) {
		BingoSession sourceSession = getSessionFromWorld(player.world());

		if (sourceSession != null) {
			sourceSession.removePlayer(player);
		}

		return EventResult.PASS;
	}

	public void prepareNextBingoGame(BingoSession session) {
		if (config.getOptionValue(BingoOptions.SAVE_PLAYER_INFORMATION) &&
				config.getOptionValue(BingoOptions.LOAD_PLAYER_INFORMATION_STRATEGY) == BingoOptions.LoadPlayerInformationStrategy.AFTER_GAME) {
			for (BingoParticipant participant : session.teamManager.getParticipants()) {
				participant.sessionPlayer().ifPresent(player -> {
					session.teamManager.removeMemberFromTeam(participant);
					playerData.loadPlayer(player);
				});
			}
		}
	}

	public PlayerSerializationData getPlayerData() {
		return playerData;
	}

	public ServerSoftware getPlatform() {
		return runtime.getServerSoftware();
	}

	public BingoReloadedRuntime getRuntime() {
		return runtime;
	}

	public BingoEventListener eventListener() {
		return eventListener;
	}

	public BingoLobbyData getLobbyData() {
		return lobbyData;
	}
}
