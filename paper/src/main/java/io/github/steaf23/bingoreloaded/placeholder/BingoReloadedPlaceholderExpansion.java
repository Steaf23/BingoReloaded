package io.github.steaf23.bingoreloaded.placeholder;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoStatData;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BingoReloadedPlaceholderExpansion extends PlaceholderExpansion {

	private final BingoReloaded bingo;
	private final BingoPlaceholderFormatter formatter;
	private final PlatformServer server;

	public BingoReloadedPlaceholderExpansion(PlatformServer server, BingoReloaded bingo) {
		this.server = server;
		this.bingo = bingo;
		this.formatter = new BingoPlaceholderFormatter();
	}

	@Override
	public @NotNull String getIdentifier() {
		return "bingoreloaded";
	}

	@Override
	public @NotNull String getAuthor() {
		return String.join(", ", BingoReloaded.getMetaInfo().authors());
	}

	@Override
	public @NotNull String getVersion() {
		return BingoReloaded.getMetaInfo().version();
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
		BingoReloadedPlaceholder placeholder = BingoReloadedPlaceholder.fromString(params);
		if (placeholder == null) {
			ConsoleMessenger.error("unexpected placeholder '" + params + "' not found in bingo reloaded.");
			return null;
		}

		TextComponent defaultComponent = Component.text("-");

		Component placeholderComponent = switch (placeholder) {
			case CREATED_SESSION -> {
				String sessionName = params.replace(BingoReloadedPlaceholder.CREATED_SESSION.getName(), "");

				yield bingo.getGameManager().getSession(sessionName)
						.map(_ -> Component.text(sessionName))
						.orElse(Component.empty());
			}
			case COUNT_SESSION_PLAYERS -> {
				String sessionName = params.replace(BingoReloadedPlaceholder.COUNT_SESSION_PLAYERS.getName(), "");

				yield bingo.getGameManager().getSession(sessionName)
						.map(session -> Component.text(session.teamManager.getParticipants().size()))
						.orElse(Component.empty());
			}
			case TEAM_FULL -> getPlayerTeamPlaceholder(player, true, true);
			case TEAM_COLOR -> getPlayerTeamPlaceholder(player, false, true);
			case TEAM_NAME -> getPlayerTeamPlaceholder(player, true, false);
			case CURRENT_TASKS_TEAM -> {
				yield getPlayerTeam(player).map(team -> Component.text(team.getCompleteCount()))
						.orElse(defaultComponent);
			}
			case CURRENT_TASKS_PLAYER -> {
				yield getParticipant(player)
						.map(participant -> Component.text(participant.getAmountOfTaskCompleted()))
						.orElse(defaultComponent);
			}
			case CURRENT_TIME -> {
				yield getSession(player).map(session -> {
					if (session.phase() instanceof BingoGame game) {
						return GameTimer.getTimeAsComponent(game.getGameTime());
					} else {
						return defaultComponent;
					}
				}).orElse(defaultComponent);
			}
			case GAME_STATUS -> {
				//TODO: implement
				ConsoleMessenger.error("placeholder bingoreloaded_game_status is not implemented yet!");
				yield defaultComponent;
			}
			case SETTING_GAMEMODE -> {
				yield getSettings(player).map(settings -> settings.mode().asComponent())
						.orElse(defaultComponent);
			}
			case SETTING_CARDSIZE -> {
				yield getSettings(player).map(settings -> settings.size().asComponent())
						.orElse(defaultComponent);
			}
			case SETTING_CARDNAME -> {
				yield getSettings(player).map(settings -> Component.text(settings.cardName()))
						.orElse(defaultComponent);
			}
			case SETTING_KIT -> {
				yield getSettings(player).map(settings -> settings.kit().getDisplayName())
						.orElse(defaultComponent);
			}
			case SETTING_DURATION -> {
				yield getSettings(player).filter(BingoSettings::useCountdown).map(settings -> Component.text(settings.countdownDuration()))
						.orElse(defaultComponent);
			}
			case SETTING_EFFECTS -> {
				//TODO: implement
				ConsoleMessenger.error("placeholder bingoreloaded_setting_effect is not implemented yet!");
				yield defaultComponent;
			}
			case SETTING_HOTSWAP_WINSCORE -> {
				yield getSettings(player).filter(BingoSettings::useScoreAsWinCondition).map(settings -> Component.text(settings.hotswapGoal()))
						.orElse(defaultComponent);
			}
			case SETTING_HOTSWAP_EXPIRE -> {
				yield getSettings(player).map(settings -> Component.text(settings.expireHotswapTasks()))
						.orElse(defaultComponent);
			}
			case SETTING_COMPLETE_WINSCORE -> {
				yield getSettings(player).filter(BingoSettings::useScoreAsWinCondition).map(settings -> Component.text(settings.completeGoal()))
						.orElse(defaultComponent);
			}
			case SETTING_SEED -> {
				yield getSettings(player).map(settings -> Component.text(settings.seed()))
						.orElse(defaultComponent);
			}
			case SETTING_TEAMSIZE -> {
				yield getSettings(player).map(settings -> Component.text(settings.maxTeamSize()))
						.orElse(defaultComponent);
			}
			case SETTING_SEPARATE_CARDS -> {
				yield getSettings(player).map(settings -> Component.text(settings.differentCardPerTeam()))
						.orElse(defaultComponent);
			}
			case SETTING_BLITZ_START_DURATION -> {
				yield getSettings(player).filter(BingoSettings::useCountdown).map(settings -> Component.text(settings.blitzStartDuration() * 10))
						.orElse(defaultComponent);
			}
			case SETTING_BLITZ_BONUS_DURATION -> {
				yield getSettings(player).filter(BingoSettings::useCountdown).map(settings -> Component.text(settings.blitzBonusDuration() * 10))
						.orElse(defaultComponent);
			}
			case SESSION_NAME -> getPlayerSessionPlaceholder(player);
			case GAMES_WINS -> getPlayerStatPlaceholder(player, BingoStatType.WINS);
			case GAMES_LOSSES -> getPlayerStatPlaceholder(player, BingoStatType.LOSSES);
			case GAMES_PLAYED -> getPlayerStatPlaceholder(player, BingoStatType.PLAYED);
			case TASKS_COMPLETED -> getPlayerStatPlaceholder(player, BingoStatType.TASKS);
			case TASK_COMPLETED_RECORD -> getPlayerStatPlaceholder(player, BingoStatType.RECORD_TASKS);
			case ITEM_USES_WAND -> getPlayerStatPlaceholder(player, BingoStatType.WAND_USES);
		};

		if (placeholder == BingoReloadedPlaceholder.TEAM_FULL) {
			return LegacyComponentSerializer.legacySection().serialize(placeholderComponent) + "§r";
		}
		return LegacyComponentSerializer.legacySection().serialize(BingoMessage.createPhrase(getPlaceholderFormat(placeholder), placeholderComponent)) + "§r";
	}

	private Component getPlayerTeamPlaceholder(OfflinePlayer player, boolean getName, boolean getColor) {
		GameManager gameManager = bingo.getGameManager();
		Component noTeamPlaceholder = Component.empty();

		// If a player is online, we can the team from the participant object
		PlayerHandle onlinePlayer = server.getPlayerFromUniqueId(player.getUniqueId());
		if (onlinePlayer != null) {
			return gameManager.getSessionFromWorld(onlinePlayer.world())
					.map(session -> session.teamManager.getPlayerAsParticipant(onlinePlayer))
					.map(BingoParticipant::getTeam)
					.map(team -> placeholderFromTeam(team, getName, getColor))
					.orElse(noTeamPlaceholder);
		}

		// When a player is either not online or in the auto team, we have to get the team manually.
		for (String sessionName : gameManager.getSessionNames()) {
			BingoSession session = gameManager.getSession(sessionName).orElseThrow();

			for (BingoParticipant participant : session.teamManager.getParticipants()) {
				if (participant.getId().equals(player.getUniqueId()) && participant.getTeam() != null) {
					return placeholderFromTeam(participant.getTeam(), getName, getColor);
				}
			}
		}

		return noTeamPlaceholder;
	}

	private Component getPlayerStatPlaceholder(OfflinePlayer player, BingoStatType statType) {
		BingoStatData statData = new BingoStatData(server);
		return Component.text(statData.getPlayerStat(player.getUniqueId(), statType));
	}

	private Component placeholderFromTeam(@NotNull BingoTeam team, boolean getName, boolean getColor) {
		if (getColor && getName) {
			//FIXME: weird manual argument shifting is required because of how the color argument works, as we cannot easily get a standalone color...
			return BingoMessage.createPhrase(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_FULL).replace("{0}", "<" + team.getColor().toString() + ">").replace("{1}", "<0>"),
					placeholderFromTeam(team, true, false));
		}

		if (getColor) {
			return Component.empty();
//            return BingoMessage.createPhrase(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_COLOR), Component.text("<" + team.getColor().toString() + ">"));
		}
		if (getName) {
			return BingoMessage.createPhrase(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_NAME), team.getName());
		}
		return Component.empty();
	}

	private Component getPlayerSessionPlaceholder(OfflinePlayer player) {
		Component noSessionPlaceholder = Component.empty();

		return getSession(player)
				.map(session -> {
					return BingoMessage.createPhrase(getPlaceholderFormat(BingoReloadedPlaceholder.SESSION_NAME), Component.text(bingo.getGameManager().getNameOfSession(session)));
				}).orElse(noSessionPlaceholder);
	}

	private String getPlaceholderFormat(BingoReloadedPlaceholder placeholder) {
		return formatter.format(placeholder);
	}

	private Optional<BingoSession> getSession(OfflinePlayer player) {
		GameManager gameManager = bingo.getGameManager();

		// If a player is online, we can the team from the participant object
		PlayerHandle onlinePlayer = server.getPlayerFromUniqueId(player.getUniqueId());
		if (onlinePlayer != null) {
			return gameManager.getSessionFromWorld(onlinePlayer.world());
		}

		// When a player is either not online or in the auto team, we have to get the team manually.
		for (String sessionName : gameManager.getSessionNames()) {
			BingoSession session = gameManager.getSession(sessionName).orElseThrow();

			for (BingoParticipant participant : session.teamManager.getParticipants()) {
				if (participant.getId().equals(player.getUniqueId()) && participant.getTeam() != null) {
					return Optional.of(session);
				}
			}
		}
		return Optional.empty();
	}

	private Optional<BingoParticipant> getParticipant(OfflinePlayer player) {
		GameManager gameManager = bingo.getGameManager();

		// If a player is online, we can get the team from the participant object
		PlayerHandle onlinePlayer = server.getPlayerFromUniqueId(player.getUniqueId());
		if (onlinePlayer != null) {
			return gameManager.getSessionFromWorld(onlinePlayer.world())
					.map(session -> session.teamManager.getPlayerAsParticipant(onlinePlayer));
		}

		// When a player is either not online or in the auto team, we have to get the team manually.
		for (String sessionName : gameManager.getSessionNames()) {
			BingoSession session = gameManager.getSession(sessionName).orElseThrow();

			for (BingoParticipant participant : session.teamManager.getParticipants()) {
				if (participant.getId().equals(player.getUniqueId()) && participant.getTeam() != null) {
					return Optional.of(participant);
				}
			}
		}
		return Optional.empty();
	}

	private Optional<BingoTeam> getPlayerTeam(OfflinePlayer player) {
		return getParticipant(player).map(BingoParticipant::getTeam);
	}

	private Optional<BingoSettings> getSettings(OfflinePlayer player) {
		return getSession(player).map(session -> session.settingsBuilder.view());
	}
}
