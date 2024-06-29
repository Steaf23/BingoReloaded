package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public enum BingoGamemode implements ComponentLike
{
    REGULAR("regular", BingoMessage.MODE_REGULAR.asPhrase()),
    LOCKOUT("lockout", BingoMessage.MODE_LOCKOUT.asPhrase()),
    COMPLETE("complete", BingoMessage.MODE_COMPLETE.asPhrase()),
    HOTSWAP("hotswap", BingoMessage.MODE_HOTSWAP.asPhrase()),
    ;

    private final String configName;
    private final Component displayName;

    BingoGamemode(String configName, Component displayName)
    {
        this.configName = configName;
        this.displayName = displayName;
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

    @Override
    public @NotNull Component asComponent() {
        return displayName;
    }
}
