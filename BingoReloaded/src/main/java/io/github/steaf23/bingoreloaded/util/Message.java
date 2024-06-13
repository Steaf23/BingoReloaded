package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import io.github.steaf23.easymenulib.util.SmallCaps;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
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
    protected List<BaseComponent> args;
    protected TextComponent base;
    protected BaseComponent finalMessage;

    public Message() {
        this("");
    }

    /**
     * @param text Arguments can be supplied using {n} where n is the index of the argument in the order of when they are added to this Message
     */
    public Message(String text) {
        this.raw = text;
        this.args = new ArrayList<>();
        this.base = new TextComponent();
    }

    public Message arg(@NonNull String name) {
        args.add(ChatComponentUtils.concatComponents(TextComponent.fromLegacyText(name)));
        return this;
    }

    public Message arg(@NonNull BaseComponent component) {
        args.add(component);
        return this;
    }

    public Message color(@NonNull ChatColor color) {
        if (args.size() == 0) {
            base.setColor(color);
            return this;
        }
        args.get(args.size() - 1).setColor(color);
        return this;
    }

    public Message bold() {
        if (args.size() == 0) {
            base.setBold(true);
            return this;
        }
        args.get(args.size() - 1).setBold(true);
        return this;
    }

    public Message italic() {
        if (args.size() == 0) {
            base.setItalic(true);
            return this;
        }
        args.get(args.size() - 1).setItalic(true);
        return this;
    }

    public Message underline() {
        if (args.size() == 0) {
            base.setUnderlined(true);
            return this;
        }
        args.get(args.size() - 1).setUnderlined(true);
        return this;
    }

    public Message strikethrough() {
        if (args.size() == 0) {
            base.setStrikethrough(true);
            return this;
        }
        args.get(args.size() - 1).setStrikethrough(true);
        return this;
    }

    public Message obfuscate() {
        if (args.size() == 0) {
            base.setObfuscated(true);
            return this;
        }
        args.get(args.size() - 1).setObfuscated(true);
        return this;
    }

    public Message smallCaps() {
        if (args.size() == 0) {
            base.setText(SmallCaps.toSmallCaps(base.getText()));
            return this;
        }
        var arg = args.get(args.size() - 1);
        if (arg instanceof TextComponent textComponent) {
            textComponent.setText(SmallCaps.toSmallCaps(textComponent.getText()));
        }
        args.set(args.size() - 1, arg);
        return this;
    }

    public void send(Player player) {
        if (finalMessage == null) {
            if (raw.isBlank())
                createMessage(player);
            else
                createPrefixedMessage(player);
        }
        player.spigot().sendMessage(finalMessage);
    }

    public void createPrefixedMessage(Player player) {
        TextComponent prefixedBase = new TextComponent();

        prefixedBase.addExtra(new TextComponent(BingoTranslation.MESSAGE_PREFIX.translate()));

        createMessage(player);

        prefixedBase.addExtra(finalMessage);
        finalMessage = prefixedBase;
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

    public String toLegacyString(Player player) {
        return asComponent(player).toLegacyText();
    }

    public BaseComponent asComponent(Player player) {
        if (finalMessage == null)
            createMessage(player);
        return finalMessage;
    }

    public BaseComponent[] asComponents(Player player) {
        return new BaseComponent[]{asComponent(player)};
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

    public static void log(BaseComponent text) {
        Bukkit.getConsoleSender().sendMessage(text.toPlainText());
    }

    public static void sendDebug(String text, Player player) {
        Message.sendDebug(TextComponent.fromLegacyText(text), player);
    }

    public static void sendDebug(BaseComponent text, Player player) {
        BaseComponent finalMsg = new TextComponent();
        finalMsg.addExtra(new TextComponent(BingoTranslation.MESSAGE_PREFIX.translate()));
        finalMsg.addExtra(text);
        player.spigot().sendMessage(finalMsg);
    }

    public static void sendDebugNoPrefix(BaseComponent text, Player player) {
        player.spigot().sendMessage(text);
    }

    public static void sendDebug(BaseComponent[] text, Player player) {
        BaseComponent finalMsg = new TextComponent();
        finalMsg.addExtra(new TextComponent(BingoTranslation.MESSAGE_PREFIX.translate()));
        TextComponent allText = new TextComponent();
        allText.setExtra(Arrays.stream(text).collect(Collectors.toList()));
        finalMsg.addExtra(allText);
        player.spigot().sendMessage(finalMsg);
    }

    public static void sendActionMessage(String message, Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent[]{new TextComponent(message)});
    }

    public static void sendActionMessage(Message message, Player player) {
        sendActionMessage(message.toLegacyString(player), player);
    }

    public static void sendTitleMessage(String title, String subtitle, Player player) {
        player.sendTitle(title, subtitle, -1, -1, -1);
    }

    public static void sendTitleMessage(Message title, Message subtitle, Player player) {
        sendTitleMessage(title.toLegacyString(player), subtitle.toLegacyString(player), player);
    }

    protected void createMessage(Player player) {
        //for any given message like "{#00bb33}Completed {0} by team {1}! At {2}" split the arguments from the message.
        String[] rawSplit = raw.split("\\{[^\\{\\}#@$]*\\}"); //[{#00bb33}Completed, by team, ! At]

        // convert custom hex colors to legacyText: {#00bb33} -> ChatColor.of("#00bb33")
        // convert "&" to "§" and "&&" to "&"
        for (int i = 0; i < rawSplit.length; i++) {
            String part = BingoTranslation.convertConfigString(rawSplit[i]);
            rawSplit[i] = part;
        }

        // keep the previous message part for format retention
        BaseComponent prevLegacy = new TextComponent();
        // for each translated part of the message
        int i = 0;
        while (i < rawSplit.length) {
            for (var bc : TextComponent.fromLegacyText(solvePlaceholders(rawSplit[i], player))) {
                bc.copyFormatting(prevLegacy, ComponentBuilder.FormatRetention.NONE, false);
                prevLegacy = bc;
                base.addExtra(bc);
            }
            if (args.size() > i) {
                base.addExtra(args.get(i));
            }
            i++;
        }

        if (i == 0 && args.size() > 0) {
            for (int j = 0; j < args.size(); j++)
                base.addExtra(args.get(j));
        }
        finalMessage = base;
    }

    // solve placeholders from PlaceholderAPI
    protected static String solvePlaceholders(String input, Player player) {
        if (BingoReloaded.PLACEHOLDER_API_ENABLED) {
            return PlaceholderAPI.setPlaceholders(player, input);
        }
        return input;
    }


    public static BaseComponent[] createHoverCommandMessage(@NonNull BingoTranslation translation, @Nullable String command) {
        // Limit -1 makes it so split returns trailing empty strings
        String[] components = translation.translate().split("//", -1);

        if (components.length != 4) {
            Message.warn("Hover commands should contain 4 lines!, please check the language file");
            return new TextComponent[]{};
        }
        TextComponent prefix = new TextComponent(components[0]);
        TextComponent hoverable = new TextComponent(components[1]);
        TextComponent hover = new TextComponent(components[2]);
        TextComponent suffix = new TextComponent(components[3]);

        return createHoverCommandMessage(prefix, hoverable, hover, suffix, command);
    }

    public static BaseComponent[] createHoverCommandMessage(@NonNull BaseComponent prefix, @NotNull BaseComponent hoverable, @NotNull BaseComponent hover, @NotNull BaseComponent suffix, @Nullable String command) {
        if (command != null) {
            hoverable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }
        hoverable.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(hover).create()));

        return new BaseComponent[]{new TextComponent(BingoTranslation.MESSAGE_PREFIX.translate()), prefix, hoverable, suffix};
    }
}
