package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUD;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUDGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager of multiple player HUDs, to show similar contents, but allows for per-player options as well
 */
public class BingoSettingsHUDManager extends PlayerHUDGroup
{
    private final ScoreboardData.SidebarTemplate settingsBoardTemplate;

    public BingoSettingsHUDManager(HUDRegistry registry) {
        super(registry);
        this.settingsBoardTemplate = new ScoreboardData().loadTemplate("lobby", registeredFields);

        setStatus("");
        updateSettings(null);
    }

    @Override
    protected PlayerHUD createHUDForPlayer(Player player) {
        return new TemplatedPlayerHUD(player, "Bingo Settings", settingsBoardTemplate);
    }

    public void setStatus(String status) {
        registeredFields.put("status", status);
        updateVisible();
    }

    public void updateSettings(@Nullable BingoSettings settings) {
        if (settings == null) {
            return;
        }
        registeredFields.put("gamemode", settings.mode().displayName);
        registeredFields.put("card_size", settings.size().toString());
        registeredFields.put("kit", settings.kit().getDisplayName());
        registeredFields.put("team_size", Integer.toString(settings.maxTeamSize()));
        registeredFields.put("duration", settings.enableCountdown() ? Integer.toString(settings.countdownDuration()) : "∞");

        String effects = "";
        if (settings.effects().size() == 0)
        {
            effects = ChatColor.GRAY + "None";
        }
        else
        {
            effects = "\n";
            // Display effects in pairs of 2 per line to save space
            int effectIdx = 0;
            List<EffectOptionFlags> allEffects = settings.effects().stream().toList();
            int effectCount = allEffects.size();

            boolean firstLine = true;
            for (int effectPair = 0; effectPair < effectCount / 2.0; effectPair++) {
                String effectNameLeft = allEffects.get(effectPair * 2).name;
                String prefix = firstLine ? " - " : "   ";
                if (effectCount > effectPair * 2 + 1) {
                    String effectNameRight = allEffects.get(effectPair * 2 + 1).name;
                    effects += prefix + ChatColor.GRAY + effectNameLeft + ", " + effectNameRight + "\n";
                } else {
                    effects += prefix + ChatColor.GRAY + effectNameLeft + "\n";
                }
                firstLine = false;
            }
        }
        registeredFields.put("effects", effects);

        updateVisible();
    }

}