package io.github.steaf23.bingoreloaded.placeholder;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoStatData;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.Extension;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BingoReloadedPlaceholderExpansion extends PlaceholderExpansion
{
    private final BingoReloaded extension;
    private final BingoPlaceholderFormatter formatter;

    public BingoReloadedPlaceholderExpansion(BingoReloaded extension) {
        this.extension = extension;
        this.formatter = new BingoPlaceholderFormatter();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bingoreloaded";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", extension.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return extension.getPluginMeta().getVersion();
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

        Component defaultComponent = Component.text("-");

        Component placeholderComponent = switch (placeholder) {
            case CREATED_SESSION -> {
                String sessionName = params.replace(BingoReloadedPlaceholder.CREATED_SESSION.getName(), "");

                BingoSession session = extension.getGameManager().getSession(sessionName);
                if (session == null) {
                    yield Component.empty();
                }
                yield Component.text(sessionName);
            }
            case COUNT_SESSION_PLAYERS -> {
                String sessionName = params.replace(BingoReloadedPlaceholder.COUNT_SESSION_PLAYERS.getName(), "");

                BingoSession session = extension.getGameManager().getSession(sessionName);
                if (session == null) {
                    yield Component.empty();
                }
                yield Component.text(session.teamManager.getParticipants().size());
            }
            case TEAM_FULL -> getPlayerTeamPlaceholder(player, true, true);
            case TEAM_COLOR -> getPlayerTeamPlaceholder(player, false, true);
            case TEAM_NAME -> getPlayerTeamPlaceholder(player, true, false);
            case CURRENT_TASKS_TEAM -> {
                BingoTeam team = getPlayerTeam(player);
                if (team == null) {
                    yield defaultComponent;
                }
                yield Component.text(team.getCompleteCount());
            }
            case CURRENT_TASKS_PLAYER -> {
                BingoParticipant participant = getParticipant(player);
                if (participant == null) {
                    yield defaultComponent;
                }
                yield Component.text(participant.getAmountOfTaskCompleted());
            }
            case CURRENT_TIME -> {
                BingoSession session = getSession(player);
                if (session == null) {
                    yield defaultComponent;
                }
                if (session.phase() instanceof BingoGame game) {
                    yield GameTimer.getTimeAsComponent(game.getGameTime());
                }
                yield defaultComponent;
            }
            case GAME_STATUS -> {
                //TODO: implement
                ConsoleMessenger.error("placeholder bingoreloaded_game_status is not implemented yet!");
                yield defaultComponent;
            }
            case SETTING_GAMEMODE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield settings.mode().asComponent();
                }
            }
            case SETTING_CARDSIZE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield settings.size().asComponent();
                }
            }
            case SETTING_CARDNAME -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield Component.text(settings.card());
                }
            }
            case SETTING_KIT -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield settings.kit().getDisplayName();
                }
            }
            case SETTING_DURATION -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield settings.useCountdown() ? Component.text(settings.countdownDuration()) : defaultComponent;
                }
            }
            case SETTING_EFFECTS -> {
                //TODO: implement
                ConsoleMessenger.error("placeholder bingoreloaded_setting_effect is not implemented yet!");
                yield defaultComponent;
            }
            case SETTING_HOTSWAP_WINSCORE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield settings.useScoreAsWinCondition() ? Component.text(settings.hotswapGoal()) : defaultComponent;
                }
            }
            case SETTING_HOTSWAP_EXPIRE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield Component.text(settings.expireHotswapTasks());
                }
            }
            case SETTING_COMPLETE_WINSCORE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield settings.useScoreAsWinCondition() ? Component.text(settings.completeGoal()) : defaultComponent;
                }
            }
            case SETTING_SEED -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield Component.text(settings.seed());
                }
            }
            case SETTING_TEAMSIZE -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield Component.text(settings.maxTeamSize());
                }
            }
            case SETTING_SEPARATE_CARDS -> {
                BingoSettings settings = getSettings(player);
                if (settings == null) {
                    yield defaultComponent;
                }
                else {
                    yield Component.text(settings.differentCardPerTeam());
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
            return LegacyComponentSerializer.legacySection().serialize(placeholderComponent) + "§r";
        }
        return LegacyComponentSerializer.legacySection().serialize(BingoMessage.createPhrase(getPlaceholderFormat(placeholder), placeholderComponent)) + "§r";
    }

    private Component getPlayerTeamPlaceholder(OfflinePlayer player, boolean getName, boolean getColor) {
        GameManager gameManager = extension.getGameManager();
        Component noTeamPlaceholder = Component.empty();

        // If a player is online, we can the team from the participant object
        PlayerHandle onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
        if (onlinePlayer != null) {
            BingoSession session = gameManager.getSessionFromWorld(onlinePlayer.getWorld());
            if (session == null) {
                return noTeamPlaceholder;
            }
            BingoParticipant participant = session.teamManager.getPlayerAsParticipant(onlinePlayer);
            if (participant == null || participant.getTeam() == null) {
                return noTeamPlaceholder;
            }
            return placeholderFromTeam(participant.getTeam(), getName, getColor);
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

    private Component getPlayerStatPlaceholder(OfflinePlayer player, BingoStatType statType) {
        BingoStatData statData = new BingoStatData();
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
        BingoSession session = getSession(player);
        Component noSessionPlaceholder = Component.empty();

        if (session == null) {
            return noSessionPlaceholder;
        }

        return BingoMessage.createPhrase(getPlaceholderFormat(BingoReloadedPlaceholder.SESSION_NAME), Component.text(plugin.getGameManager().getNameOfSession(session)));
    }

    private String getPlaceholderFormat(BingoReloadedPlaceholder placeholder) {
        return formatter.format(placeholder);
    }

    private @Nullable BingoSession getSession(OfflinePlayer player) {
        GameManager gameManager = extension.getGameManager();

        // If a player is online, we can the team from the participant object
        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
        if (onlinePlayer != null) {
            return gameManager.getSessionFromWorld(onlinePlayer.getWorld());
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
        GameManager gameManager = extension.getGameManager();

        // If a player is online, we can the team from the participant object
        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
        if (onlinePlayer != null) {
            BingoSession session = gameManager.getSessionFromWorld(onlinePlayer.getWorld());
            if (session == null) {
                return null;
            }
            return session.teamManager.getPlayerAsParticipant(onlinePlayer);
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

        return participant.getTeam();
    }

    private @Nullable BingoSettings getSettings(OfflinePlayer player) {
        BingoSession session = getSession(player);
        if (session == null) {
            return null;
        }

        return session.settingsBuilder.view();
    }
}
