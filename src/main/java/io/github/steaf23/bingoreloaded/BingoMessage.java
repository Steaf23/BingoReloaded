package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;

public class BingoMessage extends Message
{
    public static final BaseComponent[] PRINT_PREFIX = new ComponentBuilder("").append("[").color(ChatColor.DARK_RED)
            .append("Bingo", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_AQUA).bold(true)
            .append("Reloaded", ComponentBuilder.FormatRetention.NONE).color(ChatColor.YELLOW).italic(true)
            .append("]", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_RED)
            .append(" ", ComponentBuilder.FormatRetention.NONE).create();

    public static final BaseComponent[] SHORT_PREFIX = new ComponentBuilder("").append("[").color(ChatColor.DARK_RED)
            .append("B", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_AQUA).bold(true)
            .append("R", ComponentBuilder.FormatRetention.NONE).color(ChatColor.YELLOW).italic(true)
            .append("]", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_RED)
            .append(" ", ComponentBuilder.FormatRetention.NONE).create();

    public static final String PREFIX_STRING = new TextComponent(PRINT_PREFIX).toLegacyText();

    public static final String PREFIX_STRING_SHORT = new TextComponent(SHORT_PREFIX).toLegacyText();

    public BingoMessage()
    {
        this("");
    }

    public BingoMessage(String translatePath)
    {
        super(translatePath);
    }
    @Override
    protected void createMessage()
    {
        TextComponent prefixedBase = new TextComponent();
        for (BaseComponent c : PRINT_PREFIX)
        {
            prefixedBase.addExtra(c);
        }

        super.createMessage();

        prefixedBase.addExtra(base);
        finalMessage = prefixedBase;
    }

    public static TextComponent[] createHoverCommandMessage(@NonNull String translatePath, @Nullable String command)
    {
        TextComponent prefix = new TextComponent(PREFIX_STRING + TranslationData.translate(translatePath + ".prefix"));
        TextComponent hoverable = new TextComponent(TranslationData.translate(translatePath + ".hoverable"));
        TextComponent hover = new TextComponent(TranslationData.translate(translatePath + ".hover"));
        TextComponent suffix = new TextComponent(TranslationData.translate(translatePath + ".suffix"));

        if (command != null)
        {
            hoverable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }
        hoverable.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(hover).create()));

        return new TextComponent[]{prefix, hoverable, suffix};
    }

    public static void log(String text)
    {
        Bukkit.getLogger().info("[BingoReloaded]: " + text);
    }

    public static void warn(String text)
    {
        Bukkit.getLogger().warning("[BingoReloaded]: " + text);
    }

    public static void error(String text)
    {
        Bukkit.getLogger().severe("[BingoReloaded]: " + text);
    }
}
