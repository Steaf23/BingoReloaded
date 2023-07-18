package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.util.InfoScoreboard;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class SettingsPreviewBoard extends InfoScoreboard
{
    private static final BaseComponent[] TITLE = new ComponentBuilder("")
            .append("Bingo", ComponentBuilder.FormatRetention.NONE).color(ChatColor.DARK_AQUA).bold(true)
            .append("Ⓡeloaded", ComponentBuilder.FormatRetention.NONE).color(ChatColor.YELLOW).italic(true)
            .append("", ComponentBuilder.FormatRetention.NONE).create();

    public SettingsPreviewBoard()
    {
        super(new TextComponent(TITLE).toLegacyText(), Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
        new TranslatedMessage(BingoTranslation.SETTINGS_UPDATED).sendAll(event.getSession());
        showSettings(event.getNewSettings());
    }

    public void showSettings(BingoSettings settings)
    {
        clearDisplay();
        setLineText(0, " ");
        setLineText(2, " ");
        setLineText(3,  ChatColor.BOLD + "Gamemode:");
        setLineText(4, " - " + settings.mode().displayName + " " + settings.size().size + "x" + settings.size().size);
        setLineText(5, ChatColor.BOLD + "Kit:");
        setLineText(6, " - " + settings.kit().getDisplayName());
        setLineText(7, ChatColor.BOLD + "Effects:");
        int idx = 8;
        if (settings.effects().size() == 0)
        {
            setLineText(idx, " - " + ChatColor.GRAY + " None");
            idx ++;
        }
        else
        {
            // Display effects in pairs of 2 per line to save space
            int effectIdx = 0;
            var effects = settings.effects().stream().toList();
            int effectCount = effects.size();
            boolean firstLine = true;
            for (int effectPair = 0; effectPair < effectCount / 2.0; effectPair++) {
                String effectNameLeft = effects.get(effectPair * 2).name;
                String prefix = firstLine ? " - " : "   ";
                if (effectCount > effectPair * 2 + 1) {
                    String effectNameRight = effects.get(effectPair * 2 + 1).name;
                    setLineText(idx, prefix + ChatColor.GRAY + effectNameLeft + ", " + effectNameRight);
                } else {
                    setLineText(idx, prefix + ChatColor.GRAY + effectNameLeft);
                }
                firstLine = false;
                idx++;
            }
        }

        setLineText(idx, ChatColor.BOLD + "Team Size:");
        setLineText(idx + 1, " - " + settings.maxTeamSize() + " players");
        if (settings.enableCountdown())
        {
            setLineText(idx + 2, ChatColor.BOLD + "Time:");
            setLineText(idx + 3, " - " + settings.countdownDuration() + " minutes");
        }
    }

    public void setStatus(String newStatus) {
        setLineText(1, ChatColor.RED + newStatus);
    }
}
