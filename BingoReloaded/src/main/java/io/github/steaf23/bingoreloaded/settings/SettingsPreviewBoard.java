package io.github.steaf23.bingoreloaded.settings;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import io.github.steaf23.easymenulib.scoreboard.SidebarHUD;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class SettingsPreviewBoard extends SidebarHUD
{
    public SettingsPreviewBoard(HUDRegistry registry)
    {
        super(registry, BingoTranslation.SETTINGS_SCOREBOARD_TITLE.translate());
    }

    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
        new TranslatedMessage(BingoTranslation.SETTINGS_UPDATED).sendAll(event.getSession());
        showSettings(event.getNewSettings());
    }

    public void showSettings(BingoSettings settings)
    {
        clear();
        setText(0, " ");
        setText(2, " ");
        setText(3, ChatColor.BOLD + "Gamemode:");
        setText(4, " - " + settings.mode().displayName + " " + settings.size().size + "x" + settings.size().size);
        setText(5, ChatColor.BOLD + "Kit:");
        setText(6, " - " + settings.kit().getDisplayName());
        setText(7, ChatColor.BOLD + "Effects:");
        int idx = 8;
        if (settings.effects().size() == 0)
        {
            setText(idx, " - " + ChatColor.GRAY + " None");
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
                    setText(idx, prefix + ChatColor.GRAY + effectNameLeft + ", " + effectNameRight);
                } else {
                    setText(idx, prefix + ChatColor.GRAY + effectNameLeft);
                }
                firstLine = false;
                idx++;
            }
        }

        setText(idx, ChatColor.BOLD + "Team Size:");
        setText(idx + 1, " - " + settings.maxTeamSize() + " players");
        if (settings.enableCountdown())
        {
            setText(idx + 2, ChatColor.BOLD + "Time:");
            setText(idx + 3, " - " + settings.countdownDuration() + " minutes");
        }
    }

    public void setStatus(String newStatus) {
        setText(1, ChatColor.RED + newStatus);
    }
}
