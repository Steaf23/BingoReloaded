package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.easymenulib.util.SmallCaps;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.TitlePart;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Message builder class to construct and send messages to the player
// Also used for debugging and console logging
// Similar to ComponentBuilder, but can parse language yml files better.
public class Message
{
    public static final String LOG_PREFIX = BingoTranslation.convertColors("&3&o[BingoReloaded]&r");
    protected String raw;
    protected List<Component> args;
    protected Component base;
    protected Component finalMessage;

    public Message() {
        this("");
    }

    /**
     * @param text Arguments can be supplied using {n} where n is the index of the argument in the order of when they are added to this Message
     */
    public Message(String text) {
        this.raw = text;
        this.args = new ArrayList<>();
        this.base = Component.empty();
    }

    public Message arg(@NonNull String name) {
        args.add(LegacyComponentSerializer.legacySection().deserialize(name));
        return this;
    }

    public Message arg(@NonNull Component argument) {
        args.add(argument);
        return this;
    }

    public void send(Player player) {
        if (finalMessage == null) {
            if (raw.isBlank())
                createMessage(player);
            else
                createPrefixedMessage(player);
        }
        player.sendMessage(finalMessage);
    }

    public void createPrefixedMessage(Player player) {
        TextComponent.Builder prefixedBase = Component.text()
                .append(BingoTranslation.MESSAGE_PREFIX.asComponent());

        createMessage(player);

        prefixedBase.append(finalMessage);
        finalMessage = prefixedBase.build();
    }

    /**
     * Send this message to all players in the given session's world(s).
     *
     * @param session
     */
    public void sendAll(BingoSession session) {
        if (session == null)
            return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (session.hasPlayer(p)) {
                send(p);
            }
        }
    }

    public void send(BingoTeam team) {
        team.getMembers()
                .forEach(p -> p.sessionPlayer().ifPresent(this::send));
    }

    public Component asComponent(Player player) {
        if (finalMessage == null)
            createMessage(player);
        return finalMessage;
    }

    public static void log(String text) {
        Bukkit.getConsoleSender().sendMessage(LOG_PREFIX + ": " + text);
    }

    public static void log(String text, String worldName) {
        Bukkit.getConsoleSender().sendMessage(LOG_PREFIX + "(" + worldName + "): " + text);
    }


    public static void warn(String text) {
        Bukkit.getLogger().warning("[BingoReloaded]: " + text);
    }

    public static void error(String text) {
        Bukkit.getLogger().severe("[BingoReloaded]: " + text);
    }

    public static void log(Component text) {
        Bukkit.getConsoleSender().sendMessage(text);
    }

    public static void sendDebug(String text, Player player) {
        Message.sendDebug(LegacyComponentSerializer.legacySection().deserialize(text), player);
    }

    public static void sendDebug(Component text, Player player) {
        TextComponent.Builder finalMsg = Component.text();
        finalMsg.append(Component.text(BingoTranslation.MESSAGE_PREFIX.translate()));
        finalMsg.append(text);
        player.sendMessage(finalMsg.build());
    }

    public static void sendDebugNoPrefix(Component text, Player player) {
        player.sendMessage(text);
    }

    public static void sendTitleMessage(Component title, Component subtitle, Player player) {
        player.sendTitlePart(TitlePart.TITLE, title);
        player.sendTitlePart(TitlePart.SUBTITLE, subtitle);
    }

    public static void sendAll(Component message, BingoSession session) {
        session.getPlayersInWorld().forEach(p -> p.sendMessage(message));
    }

    protected void createMessage(Player player) {
//        //for any given message like "{#00bb33}Completed {0} by team {1}! At {2}" split the arguments from the message.
//        String[] rawSplit = raw.split("\\{[^\\{\\}#@$]*\\}"); //[{#00bb33}Completed, by team, ! At]
//
//        // convert custom hex colors to legacyText: {#00bb33} -> ChatColor.of("#00bb33")
//        // convert "&" to "§" and "&&" to "&"
//        for (int i = 0; i < rawSplit.length; i++) {
//            String part = BingoTranslation.convertConfigString(rawSplit[i]);
//            rawSplit[i] = part;
//        }
//
//        // keep the previous message part for format retention
//        BaseComponent prevLegacy = new TextComponent();
//        // for each translated part of the message
//        int i = 0;
//        while (i < rawSplit.length) {
//            for (var bc : TextComponent.fromLegacyText(solvePlaceholders(rawSplit[i], player))) {
//                bc.copyFormatting(prevLegacy, ComponentBuilder.FormatRetention.NONE, false);
//                prevLegacy = bc;
//                base.addExtra(bc);
//            }
//            if (args.size() > i) {
//                base.addExtra(args.get(i));
//            }
//            i++;
//        }
//
//        if (i == 0 && args.size() > 0) {
//            for (int j = 0; j < args.size(); j++)
//                base.addExtra(args.get(j));
//        }
        finalMessage = base;
    }

    // solve placeholders from PlaceholderAPI
    protected static String solvePlaceholders(String input, Player player) {
        if (BingoReloaded.PLACEHOLDER_API_ENABLED) {
            return PlaceholderAPI.setPlaceholders(player, input);
        }
        return input;
    }


    public static Component createHoverCommandMessage(@NonNull BingoTranslation translation, @Nullable String command) {
        // Limit -1 makes it so split returns trailing empty strings
        String[] components = translation.translate().split("//", -1);

        if (components.length != 4) {
            Message.warn("Hover commands should contain 4 lines!, please check the language file");
            return Component.empty();
        }
        Component prefix = Component.text(components[0]);
        Component hoverable = Component.text(components[1]);
        Component hover = Component.text(components[2]);
        Component suffix = Component.text(components[3]);

        return createHoverCommandMessage(prefix, hoverable, hover, suffix, command);
    }

    public static Component createHoverCommandMessage(@NonNull Component prefix, @NotNull Component hoverable, @NotNull Component hover, @NotNull Component suffix, @Nullable String command) {
        if (command != null) {
            hoverable.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }
        hoverable.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, hover));

        return Component.text().append(BingoTranslation.MESSAGE_PREFIX.asComponent(prefix, hoverable, suffix)).build();
    }
}
