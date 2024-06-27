package io.github.steaf23.bingoreloaded.placeholder;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoStatData;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BingoReloadedPlaceholderExpansion extends PlaceholderExpansion
{
    private final BingoReloaded plugin;
    private final BingoPlaceholderFormatter formatter;

    public BingoReloadedPlaceholderExpansion(BingoReloaded plugin) {
        this.plugin = plugin;
        this.formatter = new BingoPlaceholderFormatter();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bingoreloaded";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        BingoReloadedPlaceholder placeholder = BingoReloadedPlaceholder.fromString(params);
        if (placeholder == null) {
            Message.error("unexpected placeholder '" + params + "' not found in bingo reloaded.");
            return null;
        }

        String rawPlaceholder = switch (placeholder) {
            case TEAM_FULL -> getPlayerTeamPlaceholder(player, true, true);
            case TEAM_COLOR -> getPlayerTeamPlaceholder(player, false, true);
            case TEAM_NAME -> getPlayerTeamPlaceholder(player, true, false);
            case CURRENT_TASKS_TEAM -> {
                BingoTeam team = getPlayerTeam(player);
                if (team == null) {
                    yield "-";
                }
                yield Integer.toString(team.getCompleteCount());
            }
            case CURRENT_TASKS_PLAYER -> {
                BingoParticipant participant = getParticipant(player);
                if (participant == null) {
                    yield "-";
                }
                yield Integer.toString(participant.getAmountOfTaskCompleted());
            }
            case CURRENT_TIME -> {
                BingoSession session = getSession(player);
                if (session == null) {
                    yield "-";
                }
                boolean running = session.isRunning();
                if (session.phase() instanceof BingoGame game) {
                    yield GameTimer.getTimeAsString(game.getGameTime());
                }
                yield "-";
            }
            case GAME_STATUS -> {
                //TODO: implement
                Message.error("placeholder bingoreloaded_game_status is not implemented yet!");
                yield "-";
            }
            case SETTING_GAMEMODE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield "-";
                }
                else {
                    //FIXME: add displayname
                    yield settings.mode().getDataName();
                }
            }
            case SETTING_CARDSIZE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield "-";
                }
                else {
                    yield settings.size().toString();
                }
            }
            case SETTING_KIT -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield "-";
                }
                else {
                    yield LegacyComponentSerializer.legacySection().serialize(settings.kit().getDisplayName());
                }
            }
            case SETTING_DURATION -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield "-";
                }
                else {
                    yield settings.enableCountdown() ? Integer.toString(settings.countdownDuration()) : "-";
                }
            }
            case SETTING_EFFECTS -> {
                //TODO: implement
                Message.error("placeholder bingoreloaded_setting_effect is not implemented yet!");
                yield "-";
            }
            case SETTING_HOTSWAP_WINSCORE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield "-";
                }
                else {
                    yield settings.enableCountdown() || settings.mode() != BingoGamemode.HOTSWAP ? "-" : Integer.toString(settings.hotswapGoal());
                }
            }
            case SETTING_SEED -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield "-";
                }
                else {
                    yield Integer.toString(settings.seed());
                }
            }
            case SETTING_TEAMSIZE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield "-";
                }
                else {
                    yield Integer.toString(settings.maxTeamSize());
                }
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
            return rawPlaceholder;
        }
        return BingoPlaceholderFormatter.createLegacyTextFromMessage(getPlaceholderFormat(placeholder), rawPlaceholder);
    }

    private String getPlayerTeamPlaceholder(OfflinePlayer player, boolean getName, boolean getColor) {
        GameManager gameManager = plugin.getGameManager();
        String noTeamPlaceholder = "";

        // If a player is online, we can the team from the participant object
        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
        if (onlinePlayer != null) {
            BingoSession session = gameManager.getSessionFromWorld(onlinePlayer.getWorld());
            BingoParticipant participant = session.teamManager.getPlayerAsParticipant(onlinePlayer);
            if (participant != null) {
                String text = placeholderFromTeam(participant.getTeam(), getName, getColor);
                return text;
            }
            return noTeamPlaceholder;
        }

        // When a player is either not online or in the auto team, we have to get the team manually.
        for (String sessionName : gameManager.getSessionNames()) {
            BingoSession session = gameManager.getSession(sessionName);
            if (session == null) {
                return noTeamPlaceholder;
            }

            for (BingoParticipant participant : session.teamManager.getParticipants()) {
                if (participant.getId().equals(player.getUniqueId()) && participant.getTeam() != null) {
                    return placeholderFromTeam(participant.getTeam(), getName, getColor);
                }
            }
        }

        return noTeamPlaceholder;
    }

    private String getPlayerStatPlaceholder(OfflinePlayer player, BingoStatType statType) {
        BingoStatData statData = new BingoStatData();
        return Integer.toString(statData.getPlayerStat(player.getUniqueId(), statType));
    }

    private String placeholderFromTeam(@NotNull BingoTeam team, boolean getName, boolean getColor) {
        if (getColor && getName) {
            return BingoPlaceholderFormatter.createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_FULL),
                    team.getColor().toString(),
                    team.getName().toString());
        }

        if (getColor) {
            return BingoPlaceholderFormatter.createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_COLOR), team.getColor().toString());
        }
        if (getName) {
            return BingoPlaceholderFormatter.createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_NAME), team.getName());
        }
        return "";
    }

    private String getPlayerSessionPlaceholder(OfflinePlayer player) {
        BingoSession session = getSession(player);
        String noSessionPlaceholder = "-";

        if (session == null) {
            return noSessionPlaceholder;
        }

        return BingoPlaceholderFormatter.createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.SESSION_NAME), plugin.getGameManager().getNameOfSession(session));
    }

    private String getPlaceholderFormat(BingoReloadedPlaceholder placeholder) {
        return formatter.format(placeholder);
    }

    private @Nullable BingoSession getSession(OfflinePlayer player) {
        GameManager gameManager = plugin.getGameManager();

        // If a player is online, we can the team from the participant object
        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
        if (onlinePlayer != null) {
            BingoSession session = gameManager.getSessionFromWorld(onlinePlayer.getWorld());
            return session;
        }

        // When a player is either not online or in the auto team, we have to get the team manually.
        for (String sessionName : gameManager.getSessionNames()) {
            BingoSession session = gameManager.getSession(sessionName);

            for (BingoParticipant participant : session.teamManager.getParticipants()) {
                if (participant.getId().equals(player.getUniqueId()) && participant.getTeam() != null) {
                    return session;
                }
            }
        }
        return null;
    }

    private @Nullable BingoParticipant getParticipant(OfflinePlayer player) {
        GameManager gameManager = plugin.getGameManager();

        // If a player is online, we can the team from the participant object
        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
        if (onlinePlayer != null) {
            BingoSession session = gameManager.getSessionFromWorld(onlinePlayer.getWorld());
            BingoParticipant participant = session.teamManager.getPlayerAsParticipant(onlinePlayer);
            return participant;
        }

        // When a player is either not online or in the auto team, we have to get the team manually.
        for (String sessionName : gameManager.getSessionNames()) {
            BingoSession session = gameManager.getSession(sessionName);

            for (BingoParticipant participant : session.teamManager.getParticipants()) {
                if (participant.getId().equals(player.getUniqueId()) && participant.getTeam() != null) {
                    return participant;
                }
            }
        }
        return null;
    }

    private @Nullable BingoTeam getPlayerTeam(OfflinePlayer player) {
        BingoParticipant participant = getParticipant(player);
        if (participant == null) {
            return null;
        }

        BingoTeam team = participant.getTeam();
        return team;
    }

    private @Nullable BingoSettings getSettings(OfflinePlayer player) {
        BingoSession session = getSession(player);
        if (session == null) {
            return null;
        }

        return session.settingsBuilder.view();
    }
}
