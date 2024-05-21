package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoStatData;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class BingoReloadedPlaceholderExpansion extends PlaceholderExpansion
{
    enum BingoReloadedPlaceholder {
        //current match information
        TEAM_FULL("team_full"),
        TEAM_COLOR("team_color"),
        TEAM_NAME("team_name"),
        CURRENT_TASKS_TEAM("current_tasks_team"),
        CURRENT_TASKS_PLAYER("current_tasks_player"),
        CURRENT_TIME("current_time"),
        GAME_STATUS("game_status"),
        // current settings information
        SETTING_GAMEMODE("setting_gamemode"),
        SETTING_CARDSIZE("setting_cardsize"),
        SETTING_KIT("setting_kit"),
        SETTING_DURATION("setting_duration"),
        SETTING_EFFECTS("setting_effects"),
        SETTING_HOTSWAP_WINSCORE("setting_hotswap_winscore"),
        SETTING_SEED("setting_seed"),
        SETTING_TEAMSIZE("setting_teamsize"),

        // player lifetime information
        SESSION_NAME("session_name"),
        GAMES_WINS("games_won"),
        GAMES_LOSSES("games_lost"),
        GAMES_PLAYED("games_played"),
        TASKS_COMPLETED("tasks_completed"),
        TASK_COMPLETED_RECORD("tasks_completed_record"),
        ITEM_USES_WAND("item_uses_wand");

        private final String placeholderName;

        BingoReloadedPlaceholder(String placeholderName) {
            this.placeholderName = placeholderName;
        }

        public static @Nullable BingoReloadedPlaceholder fromString(String name) {
            return Arrays.stream(values()).filter(p -> p.placeholderName.equals(name)).findFirst().orElse(null);
        }
    }

    private final BingoReloaded plugin;
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("placeholders.yml");
    private final BingoStatData statData;

    public BingoReloadedPlaceholderExpansion(BingoReloaded plugin) {
        this.plugin = plugin;
        this.statData = new BingoStatData();
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
        if (params == null) {
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
                    yield Long.toString(game.getGameTime());
                }
                yield "-";
            }
            case GAME_STATUS -> {
                //TODO: implement
                yield "-";
            }
            case SETTING_GAMEMODE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield "-";
                }
                else {
                    yield settings.mode().displayName;
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
                    yield settings.kit().getDisplayName();
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
                yield "-";
            }
            case SETTING_HOTSWAP_WINSCORE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield "-";
                }
                else {
                    yield settings.enableCountdown() ? "-" : Integer.toString(settings.hotswapGoal());
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
        return createLegacyTextFromMessage(getPlaceholderFormat(placeholder), rawPlaceholder);
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
                Message.log(text);
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

    private String placeholderFromTeam(@NotNull BingoTeam team, boolean getName, boolean getColor) {
        String result = "";
        if (getColor && getName) {
            return createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_FULL),
                    placeholderFromTeam(team, true, false),
                    placeholderFromTeam(team, false, true));
        }

        if (getColor) {
            return team.getColor().toString();
        }
        if (getName) {
            return team.getName().toString();
        }
        return "";
    }

    private String getPlayerSessionPlaceholder(OfflinePlayer player) {
        BingoSession session = getSession(player);
        String noSessionPlaceholder = "-";

        if (session == null) {
            return noSessionPlaceholder;
        }

        return createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.SESSION_NAME), plugin.getGameManager().getNameOfSession(session));
    }

    public String getPlayerStatPlaceholder(OfflinePlayer player, BingoStatType statType) {
        return Integer.toString(statData.getPlayerStat(player.getUniqueId(), statType));
    }

    private String getPlaceholderFormat(BingoReloadedPlaceholder placeholder) {
        String format = data.getConfig().getString("placeholders." + placeholder.placeholderName + ".format", "");
        return format;
    }

    private String createLegacyTextFromMessage(String message, String... args) {
        //for any given message like "{#00bb33}Completed {0} by team {1}! At {2}" split the arguments from the message.
        String[] rawSplit = message.split("\\{[^\\{\\}#@]*\\}"); //[{#00bb33}Completed, by team, ! At]

        // convert custom hex colors to legacyText: {#00bb33} -> ChatColor.of("#00bb33")
        // convert "&" to "§" and "&&" to "&"
        for (int i = 0; i < rawSplit.length; i++) {
            String part = BingoTranslation.convertConfigString(rawSplit[i]);
            rawSplit[i] = part;
        }

        String finalMessage = "";
        // keep the previous message part for format retention
        BaseComponent prevLegacy = new TextComponent();
        // for each translated part of the message
        int i = 0;
        while (i < rawSplit.length) {
            finalMessage += rawSplit[i];
            if (args.length > i) {
                finalMessage += args[i];
            }
            i++;
        }

        if (i == 0 && args.length > 0) {
            for (int j = 0; j < args.length; j++)
            finalMessage += args[j];
        }
        return finalMessage;
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
