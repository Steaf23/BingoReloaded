package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public enum FlexColor
{
    BROWN("brown", ChatColor.of("#734829"), DyeColor.BROWN, 12, Material.BROWN_CONCRETE, Material.BROWN_STAINED_GLASS_PANE, Material.BROWN_BANNER),
    ORANGE("orange", ChatColor.of("#f17716"), DyeColor.ORANGE, 1, Material.ORANGE_CONCRETE, Material.ORANGE_STAINED_GLASS_PANE, Material.ORANGE_BANNER),
    WHITE("white", ChatColor.of("#eaeded"), DyeColor.WHITE, 0, Material.WHITE_CONCRETE, Material.WHITE_STAINED_GLASS_PANE, Material.WHITE_BANNER),
    GRAY("gray", ChatColor.of("#3f4548"), DyeColor.GRAY, 7, Material.GRAY_CONCRETE, Material.GRAY_STAINED_GLASS_PANE, Material.GRAY_BANNER),
    LIGHT_GRAY("light_gray", ChatColor.of("#8e8f87"), DyeColor.LIGHT_GRAY, 8, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.LIGHT_GRAY_BANNER),
    BLACK("black", ChatColor.of("#16161b"), DyeColor.BLACK, 15, Material.BLACK_CONCRETE, Material.BLACK_STAINED_GLASS_PANE, Material.BLACK_BANNER),
    BLUE("blue", ChatColor.of("#353a9e"), DyeColor.BLUE,11, Material.BLUE_CONCRETE, Material.BLUE_STAINED_GLASS_PANE, Material.BLUE_BANNER),
    LIGHT_BLUE("light_blue", ChatColor.of("#3cb0da"), DyeColor.LIGHT_BLUE, 3, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIGHT_BLUE_BANNER),
    GREEN("green", ChatColor.of("#556e1c"), DyeColor.GREEN, 13, Material.GREEN_CONCRETE, Material.GREEN_STAINED_GLASS_PANE, Material.GREEN_BANNER),
    LIME("lime", ChatColor.of("#71ba1a"), DyeColor.LIME, 5, Material.LIME_CONCRETE, Material.LIME_STAINED_GLASS_PANE, Material.LIME_BANNER),
    MAGENTA("magenta", ChatColor.of("#be46b5"), DyeColor.MAGENTA, 2, Material.MAGENTA_CONCRETE, Material.MAGENTA_STAINED_GLASS_PANE, Material.MAGENTA_BANNER),
    PURPLE("purple", ChatColor.of("#7b2bad"), DyeColor.PURPLE, 10, Material.PURPLE_CONCRETE, Material.PURPLE_STAINED_GLASS_PANE, Material.PURPLE_BANNER),
    PINK("pink", ChatColor.of("#ee90ad"), DyeColor.PINK, 6, Material.PINK_CONCRETE, Material.PINK_STAINED_GLASS_PANE, Material.PINK_BANNER),
    RED("red", ChatColor.of("#a12823"), DyeColor.RED, 14, Material.RED_CONCRETE, Material.RED_STAINED_GLASS_PANE, Material.RED_BANNER),
    YELLOW("yellow", ChatColor.of("#f9c629"), DyeColor.YELLOW, 4, Material.YELLOW_CONCRETE, Material.YELLOW_STAINED_GLASS_PANE, Material.YELLOW_BANNER),
    CYAN("cyan", ChatColor.of("#158a91"), DyeColor.CYAN, 9, Material.CYAN_CONCRETE, Material.CYAN_STAINED_GLASS_PANE, Material.CYAN_BANNER),
    ;

    public final String name;
    public final ChatColor chatColor;
    public final DyeColor dyeColor;
    public final int nbtColor;
    public final Material concrete;
    public final Material wool;
    public final Material glass;
    public final Material glassPane;
    public final Material banner;
    public final Material beds;

    FlexColor(String name, ChatColor chatColor, DyeColor dyeColor, int nbtColor, Material concrete, Material glassPane, Material banner)
    {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.nbtColor = nbtColor;
        this.name = name;
        this.concrete = concrete;
        this.glassPane = glassPane;
        this.banner = banner;
        this.wool = null;
        this.glass = null;
        this.beds = null;
    }

    public String getTranslatedName()
    {
        String def = "" + chatColor + ChatColor.BOLD + name;
        BingoTranslation translation = BingoTranslation.getByKey("teams." + name);
        if (translation == null)
            return def;

        String translatedName = translation.translate();
        if (translatedName.length() >= 64)
        {
            return def;
        }
        return translatedName;
    }

    public static FlexColor fromName(String name)
    {
        for (FlexColor c : FlexColor.values())
        {
            if (c.name.equals(name))
            {
                return c;
            }
        }

        Message.warn("Could not find a color fitting to " + name + "!");
        return null;
    }

    public static FlexColor fromChatColor(ChatColor color)
    {
        for (FlexColor flexColor : FlexColor.values())
        {
            if (flexColor.chatColor.equals(color))
                return flexColor;
        }

        Message.warn("Could not find a color fitting to " + color + "(chat color)!");
        return null;
    }

    public static org.bukkit.Color toBukkitColor(java.awt.Color color)
    {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return Color.fromRGB(r, g, b);
    }

    public static FlexColor fromDye(DyeColor dye)
    {
        for (FlexColor flexColor : FlexColor.values())
        {
            if (flexColor.dyeColor.equals(dye))
                return flexColor;
        }

        Message.warn("Could not find a color fitting to " + dye.name() + "(dye) !");
        return null;
    }

    public static FlexColor fromConcrete(Material concrete)
    {
        for (FlexColor flexColor : FlexColor.values())
        {
            if (flexColor.concrete.equals(concrete))
                return flexColor;
        }

        Message.warn("Could not find a color fitting to " + concrete.name() + "(concrete) !");
        return null;
    }

    public static FlexColor fromGlassPane(Material glassPane)
    {
        for (FlexColor flexColor : FlexColor.values())
        {
            if (flexColor.glassPane.equals(glassPane))
                return flexColor;
        }

        Message.warn("Could not find a color fitting to " + glassPane.name() + "(glass pane) !");
        return null;
    }

    public static FlexColor fromBanner(Material banner)
    {
        for (FlexColor flexColor : FlexColor.values())
        {
            if (flexColor.banner.equals(banner))
                return flexColor;
        }

        Message.warn("Could not find a color fitting to " + banner.name() + "(banner) !");
        return null;
    }

    public static FlexColor fromNbt(int nbtColor)
    {
        for (FlexColor flexColor : FlexColor.values())
        {
            if (flexColor.nbtColor == nbtColor)
                return flexColor;
        }

        Message.warn("Could not find a color fitting to " + nbtColor + "(nbt) !");
        return null;
    }

    public static String asHex(ChatColor color)
    {
        java.awt.Color awtCol = color.getColor();
        return String.format("#%02X%02X%02X", awtCol.getRed(), awtCol.getGreen(), awtCol.getBlue());
    }
}
