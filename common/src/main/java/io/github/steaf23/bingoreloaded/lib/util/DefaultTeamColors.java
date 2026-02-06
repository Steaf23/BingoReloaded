package io.github.steaf23.bingoreloaded.lib.util;

import net.kyori.adventure.text.format.TextColor;

public enum DefaultTeamColors
{
    BROWN("brown", TextColor.fromHexString("#734829")),
    ORANGE("orange", TextColor.fromHexString("#f17716")),
    WHITE("white", TextColor.fromHexString("#eaeded")),
    GRAY("gray", TextColor.fromHexString("#3f4548")),
    LIGHT_GRAY("light_gray", TextColor.fromHexString("#8e8f87")),
    BLACK("black", TextColor.fromHexString("#16161b")),
    BLUE("blue", TextColor.fromHexString("#353a9e")),
    LIGHT_BLUE("light_blue", TextColor.fromHexString("#3cb0da")),
    GREEN("green", TextColor.fromHexString("#556e1c")),
    LIME("lime", TextColor.fromHexString("#71ba1a")),
    MAGENTA("magenta", TextColor.fromHexString("#be46b5")),
    PURPLE("purple", TextColor.fromHexString("#7b2bad")),
    PINK("pink", TextColor.fromHexString("#ee90ad")),
    RED("red", TextColor.fromHexString("#a12823")),
    YELLOW("yellow", TextColor.fromHexString("#f9c629")),
    CYAN("cyan", TextColor.fromHexString("#158a91")),
    ;

    public final String name;
    public final TextColor textColor;

    DefaultTeamColors(String name, TextColor textColor)
    {
        this.textColor = textColor;
        this.name = name;
    }

    public static DefaultTeamColors fromName(String name)
    {
        for (DefaultTeamColors c : DefaultTeamColors.values())
        {
            if (c.name.equals(name))
            {
                return c;
            }
        }

        ConsoleMessenger.warn("Could not find a color fitting to " + name + "!");
        return null;
    }
}
