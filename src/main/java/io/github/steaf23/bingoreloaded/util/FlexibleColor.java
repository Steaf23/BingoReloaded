package io.github.steaf23.bingoreloaded.util;

import org.bukkit.*;

import java.util.logging.Level;

public enum FlexibleColor
{
    BROWN("Evil Red", ChatColor.DARK_RED, DyeColor.BROWN, 12, Material.BROWN_CONCRETE, Material.BROWN_STAINED_GLASS_PANE, Material.BROWN_BANNER),
    ORANGE("Orange", ChatColor.GOLD, DyeColor.ORANGE, 1, Material.ORANGE_CONCRETE, Material.ORANGE_STAINED_GLASS_PANE, Material.ORANGE_BANNER),
    WHITE("White", ChatColor.WHITE, DyeColor.WHITE, 0, Material.WHITE_CONCRETE, Material.WHITE_STAINED_GLASS_PANE, Material.WHITE_BANNER),
    GRAY("Gray", ChatColor.DARK_GRAY, DyeColor.GRAY, 7, Material.GRAY_CONCRETE, Material.GRAY_STAINED_GLASS_PANE, Material.GRAY_BANNER),
    LIGHT_GRAY("Light Gray", ChatColor.GRAY, DyeColor.LIGHT_GRAY, 8, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.LIGHT_GRAY_BANNER),
    BLACK("Dark Gray", ChatColor.BLACK, DyeColor.BLACK, 15, Material.BLACK_CONCRETE, Material.BLACK_STAINED_GLASS_PANE, Material.BLACK_BANNER),
    BLUE("Blue", ChatColor.DARK_BLUE, DyeColor.BLUE,11, Material.BLUE_CONCRETE, Material.BLUE_STAINED_GLASS_PANE, Material.BLUE_BANNER),
    LIGHT_BLUE("Light Blue", ChatColor.BLUE, DyeColor.LIGHT_BLUE, 3, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIGHT_BLUE_BANNER),
    GREEN("Dark Lime", ChatColor.DARK_GREEN, DyeColor.GREEN, 13, Material.GREEN_CONCRETE, Material.GREEN_STAINED_GLASS_PANE, Material.GREEN_BANNER),
    LIME("Lime", ChatColor.GREEN, DyeColor.LIME, 5, Material.LIME_CONCRETE, Material.LIME_STAINED_GLASS_PANE, Material.LIME_BANNER),
    MAGENTA("Teal Magenta", ChatColor.AQUA, DyeColor.MAGENTA, 2, Material.MAGENTA_CONCRETE, Material.MAGENTA_STAINED_GLASS_PANE, Material.MAGENTA_BANNER),
    PURPLE("Purple", ChatColor.DARK_PURPLE, DyeColor.PURPLE, 10, Material.PURPLE_CONCRETE, Material.PURPLE_STAINED_GLASS_PANE, Material.PURPLE_BANNER),
    PINK("Pink", ChatColor.LIGHT_PURPLE, DyeColor.PINK, 6, Material.PINK_CONCRETE, Material.PINK_STAINED_GLASS_PANE, Material.PINK_BANNER),
    RED("Red", ChatColor.RED, DyeColor.RED, 14, Material.RED_CONCRETE, Material.RED_STAINED_GLASS_PANE, Material.RED_BANNER),
    YELLOW("Yellow", ChatColor.YELLOW, DyeColor.YELLOW, 4, Material.YELLOW_CONCRETE, Material.YELLOW_STAINED_GLASS_PANE, Material.YELLOW_BANNER),
    CYAN("Blue-Green", ChatColor.DARK_AQUA, DyeColor.CYAN, 9, Material.CYAN_CONCRETE, Material.CYAN_STAINED_GLASS_PANE, Material.CYAN_BANNER),
    ;

    public final String displayName;
    public final Color rgbColor;
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
        java.awt.Color color = chatColor.asBungee().getColor();
        this.rgbColor = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
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

    public static FlexibleColor fromChatColor(ChatColor color)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.chatColor.equals(color))
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + color.name() + "(chat color)!");
        return null;
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
