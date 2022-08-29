package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MessageSender
{
    public static String PRINT_PREFIX = "" + ChatColor.DARK_RED + "[" +
            ChatColor.DARK_AQUA + ChatColor.BOLD + "Bingo" +
            ChatColor.YELLOW + ChatColor.ITALIC + "Reloaded" +
            ChatColor.DARK_RED + "]" + ChatColor.RESET + " ";

    public static void log(String message)
    {
        Bukkit.getLogger().info(message);
    }

    public static void log(BaseComponent message)
    {
        log(message.toPlainText());
    }

    public static void sendDebug(String message, Player player)
    {
        player.spigot().sendMessage(new TextComponent(message));
    }

    //Send message to all players
    public static void sendAll(String translatePath, @Nullable String... params)
    {
        Bukkit.getOnlinePlayers().forEach(p -> sendPlayer(translatePath, p, params));
    }

    // Send message to a player
    public static void sendPlayer(String translatePath, @NonNull Player player, @Nullable String... params)
    {
        String translatedString = TranslationData.get(translatePath);
        String[] messageParts = translatedString.split("(%[^%]*%)");

        TextComponent message = new TextComponent(PRINT_PREFIX);
        if (params != null)
        {
            int i = 0;
            if (messageParts.length == params.length)
            {
                for (i = 0; i < params.length; i++)
                {
                    message.addExtra(new TextComponent(messageParts[i]));
                    message.addExtra(new TranslatableComponent(params[i]));
                }
            }
            if (messageParts.length == params.length + 1)
            {
                message.addExtra(new TextComponent(messageParts[i + 1]));
            }
        }
        else
        {
            message.setText(PRINT_PREFIX + translatedString);
        }

        player.spigot().sendMessage(message);
    }

    //Send message to a team
    public static void sendTeam(String translatePath, BingoTeam team, @Nullable String... params)
    {
        for (String name : team.team.getEntries())
        {
            Player p = Bukkit.getPlayer(name);
            if (p != null)
            {
                sendPlayer(translatePath, p, params);
            }
        }
    }

    public static TextComponent[] createHoverCommandMessage(String beforeText, String commandText, String afterText, String command, String hoverText)
    {
        TextComponent message1 = new TextComponent(PRINT_PREFIX + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + beforeText);
        TextComponent message2 = new TextComponent("" + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + afterText);
        TextComponent comp = new TextComponent("" + ChatColor.RED + ChatColor.ITALIC + commandText);
        comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("" + ChatColor.GREEN + ChatColor.ITALIC + hoverText).create()));

        return new TextComponent[]{message1, comp, message2};
    }

    public static TextComponent[] createHoverInfoMessage(String beforeText, String hoverableText, String afterText, String hoverText)
    {
        TextComponent message1 = new TextComponent(PRINT_PREFIX + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + beforeText);
        TextComponent message2 = new TextComponent("" + ChatColor.GREEN + ChatColor.ITALIC + ChatColor.BOLD + afterText);
        TextComponent comp = new TextComponent("" + ChatColor.RED + ChatColor.ITALIC + hoverableText);
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("" + ChatColor.GREEN + ChatColor.ITALIC + hoverText).create()));

        return new TextComponent[]{message1, comp, message2};
    }
}
