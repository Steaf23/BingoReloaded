package me.steven.bingoreloaded.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.logging.Level;

public enum FlexibleColor
{
    BROWN("Evil Red", ChatColor.DARK_RED, Material.BROWN_CONCRETE, Material.BROWN_STAINED_GLASS_PANE),
    ORANGE("Orange", ChatColor.GOLD, Material.ORANGE_CONCRETE, Material.ORANGE_STAINED_GLASS_PANE),
    WHITE("White", ChatColor.WHITE, Material.WHITE_CONCRETE, Material.WHITE_STAINED_GLASS_PANE),
    GRAY("Gray", ChatColor.DARK_GRAY, Material.GRAY_CONCRETE, Material.GRAY_STAINED_GLASS_PANE),
    LIGHT_GRAY("Light Gray", ChatColor.GRAY, Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_STAINED_GLASS_PANE),
    BLACK("Dark Gray", ChatColor.BLACK, Material.BLACK_CONCRETE, Material.BLACK_STAINED_GLASS_PANE),
    BLUE("Blue", ChatColor.DARK_BLUE, Material.BLUE_CONCRETE, Material.BLUE_STAINED_GLASS_PANE),
    LIGHT_BLUE("Light Blue", ChatColor.BLUE, Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    GREEN("Dark Lime", ChatColor.DARK_GREEN, Material.GREEN_CONCRETE, Material.GREEN_STAINED_GLASS_PANE),
    LIME("Lime", ChatColor.GREEN, Material.LIME_CONCRETE, Material.LIME_STAINED_GLASS_PANE),
    MAGENTA("Teal Magenta", ChatColor.AQUA, Material.MAGENTA_CONCRETE, Material.MAGENTA_STAINED_GLASS_PANE),
    PURPLE("Purple", ChatColor.DARK_PURPLE, Material.PURPLE_CONCRETE, Material.PURPLE_STAINED_GLASS_PANE),
    PINK("Pink", ChatColor.LIGHT_PURPLE, Material.PINK_CONCRETE, Material.PINK_STAINED_GLASS_PANE),
    RED("Red", ChatColor.RED, Material.RED_CONCRETE, Material.RED_STAINED_GLASS_PANE),
    YELLOW("Yellow", ChatColor.YELLOW, Material.YELLOW_CONCRETE, Material.YELLOW_STAINED_GLASS_PANE),
    CYAN("Blue-Green", ChatColor.DARK_AQUA, Material.CYAN_CONCRETE, Material.CYAN_STAINED_GLASS_PANE),
    ;

    public final String displayName;
    public final Color rgbColor;
    public final ChatColor chatColor;
    public final Material concrete;
    public final Material wool;
    public final Material glass;
    public final Material glassPane;
    public final Material banners;
    public final Material beds;

    FlexibleColor(String displayName, ChatColor chatColor, Material concrete, Material glassPane)
    {
        java.awt.Color color = chatColor.asBungee().getColor();
        this.rgbColor = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
        this.chatColor = chatColor;
        this.displayName = displayName;
        this.concrete = concrete;
        this.glassPane = glassPane;
        this.wool = null;
        this.glass = null;
        this.banners = null;
        this.beds = null;
    }

    public static FlexibleColor fromChatColor(ChatColor color)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.chatColor.equals(color))
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + color.name() + "!");
        return null;
    }

    public static FlexibleColor fromConcrete(Material concrete)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.concrete.equals(concrete))
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + concrete.name() + "!");
        return null;
    }

    public static FlexibleColor fromGlassPane(Material glassPane)
    {
        for (FlexibleColor flexColor : FlexibleColor.values())
        {
            if (flexColor.glassPane.equals(glassPane))
                return flexColor;
        }

        Bukkit.getLogger().log(Level.WARNING, "Could not find a color fitting to " + glassPane.name() + "!");
        return null;
    }
}
