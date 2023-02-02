package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.TranslationData;
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
import java.util.List;

// Message builder class to construct and send messages to the player
// Also used for debugging and console logging
// Similar to ComponentBuilder, but can parse language yml files better.
public class Message
{
    protected String raw;
    protected List<BaseComponent> args;
    protected TextComponent base;
    protected BaseComponent finalMessage;

    public Message()
    {
        this("");
    }

    public Message(String translatePath)
    {
        if (translatePath != "")
        {
            this.raw = TranslationData.translate(translatePath);
        }
        else
        {
            this.raw = "";
        }
        this.args = new ArrayList<>();
        this.base = new TextComponent();
    }

    public Message untranslated(String message)
    {
        raw = message;
        return this;
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

    public Message component(@NonNull BaseComponent component)
    {
        args.add(component);
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
        {
            //TODO: solving placeholders should be done last, however that is quite a bit more complicated
            raw = solvePlaceholders(raw, player);
            createMessage();
        }
        player.spigot().sendMessage(finalMessage);
    }

    public void sendAll(String worldName)
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

    public static void warn(String text)
    {
        Bukkit.getLogger().warning(text);
    }

    public static void error(String text)
    {
        Bukkit.getLogger().severe(text);
    }

    public static void log(BaseComponent text)
    {
        Bukkit.getLogger().info(text.toPlainText());
    }

    public static void sendDebug(String text, Player player)
    {
        player.spigot().sendMessage(TextComponent.fromLegacyText(text));
    }

    public static void sendDebug(BaseComponent text, Player player)
    {
        player.spigot().sendMessage(text);
    }

    public static void sendDebug(BaseComponent[] text, Player player)
    {
        player.spigot().sendMessage(text);
    }

    public static void sendActionMessage(String message, Player player)
    {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent[]{new TextComponent(message)});
    }

    public static void sendActionMessage(Message message, Player player)
    {
        sendActionMessage(message.toLegacyString(), player);
    }

    protected void createMessage()
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

    // solve placeholders from PlaceholderAPI
    protected static String solvePlaceholders(String input, Player player)
    {
        if (BingoReloaded.usesPlaceholder)
        {
            return PlaceholderAPI.setPlaceholders(player, input);
        }
        return input;
    }
}
