package io.github.steaf23.playerdisplay.util;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public enum BlockColor
{
    BROWN("brown", TextColor.fromHexString("#734829"), DyeColor.BROWN, Material.BROWN_CONCRETE, Material.BROWN_STAINED_GLASS_PANE, Material.BROWN_BANNER),
    ORANGE("orange", TextColor.fromHexString("#f17716"), DyeColor.ORANGE, Material.ORANGE_CONCRETE, Material.ORANGE_STAINED_GLASS_PANE, Material.ORANGE_BANNER),
    WHITE("white", TextColor.fromHexString("#eaeded"), DyeColor.WHITE, Material.WHITE_CONCRETE, Material.WHITE_STAINED_GLASS_PANE, Material.WHITE_BANNER),
    GRAY("gray", TextColor.fromHexString("#3f4548"), DyeColor.GRAY, Material.GRAY_CONCRETE, Material.GRAY_STAINED_GLASS_PANE, Material.GRAY_BANNER),
    LIGHT_GRAY("light_gray", TextColor.fromHexString("#8e8f87"), DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.LIGHT_GRAY_BANNER),
    BLACK("black", TextColor.fromHexString("#16161b"), DyeColor.BLACK, Material.BLACK_CONCRETE, Material.BLACK_STAINED_GLASS_PANE, Material.BLACK_BANNER),
    BLUE("blue", TextColor.fromHexString("#353a9e"), DyeColor.BLUE, Material.BLUE_CONCRETE, Material.BLUE_STAINED_GLASS_PANE, Material.BLUE_BANNER),
    LIGHT_BLUE("light_blue", TextColor.fromHexString("#3cb0da"), DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIGHT_BLUE_BANNER),
    GREEN("green", TextColor.fromHexString("#556e1c"), DyeColor.GREEN, Material.GREEN_CONCRETE, Material.GREEN_STAINED_GLASS_PANE, Material.GREEN_BANNER),
    LIME("lime", TextColor.fromHexString("#71ba1a"), DyeColor.LIME, Material.LIME_CONCRETE, Material.LIME_STAINED_GLASS_PANE, Material.LIME_BANNER),
    MAGENTA("magenta", TextColor.fromHexString("#be46b5"), DyeColor.MAGENTA, Material.MAGENTA_CONCRETE, Material.MAGENTA_STAINED_GLASS_PANE, Material.MAGENTA_BANNER),
    PURPLE("purple", TextColor.fromHexString("#7b2bad"), DyeColor.PURPLE, Material.PURPLE_CONCRETE, Material.PURPLE_STAINED_GLASS_PANE, Material.PURPLE_BANNER),
    PINK("pink", TextColor.fromHexString("#ee90ad"), DyeColor.PINK, Material.PINK_CONCRETE, Material.PINK_STAINED_GLASS_PANE, Material.PINK_BANNER),
    RED("red", TextColor.fromHexString("#a12823"), DyeColor.RED, Material.RED_CONCRETE, Material.RED_STAINED_GLASS_PANE, Material.RED_BANNER),
    YELLOW("yellow", TextColor.fromHexString("#f9c629"), DyeColor.YELLOW, Material.YELLOW_CONCRETE, Material.YELLOW_STAINED_GLASS_PANE, Material.YELLOW_BANNER),
    CYAN("cyan", TextColor.fromHexString("#158a91"), DyeColor.CYAN, Material.CYAN_CONCRETE, Material.CYAN_STAINED_GLASS_PANE, Material.CYAN_BANNER),
    ;

    public final String name;
    public final TextColor textColor;
    public final DyeColor dyeColor;

    public final Material concrete;
    public final Material glassPane;
    public final Material banner;

    BlockColor(String name, TextColor textColor, DyeColor dyeColor, Material concrete, Material glassPane, Material banner)
    {
        this.textColor = textColor;
        this.dyeColor = dyeColor;
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

        Bukkit.getLogger().warning("Could not find a color fitting to " + name + "!");
        return null;
    }

    public static BlockColor fromDye(DyeColor dye)
    {
        for (BlockColor flexColor : BlockColor.values())
        {
            if (flexColor.dyeColor.equals(dye))
                return flexColor;
        }

        Bukkit.getLogger().warning("Could not find a color fitting to " + dye.name() + "(dye) !");
        return null;
    }
    //TODO: finish all colored blocks
}
