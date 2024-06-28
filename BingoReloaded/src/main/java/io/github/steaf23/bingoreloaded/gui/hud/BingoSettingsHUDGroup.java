package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.playerdisplay.scoreboard.HUDRegistry;
import io.github.steaf23.playerdisplay.scoreboard.PlayerHUD;
import io.github.steaf23.playerdisplay.scoreboard.PlayerHUDGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Manager of multiple player HUDs, to show similar contents, but allows for per-player options as well
 */
public class BingoSettingsHUDGroup extends PlayerHUDGroup
{
    private final ScoreboardData.SidebarTemplate settingsBoardTemplate;

    public BingoSettingsHUDGroup(HUDRegistry registry) {
        super(registry);
        this.settingsBoardTemplate = new ScoreboardData().loadTemplate("lobby", registeredFields);

        setStatus((Component)null);
    }

    @Override
    protected PlayerHUD createHUDForPlayer(Player player) {
        return new TemplatedPlayerHUD(player, "Bingo Settings", settingsBoardTemplate);
    }

    //FIXME: write proper fix
    public void setStatus(@Nullable String status) {
        registeredFields.put("status", LegacyComponentSerializer.legacySection().deserialize(status));
        updateVisible();
    }

    public void setStatus(@Nullable Component status) {
        registeredFields.put("status", status);
        updateVisible();
    }

    public void updateSettings(@Nullable BingoSettings settings, ConfigData config) {
        //FIXME: use gamemode display name from config
        registeredFields.put("gamemode", Component.text(settings.mode().getDataName()));
        registeredFields.put("card_size", Component.text(settings.size().toString()));
        registeredFields.put("kit", settings.kit().getDisplayName());
        registeredFields.put("team_size", config.singlePlayerTeams ? Component.text("1").color(NamedTextColor.AQUA) : Component.text(Integer.toString(settings.maxTeamSize())));
        registeredFields.put("duration", settings.enableCountdown() ? Component.text(Integer.toString(settings.maxTeamSize())) : Component.text("∞").color(NamedTextColor.AQUA));

        //FIXME: i dont even know where to start translating this code to component shebang
        String effects = EffectOptionFlags.effectsToString(settings.effects());
        registeredFields.put("effects", Component.text(effects));

        updateVisible();
    }

}