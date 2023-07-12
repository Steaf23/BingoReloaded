package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

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
    public static final BaseComponent[] PRINT_PREFIX = new ComponentBuilder("").append("[").color(ChatColor.DARK_RED)
            .append("Bingo", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_AQUA).bold(true)
            .append("Ⓡeloaded", ComponentBuilder.FormatRetention.NONE).color(ChatColor.YELLOW).italic(true)
            .append("]", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_RED)
            .append("", ComponentBuilder.FormatRetention.NONE).create();

    public static final BaseComponent[] SHORT_PREFIX = new ComponentBuilder("").append("[").color(ChatColor.DARK_RED)
            .append("B", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_AQUA).bold(true)
            .append("Ⓡ", ComponentBuilder.FormatRetention.NONE).color(ChatColor.YELLOW).italic(true)
            .append("]", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_RED)
            .append("", ComponentBuilder.FormatRetention.NONE).create();

    public static final String PREFIX_STRING = new TextComponent(PRINT_PREFIX).toLegacyText();

    public static final String PREFIX_STRING_SHORT = new TextComponent(SHORT_PREFIX).toLegacyText();

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

    public Message untranslated(String message) {
        raw = message;
        return this;
    }

    public Message arg(@NonNull String name) {
        if (name == null)
            return this;

        TextComponent arg = new TextComponent();
        for (var cmp : TextComponent.fromLegacyText(name)) {
            arg.addExtra(cmp);
        }
        args.add(arg);
        return this;
    }

    public Message component(@NonNull BaseComponent component) {
        if (component == null)
            return this;

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
            //TODO: solving placeholders should be done last, however that is quite a bit more complicated
            raw = solvePlaceholders(raw, player);
            createPrefixedMessage();
        }
        player.spigot().sendMessage(finalMessage);
    }

    public void createPrefixedMessage() {
        TextComponent prefixedBase = new TextComponent();
        for (BaseComponent c : PRINT_PREFIX) {
            prefixedBase.addExtra(c);
        }

        prefixedBase.addExtra(" ");

        createMessage();

        prefixedBase.addExtra(finalMessage);
        finalMessage = prefixedBase;
    }

    /**
     * Send this message to all players in the given session's world(s).
     * @param session
     */
    public void sendAll(BingoSession session) {
        if (session == null)
            return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (BingoReloaded.getWorldNameOfDimension(p.getWorld()).equals(session.worldName)) {
                send(p);
            }
        }
    }

    public void send(BingoTeam team) {
        for (String pName : team.team.getEntries()) {
            Player p = Bukkit.getPlayer(pName);
            if (p != null) {
                send(p);
            }
        }
    }

    public String toLegacyString() {
        if (finalMessage == null)
            createMessage();
        return finalMessage.toLegacyText();
    }

    public static void log(String text) {
        Bukkit.getConsoleSender().sendMessage(PREFIX_STRING.replace("Ⓡ", "R") + ": " + text);
//        BingoLogger.log("Message", text);
    }

    public static void log(String text, String worldName) {
        Bukkit.getConsoleSender().sendMessage(PREFIX_STRING.replace("Ⓡ", "R") + "(" + worldName + "): " + text);
//        BingoLogger.log("Message", "(" + worldName + "): " + text);
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
        finalMsg.addExtra(PREFIX_STRING + " ");
        finalMsg.addExtra(text);
        player.spigot().sendMessage(finalMsg);
    }

    public static void sendDebugNoPrefix(BaseComponent text, Player player) {
        player.spigot().sendMessage(text);
    }

    public static void sendDebug(BaseComponent[] text, Player player) {
        BaseComponent finalMsg = new TextComponent();
        finalMsg.addExtra(PREFIX_STRING + " ");
        TextComponent allText = new TextComponent();
        allText.setExtra(Arrays.stream(text).collect(Collectors.toList()));
        finalMsg.addExtra(allText);
        player.spigot().sendMessage(finalMsg);
    }

    public static void sendActionMessage(String message, Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent[]{new TextComponent(message)});
    }

    public static void sendActionMessage(Message message, Player player) {
        sendActionMessage(message.toLegacyString(), player);
    }

    public static void sendTitleMessage(String title, String subtitle, Player player) {
        player.sendTitle(title, subtitle, -1, -1, -1);
    }

    public static void sendTitleMessage(Message title, Message subtitle, Player player) {
        sendTitleMessage(title.toLegacyString(), subtitle.toLegacyString(), player);
    }

    protected void createMessage() {
        //for any given message like "{#00bb33}Completed {0} by team {1}! At {2}" split the arguments from the message.
        String[] rawSplit = raw.split("\\{[^\\{\\}#@]*\\}"); //[{#00bb33}Completed, by team, ! At]

        // convert custom hex colors to legacyText: {#00bb33} -> ChatColor.of("#00bb33")
        // convert "&" to "§" and "&&" to "&"
        for (int i = 0; i < rawSplit.length; i++) {
            String part = BingoTranslation.convertColors(rawSplit[i]);
            part = BingoTranslation.convertSmallCaps(part);
            rawSplit[i] = part;
        }

        // keep the previous message part for format retention
        BaseComponent prevLegacy = new TextComponent();
        // for each translated part of the message
        int i = 0;
        while (i < rawSplit.length) {
            for (var bc : TextComponent.fromLegacyText(rawSplit[i])) {
                bc.copyFormatting(prevLegacy, ComponentBuilder.FormatRetention.ALL, false);
                prevLegacy = bc;
                base.addExtra(bc);
            }
            if (args.size() > i) {
                base.addExtra(args.get(i));
            }
            i++;
        }
        finalMessage = base;
    }

    // solve placeholders from PlaceholderAPI
    protected static String solvePlaceholders(String input, Player player) {
        if (BingoReloaded.usesPlaceholderAPI) {
            return PlaceholderAPI.setPlaceholders(player, input);
        }
        return input;
    }

    public static TextComponent[] createHoverCommandMessage(@NonNull BingoTranslation translation, @Nullable String command) {
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

        if (command != null) {
            hoverable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }
        hoverable.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(hover).create()));

        return new TextComponent[]{new TextComponent(PRINT_PREFIX), prefix, hoverable, suffix};
    }
}
