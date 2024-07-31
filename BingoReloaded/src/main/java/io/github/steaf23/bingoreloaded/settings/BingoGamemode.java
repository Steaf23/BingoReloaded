package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public enum BingoGamemode implements ComponentLike
{
    REGULAR("regular", BingoMessage.MODE_REGULAR.asPhrase(), TextColor.fromHexString("#309f14")),
    LOCKOUT("lockout", BingoMessage.MODE_LOCKOUT.asPhrase(), TextColor.fromHexString("#8138d9")),
    COMPLETE("complete", BingoMessage.MODE_COMPLETE.asPhrase(), TextColor.fromHexString("#3d6fe3")),
    HOTSWAP("hotswap", BingoMessage.MODE_HOTSWAP.asPhrase(), TextColor.fromHexString("#dd5e20")),
    ;

    private final String configName;
    private final Component displayName;
    private final TextColor color;

    BingoGamemode(String configName, Component displayName, TextColor color)
    {
        this.configName = configName;
        this.displayName = displayName;
        this.color = color;
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
        return displayName.color(color);
    }

    public TextColor getColor() {
        return color;
    }
}
