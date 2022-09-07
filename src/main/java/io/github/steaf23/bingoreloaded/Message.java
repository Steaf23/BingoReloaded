package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Text;

import javax.annotation.Nullable;
import javax.swing.text.JTextComponent;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Message builder class to construct and send messages to the player
// Also used for debugging and console logging
// Similar to ComponentBuilder, but can parse language yml files better.
public class Message
{
    public static final BaseComponent[] PRINT_PREFIX = new ComponentBuilder("").append("[").color(ChatColor.DARK_RED)
            .append("Bingo", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_AQUA).bold(true)
            .append("Reloaded", ComponentBuilder.FormatRetention.NONE).color(ChatColor.YELLOW).italic(true)
            .append("]", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_RED)
            .append(" ", ComponentBuilder.FormatRetention.NONE).create();

    public static final String PREFIX_STRING = new TextComponent(PRINT_PREFIX).toLegacyText();

    private String raw;
    private List<BaseComponent> args;
    private TextComponent base;
    private BaseComponent finalMessage;

    public Message(String translatePath)
    {
        this.raw = TranslationData.get(translatePath);
        this.args = new ArrayList<>();
        this.base = new TextComponent();
    }

    public Message arg(@NonNull String name)
    {
        TextComponent arg = new TextComponent();
        for (var cmp : TextComponent.fromLegacyText(name))
        {
            arg.addExtra(cmp);
        }
        args.add(arg);
        return this;
    }

    public Message color(@NonNull ChatColor color)
    {
        if (args.size() == 0)
        {
            base.setColor(color);
            return this;
        }
        args.get(args.size() - 1).setColor(color);
        return this;
    }

    public Message bold()
    {
        if (args.size() == 0)
        {
            base.setBold(true);
            return this;
        }
        args.get(args.size() - 1).setBold(true);
        return this;
    }

    public Message italic()
    {
        if (args.size() == 0)
        {
            base.setItalic(true);
            return this;
        }
        args.get(args.size() - 1).setItalic(true);
        return this;
    }

    public Message underline()
    {
        if (args.size() == 0)
        {
            base.setUnderlined(true);
            return this;
        }
        args.get(args.size() - 1).setUnderlined(true);
        return this;
    }

    public Message strikethrough()
    {
        if (args.size() == 0)
        {
            base.setStrikethrough(true);
            return this;
        }
        args.get(args.size() - 1).setStrikethrough(true);
        return this;
    }

    public Message obfuscate()
    {
        if (args.size() == 0)
        {
            base.setObfuscated(true);
            return this;
        }
        args.get(args.size() - 1).setObfuscated(true);
        return this;
    }

    public void send(Player player)
    {
        if (finalMessage == null)
            //TODO: solving placeholders should be done last, however that is quite a but more complicated
            raw = solvePlaceholders(raw, player);
            createPrefixedMessage();
        player.spigot().sendMessage(finalMessage);
    }

    public void sendAll()
    {
        Bukkit.getOnlinePlayers().forEach(p -> send(p));
    }

    public void send(BingoTeam team)
    {
        for (String pName : team.team.getEntries())
        {
            Player p = Bukkit.getPlayer(pName);
            if (p != null)
            {
                send(p);
            }
        }
    }

    public String toLegacyString()
    {
        if (finalMessage == null)
            createMessage();
        return finalMessage.toLegacyText();
    }

    public static void log(String text)
    {
        Bukkit.getLogger().info(text);
    }

    public static void log(BaseComponent text)
    {
        log(text.toPlainText());
    }

    public static void sendDebug(String text, Player player)
    {
        player.spigot().sendMessage(TextComponent.fromLegacyText(text));
    }

    public static void sendDebug(BaseComponent text, Player player)
    {
        player.spigot().sendMessage(text);
    }

    public static TextComponent[] createHoverCommandMessage(String beforeText, String commandText, String afterText, String command, String hoverText)
    {
        TextComponent message1 = new TextComponent(PREFIX_STRING + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + beforeText);
        TextComponent message2 = new TextComponent("" + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + afterText);
        TextComponent comp = new TextComponent("" + ChatColor.RED + ChatColor.ITALIC + commandText);
        comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("" + ChatColor.GREEN + ChatColor.ITALIC + hoverText).create()));

        return new TextComponent[]{message1, comp, message2};
    }

    public static TextComponent[] createHoverInfoMessage(String beforeText, String hoverableText, String afterText, String hoverText)
    {
        TextComponent message1 = new TextComponent(PREFIX_STRING + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + beforeText);
        TextComponent message2 = new TextComponent("" + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + afterText);
        TextComponent comp = new TextComponent("" + ChatColor.RED + ChatColor.ITALIC + hoverableText);
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("" + ChatColor.GREEN + ChatColor.ITALIC + hoverText).create()));

        return new TextComponent[]{message1, comp, message2};
    }

    private void createMessage()
    {
        //for any given message like "{#00bb33}Completed {0} by team {1}! At {2}" split the arguments from the message.
        String[] rawSplit = raw.split("\\{[^\\{\\}#]*\\}"); //[{#00bb33}Completed, by team, ! At]

        // convert custom hex colors to legacyText: {#00bb33} -> ChatColor.of("#00bb33")
        // convert "&" to "§" and "&&" to "&"
        for (int i = 0; i < rawSplit.length; i++)
        {
            String part = TranslationData.convertColors(rawSplit[i]);
            rawSplit[i] = part;
        }

        // keep the previous message part for format retention
        BaseComponent prevLegacy = new TextComponent();
        // for each translated part of the message
        int i = 0;
        while (i < rawSplit.length)
        {
            for (var bc : TextComponent.fromLegacyText(rawSplit[i]))
            {
                bc.copyFormatting(prevLegacy, ComponentBuilder.FormatRetention.ALL, false);
                prevLegacy = bc;
                base.addExtra(bc);
            }
            if (args.size() > i)
            {
                base.addExtra(args.get(i));
            }
            i++;
        }
        finalMessage = base;
    }

    private void createPrefixedMessage()
    {
        TextComponent prefixedBase = new TextComponent();
        for (BaseComponent c : PRINT_PREFIX)
        {
            prefixedBase.addExtra(c);
        }

        createMessage();

        prefixedBase.addExtra(base);
        finalMessage = prefixedBase;
    }

    // solve placeholders from PlaceholderAPI
    private static String solvePlaceholders(String input, Player player)
    {
        if (BingoReloaded.usesPlaceholder)
        {
            return PlaceholderAPI.setPlaceholders(player, input);
        }
        return input;
    }
}
