package me.steven.bingoreloaded.cards;

import org.bukkit.ChatColor;

import java.util.NoSuchElementException;

public enum BingoGameMode
{
    REGULAR(ChatColor.GREEN + "Regular", "regular"),
    LOCKOUT(ChatColor.DARK_PURPLE + "Lockout", "lookout"),
    COMPLETE(ChatColor.DARK_AQUA + "Complete-All", "complete"),
    RUSH(ChatColor.RED + "Rush", "rush"),
    ;

    public String name;
    String command;

    BingoGameMode(String name, String command)
    {
        this.name = name;
        this.command = command;
    }

    public static BingoGameMode fromCommand(String cmd)
    {
        for (BingoGameMode mode : values())
        {
            if (mode.command.equals(cmd))
            {
                return mode;
            }
        }
        throw new NoSuchElementException("Bingo game mode with command name " + cmd + " has not been found");
    }
}
