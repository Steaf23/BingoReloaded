package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.Arrays;

public class BingoReloadedPlaceholderExpansion extends PlaceholderExpansion
{
    enum BingoReloadedPlaceholder {
        TEAM_FULL("team_full"),
        TEAM_COLOR("team_color"),
        TEAM_NAME("team_name"),
        SESSION_NAME("session_name");

        private final String placeholderName;
        BingoReloadedPlaceholder(String placeholderName) {
            this.placeholderName = placeholderName;
        }
    }

    private final BingoReloaded plugin;
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("placeholders.yml");

    public BingoReloadedPlaceholderExpansion(BingoReloaded plugin) {
        this.plugin = plugin;
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
        return switch (params) {
            case "team_name" -> getPlayerTeamPlaceholder(player, true, false);
            case "team_color" -> getPlayerTeamPlaceholder(player, false, true);
            case "team_full" -> getPlayerTeamPlaceholder(player, true, true);
            case "session_name" -> getPlayerSessionPlaceholder(player);
            default -> {
                Message.error("unexpected placeholder '" + params + "' not found in bingo reloaded.");
                yield null;
            }
        };
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

    private String placeholderFromTeam(BingoTeam team, boolean getName, boolean getColor) {
        String result = "";
        if (getColor && getName) {
            return createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_FULL),
                    team.getColor().toString(),
                    team.getName().toString());
        }

        if (getColor) {
            return createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_COLOR), team.getColor().toString());
        }
        if (getName) {
            return createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.TEAM_NAME), team.getName().toString());
        }
        return "";
    }

    private String getPlayerSessionPlaceholder(OfflinePlayer player) {
        GameManager gameManager = plugin.getGameManager();
        String noSessionPlaceholder = "";

        // If a player is online, we can the team from the participant object
        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
        if (onlinePlayer != null) {
            BingoSession session = gameManager.getSessionFromWorld(onlinePlayer.getWorld());
            return createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.SESSION_NAME), gameManager.getNameOfSession(session));
        }

        // When a player is either not online or in the auto team, we have to get the team manually.
        for (String sessionName : gameManager.getSessionNames()) {
            BingoSession session = gameManager.getSession(sessionName);
            if (session == null) {
                return noSessionPlaceholder;
            }

            for (BingoParticipant participant : session.teamManager.getParticipants()) {
                if (participant.getId().equals(player.getUniqueId()) && participant.getTeam() != null) {
                    return createLegacyTextFromMessage(getPlaceholderFormat(BingoReloadedPlaceholder.SESSION_NAME), sessionName);
                }
            }
        }
        return noSessionPlaceholder;
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
}
