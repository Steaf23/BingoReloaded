package me.steven.bingoreloaded;

import org.bukkit.ChatColor;

public enum BingoGameMode
{
    REGULAR(ChatColor.GREEN + "Regular", "regular"),
    LOCKOUT(ChatColor.DARK_PURPLE + "Lockout", "lockout"),
    COMPLETE(ChatColor.DARK_AQUA + "Complete-All", "complete"),
    RUSH(ChatColor.RED + "Rush", "rush"),
    ;

    public String name;

    BingoGameMode(String name, String command)
    {
        this.name = name;
    }
}
