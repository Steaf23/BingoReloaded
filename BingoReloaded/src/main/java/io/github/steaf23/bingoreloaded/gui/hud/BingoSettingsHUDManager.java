package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import io.github.steaf23.easymenulib.scoreboard.PlayerHUD;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager of multiple player HUDs, to show similar contents, but allows for per-player options as well
 */
public class BingoSettingsHUDManager
{
    private final List<PlayerHUD> huds;
    private final HUDRegistry registry;
    private final ScoreboardData.SidebarTemplate settingsBoardTemplate;
    private final Map<String, String> registeredFields;

    public BingoSettingsHUDManager(HUDRegistry registry) {
        this.huds = new ArrayList<>();
        this.registry = registry;
        this.registeredFields = new HashMap<>();
        this.settingsBoardTemplate = new ScoreboardData().loadTemplate("lobby", registeredFields);

        setStatus("");
        updateSettings(null);
    }

    public void setStatus(String status) {
        registeredFields.put("status", status);
        for (PlayerHUD hud : huds) {
            hud.update();
        }
    }

    public void updateSettings(@Nullable BingoSettings settings) {
        if (settings == null) {
            return;
        }
        registeredFields.put("gamemode", settings.mode().displayName);
        registeredFields.put("card_size", settings.size().toString());
        registeredFields.put("kit", settings.kit().getDisplayName());
        registeredFields.put("team_size", Integer.toString(settings.maxTeamSize()));
        registeredFields.put("duration", Integer.toString(settings.countdownDuration()));

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

        for (PlayerHUD hud : huds) {
            hud.update();
        }
    }

    public void addPlayer(Player player) {
        // Don't re-add players if they are already added
        if (huds.stream().filter(hud -> player.getUniqueId().equals(hud.getPlayerId())).count() > 0) {
            return;
        }
        PlayerHUD hud = new BingoStatusHUD(player, true, "HUD - TEST", settingsBoardTemplate);
        registry.addPlayerHUD(hud);
        huds.add(hud);
    }

    public void removePlayer(Player player) {
        registry.removePlayerHUD(player.getUniqueId());
        huds.removeIf(h -> h.getPlayerId().equals(player));
    }

    public void removeAllPlayers() {
        for (PlayerHUD hud : huds) {
            registry.removePlayerHUD(hud.getPlayerId());
        }

        huds.clear();
    }
}