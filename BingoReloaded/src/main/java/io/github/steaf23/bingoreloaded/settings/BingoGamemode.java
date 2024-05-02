package io.github.steaf23.bingoreloaded.settings;

import org.bukkit.ChatColor;

public enum BingoGamemode
{
    REGULAR(ChatColor.GREEN + "Regular", "regular"),
    LOCKOUT(ChatColor.DARK_PURPLE + "Lockout", "lockout"),
    COMPLETE(ChatColor.DARK_AQUA + "Complete-All", "complete"),
    HOTSWAP(ChatColor.GOLD + "Hot-Swap", "hotswap"),
    ;

    public final String displayName;
    private final String dataName;

    BingoGamemode(String displayName, String dataName)
    {
        this.displayName = displayName;
        this.dataName = dataName;
    }

    public static BingoGamemode fromDataString(String data) {
        return fromDataString(data, false);
    }

    public static BingoGamemode fromDataString(String data, boolean strict)
    {
        for (BingoGamemode mode : BingoGamemode.values())
        {
            if (mode.dataName.equals(data))
            {
                return mode;
            }
        }

        if (strict) {
            return null;
        }
        return BingoGamemode.REGULAR;
    }


    public String getDataName()
    {
        return dataName;
    }
}
