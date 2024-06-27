package io.github.steaf23.bingoreloaded.settings;

public enum BingoGamemode
{
    REGULAR("regular"),
    LOCKOUT("lockout"),
    COMPLETE("complete"),
    HOTSWAP("hotswap"),
    ;

    private final String configName;

    BingoGamemode(String configName)
    {
        this.configName = configName;
    }

    public static BingoGamemode fromDataString(String data) {
        return fromDataString(data, false);
    }

    public static BingoGamemode fromDataString(String data, boolean strict)
    {
        for (BingoGamemode mode : BingoGamemode.values())
        {
            if (mode.configName.equals(data))
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
        return configName;
    }
}
