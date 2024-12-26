package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gui.inventory.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.playerdisplay.scoreboard.HUDRegistry;
import io.github.steaf23.playerdisplay.scoreboard.PlayerHUD;
import io.github.steaf23.playerdisplay.scoreboard.PlayerHUDGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Manager of multiple player HUDs, to show similar contents, but allows for per-player options as well
 */
public class BingoSettingsHUDGroup extends PlayerHUDGroup
{
    private final ScoreboardData.SidebarTemplate settingsBoardTemplate;

    public BingoSettingsHUDGroup(HUDRegistry registry) {
        super(registry);
        this.settingsBoardTemplate = new ScoreboardData().loadTemplate("lobby", registeredFields);

        setStatus(Component.empty());
    }

    @Override
    protected PlayerHUD createHUDForPlayer(Player player) {
        return new TemplatedPlayerHUD(player, "Bingo Settings", settingsBoardTemplate);
    }

    public void setStatus(@NotNull Component status) {
        addSidebarArgument("status", status.color(NamedTextColor.RED));
        updateVisible();
    }

    public void updateSettings(@NotNull BingoSettings settings, BingoConfigurationData config) {
        addSidebarArgument("gamemode", settings.mode().asComponent());
        addSidebarArgument("card_size", settings.size().asComponent());
        addSidebarArgument("kit", settings.kit().getDisplayName());
        addSidebarArgument("team_size", config.getOptionValue(BingoOptions.SINGLE_PLAYER_TEAMS) ? Component.text("1").color(NamedTextColor.AQUA) : Component.text(Integer.toString(settings.maxTeamSize())));
        addSidebarArgument("duration", settings.useCountdown() ? Component.text(Integer.toString(settings.countdownDuration())) : Component.text("∞").color(NamedTextColor.AQUA));
        addSidebarArgument("effects", EffectOptionFlags.effectsToText(settings.effects()));
        addSidebarArgument("seed", Component.text(settings.seed()));
        addSidebarArgument("goal", Component.text(settings.mode() == BingoGamemode.COMPLETE ? settings.completeGoal() : settings.hotswapGoal()));
        addSidebarArgument("expire_hotswap", settings.expireHotswapTasks() ? Component.text("✔").color(NamedTextColor.GREEN) : Component.text("✕").color(NamedTextColor.RED));
        addSidebarArgument("separate_cards", settings.differentCardPerTeam() ? Component.text("✔").color(NamedTextColor.GREEN) : Component.text("✕").color(NamedTextColor.RED));

        updateVisible();
    }

}