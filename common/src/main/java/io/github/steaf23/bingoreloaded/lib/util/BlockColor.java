package io.github.steaf23.bingoreloaded.lib.util;

import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import net.kyori.adventure.text.format.TextColor;

public enum BlockColor
{
    BROWN("brown", TextColor.fromHexString("#734829"), ItemType.of("brown_concrete"), ItemType.of("brown_stained_glass_pane"), ItemType.of("brown_banner")),
    ORANGE("orange", TextColor.fromHexString("#f17716"), ItemType.of("orange_concrete"), ItemType.of("orange_stained_glass_pane"), ItemType.of("orange_banner")),
    WHITE("white", TextColor.fromHexString("#eaeded"), ItemType.of("white_concrete"), ItemType.of("white_stained_glass_pane"), ItemType.of("white_banner")),
    GRAY("gray", TextColor.fromHexString("#3f4548"), ItemType.of("gray_concrete"), ItemType.of("gray_stained_glass_pane"), ItemType.of("gray_banner")),
    LIGHT_GRAY("light_gray", TextColor.fromHexString("#8e8f87"), ItemType.of("light_gray_concrete"), ItemType.of("light_gray_stained_glass_pane"), ItemType.of("light_gray_banner")),
    BLACK("black", TextColor.fromHexString("#16161b"), ItemType.of("black_concrete"), ItemType.of("black_stained_glass_pane"), ItemType.of("black_banner")),
    BLUE("blue", TextColor.fromHexString("#353a9e"), ItemType.of("blue_concrete"), ItemType.of("blue_stained_glass_pane"), ItemType.of("blue_banner")),
    LIGHT_BLUE("light_blue", TextColor.fromHexString("#3cb0da"), ItemType.of("light_blue_concrete"), ItemType.of("light_blue_stained_glass_pane"), ItemType.of("light_blue_banner")),
    GREEN("green", TextColor.fromHexString("#556e1c"), ItemType.of("green_concrete"), ItemType.of("green_stained_glass_pane"), ItemType.of("green_banner")),
    LIME("lime", TextColor.fromHexString("#71ba1a"), ItemType.of("lime_concrete"), ItemType.of("lime_stained_glass_pane"), ItemType.of("lime_banner")),
    MAGENTA("magenta", TextColor.fromHexString("#be46b5"), ItemType.of("magenta_concrete"), ItemType.of("magenta_stained_glass_pane"), ItemType.of("magenta_banner")),
    PURPLE("purple", TextColor.fromHexString("#7b2bad"), ItemType.of("purple_concrete"), ItemType.of("purple_stained_glass_pane"), ItemType.of("purple_banner")),
    PINK("pink", TextColor.fromHexString("#ee90ad"), ItemType.of("pink_concrete"), ItemType.of("pink_stained_glass_pane"), ItemType.of("pink_banner")),
    RED("red", TextColor.fromHexString("#a12823"), ItemType.of("red_concrete"), ItemType.of("red_stained_glass_pane"), ItemType.of("red_banner")),
    YELLOW("yellow", TextColor.fromHexString("#f9c629"), ItemType.of("yellow_concrete"), ItemType.of("yellow_stained_glass_pane"), ItemType.of("yellow_banner")),
    CYAN("cyan", TextColor.fromHexString("#158a91"), ItemType.of("cyan_concrete"), ItemType.of("cyan_stained_glass_pane"), ItemType.of("cyan_banner")),
    ;

    public final String name;
    public final TextColor textColor;

    public final ItemType concrete;
    public final ItemType glassPane;
    public final ItemType banner;

    BlockColor(String name, TextColor textColor, ItemType concrete, ItemType glassPane, ItemType banner)
    {
        this.textColor = textColor;
        this.name = name;

        this.concrete = concrete;
        this.glassPane = glassPane;
        this.banner = banner;
    }

    public static BlockColor fromName(String name)
    {
        for (BlockColor c : BlockColor.values())
        {
            if (c.name.equals(name))
            {
                return c;
            }
        }

        ConsoleMessenger.warn("Could not find a color fitting to " + name + "!");
        return null;
    }

    //TODO: finish all colored blocks
}
