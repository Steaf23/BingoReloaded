package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUD;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUDGroup;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.ObjectInputFilter;
import java.util.List;

/**
 * Manager of multiple player HUDs, to show similar contents, but allows for per-player options as well
 */
public class BingoSettingsHUDGroup extends PlayerHUDGroup
{
    private final ScoreboardData.SidebarTemplate settingsBoardTemplate;

    public BingoSettingsHUDGroup(HUDRegistry registry) {
        super(registry);
        this.settingsBoardTemplate = new ScoreboardData().loadTemplate("lobby", registeredFields);

        setStatus("");
    }

    @Override
    protected PlayerHUD createHUDForPlayer(Player player) {
        return new TemplatedPlayerHUD(player, "Bingo Settings", settingsBoardTemplate);
    }

    public void setStatus(String status) {
        registeredFields.put("status", status);
        updateVisible();
    }

    public void updateSettings(@Nullable BingoSettings settings, ConfigData config) {
        registeredFields.put("gamemode", settings.mode().displayName);
        registeredFields.put("card_size", settings.size().toString());
        registeredFields.put("kit", settings.kit().getDisplayName());
        registeredFields.put("team_size", config.singlePlayerTeams ? ChatColor.AQUA + "1" : Integer.toString(settings.maxTeamSize()));
        registeredFields.put("duration", settings.enableCountdown() ? Integer.toString(settings.countdownDuration()) : ChatColor.AQUA + "∞");

        String effects = EffectOptionFlags.effectsToString(settings.effects());
        registeredFields.put("effects", effects);

        updateVisible();
    }

}