package me.steven.bingoreloaded;

import org.bukkit.ChatColor;

import java.util.Objects;

public enum BingoGameMode
{
    REGULAR(ChatColor.GREEN + "Regular", "regular"),
    LOCKOUT(ChatColor.DARK_PURPLE + "Lockout", "lockout"),
    COMPLETE(ChatColor.DARK_AQUA + "Complete-All", "complete"),
    ;

    public final String name;
    private final String dataName;

    BingoGameMode(String name, String dataName)
    {
        this.name = name;
        this.dataName = dataName;
    }

    public static BingoGameMode fromDataString(String data)
    {
        for (BingoGameMode mode : BingoGameMode.values())
        {
            if (mode.dataName.equals(data))
            {
                return mode;
            }
        }

        return BingoGameMode.REGULAR;
    }

    public String getDataName()
    {
        return dataName;
    }
}
