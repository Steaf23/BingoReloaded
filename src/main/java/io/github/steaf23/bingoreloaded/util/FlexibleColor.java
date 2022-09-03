package io.github.steaf23.bingoreloaded.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.awt.*;
import java.util.logging.Level;

public enum FlexibleColor
{
    BROWN("Brown", ChatColor.of("#734829"), DyeColor.BROWN, 12, Material.BROWN_CONCRETE, Material.BROWN_STAINED_GLASS_PANE, Material.BROWN_BANNER),
    ORANGE("Orange", ChatColor.of("#f17716"), DyeColor.ORANGE, 1, Material.ORANGE_CONCRETE, Material.ORANGE_STAINED_GLASS_PANE, Material.ORANGE_BANNER),
    WHITE("White", ChatColor.of("#eaeded"), DyeColor.WHITE, 0, Material.WHITE_CONCRETE, Material.WHITE_STAINED_GLASS_PANE, Material.WHITE_BANNER),
    GRAY("Gray", ChatColor.of("#3f4548"), DyeColor.GRAY, 7, Material.GRAY_CONCRETE, Material.GRAY_STAINED_GLASS_PANE, Material.GRAY_BANNER),
    LIGHT_GRAY("Light Gray", ChatColor.of("#8e8f87"), DyeColor.LIGHT_GRAY, 8, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.LIGHT_GRAY_BANNER),
    BLACK("Black", ChatColor.of("#16161b"), DyeColor.BLACK, 15, Material.BLACK_CONCRETE, Material.BLACK_STAINED_GLASS_PANE, Material.BLACK_BANNER),
    BLUE("Blue", ChatColor.of("#353a9e"), DyeColor.BLUE,11, Material.BLUE_CONCRETE, Material.BLUE_STAINED_GLASS_PANE, Material.BLUE_BANNER),
    LIGHT_BLUE("Light Blue", ChatColor.of("#3cb0da"), DyeColor.LIGHT_BLUE, 3, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIGHT_BLUE_BANNER),
    GREEN("Green", ChatColor.of("#556e1c"), DyeColor.GREEN, 13, Material.GREEN_CONCRETE, Material.GREEN_STAINED_GLASS_PANE, Material.GREEN_BANNER),
    LIME("Lime", ChatColor.of("#71ba1a"), DyeColor.LIME, 5, Material.LIME_CONCRETE, Material.LIME_STAINED_GLASS_PANE, Material.LIME_BANNER),
    MAGENTA("Magenta", ChatColor.of("#be46b5"), DyeColor.MAGENTA, 2, Material.MAGENTA_CONCRETE, Material.MAGENTA_STAINED_GLASS_PANE, Material.MAGENTA_BANNER),
    PURPLE("Purple", ChatColor.of("#7b2bad"), DyeColor.PURPLE, 10, Material.PURPLE_CONCRETE, Material.PURPLE_STAINED_GLASS_PANE, Material.PURPLE_BANNER),
    PINK("Pink", ChatColor.of("#ee90ad"), DyeColor.PINK, 6, Material.PINK_CONCRETE, Material.PINK_STAINED_GLASS_PANE, Material.PINK_BANNER),
    RED("Red", ChatColor.of("#a12823"), DyeColor.RED, 14, Material.RED_CONCRETE, Material.RED_STAINED_GLASS_PANE, Material.RED_BANNER),
    YELLOW("Yellow", ChatColor.of("#f9c629"), DyeColor.YELLOW, 4, Material.YELLOW_CONCRETE, Material.YELLOW_STAINED_GLASS_PANE, Material.YELLOW_BANNER),
    CYAN("Cyan", ChatColor.of("#158a91"), DyeColor.CYAN, 9, Material.CYAN_CONCRETE, Material.CYAN_STAINED_GLASS_PANE, Material.CYAN_BANNER),
    ;

    public final String displayName;
    public final ChatColor chatColor;
    public final DyeColor dyeColor;
    public final int nbtColor;
    public final Material concrete;
    public final Material wool;
    public final Material glass;
    public final Material glassPane;
    public final Material banner;
    public final Material beds;

    FlexibleColor(String displayName, ChatColor chatColor, DyeColor dyeColor, int nbtColor, Material concrete, Material glassPane, Material banner)
    {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.nbtColor = nbtColor;
        this.displayName = displayName;
        this.concrete = concrete;
        this.glassPane = glassPane;
        this.banner = banner;
        this.wool = null;
        this.glass = null;
        this.beds = null;
    }

    public static FlexibleColor fromDisplayName(String displayName)
    {
        for (FlexibleColor c : FlexibleColor.values())
        {
            if (c.displayName.equals(displayName))
            {
                return c;
            }
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + displayName + "!");
        return null;
    }

    public static FlexibleColor fromChatColor(ChatColor color)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.chatColor.equals(color))
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + color + "(chat color)!");
        return null;
    }

    public static org.bukkit.Color toBukkitColor(java.awt.Color color)
    {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return Color.fromRGB(r, g, b);
    }

    public static FlexibleColor fromDye(DyeColor dye)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.dyeColor.equals(dye))
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + dye.name() + "(dye) !");
        return null;
    }

    public static FlexibleColor fromConcrete(Material concrete)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.concrete.equals(concrete))
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + concrete.name() + "(concrete) !");
        return null;
    }

    public static FlexibleColor fromGlassPane(Material glassPane)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.glassPane.equals(glassPane))
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + glassPane.name() + "(glass pane) !");
        return null;
    }

    public static FlexibleColor fromBanner(Material banner)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.banner.equals(banner))
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + banner.name() + "(banner) !");
        return null;
    }

    public static FlexibleColor fromNbt(int nbtColor)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.nbtColor == nbtColor)
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + nbtColor + "(nbt) !");
        return null;
    }
}
