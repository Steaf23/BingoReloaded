package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.ScoreboardData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.lib.gui.menu.ValueListHUD;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Manager of multiple player HUDs, to show similar contents, but allows for per-player options as well
 */
public class BingoSettingsHUDGroup extends ValueListHUD
{
    private final ScoreboardData.SidebarTemplate settingsBoardTemplate;
    private final BingoSession session;

    public BingoSettingsHUDGroup(BingoSession session) {
        this.settingsBoardTemplate = new ScoreboardData().loadTemplate("lobby", registeredFields);
        this.session = session;

        setStatus(Component.empty());
    }

//    @Override
//    protected PlayerHUD createHUDForPlayer(Player player) {
//        return new TemplatedPlayerHUD(player, "Bingo Settings", settingsBoardTemplate);
//    }

    public void setStatus(@NotNull Component status) {
        addField("status", status.color(NamedTextColor.RED));
    }

    public void updateSettings(@NotNull BingoSettings settings, BingoConfigurationData config) {
        addField("gamemode", settings.mode().asComponent());
        addField("card_size", settings.size().asComponent());
        addField("kit", settings.kit().getDisplayName());
        addField("team_size", config.getOptionValue(BingoOptions.SINGLE_PLAYER_TEAMS) ? Component.text("1").color(NamedTextColor.AQUA) : Component.text(Integer.toString(settings.maxTeamSize())));
        addField("duration", settings.useCountdown() ? Component.text(Integer.toString(settings.countdownDuration())) : Component.text("∞").color(NamedTextColor.AQUA));
        addField("effects", EffectOptionFlags.effectsToText(settings.effects()));
        addField("seed", Component.text(settings.seed()));
        addField("goal", Component.text(settings.mode() == BingoGamemode.COMPLETE ? settings.completeGoal() : settings.hotswapGoal()));
        addField("expire_hotswap", settings.expireHotswapTasks() ? Component.text("✔").color(NamedTextColor.GREEN) : Component.text("✕").color(NamedTextColor.RED));
        addField("separate_cards", settings.differentCardPerTeam() ? Component.text("✔").color(NamedTextColor.GREEN) : Component.text("✕").color(NamedTextColor.RED));
    }

    @Override
    public void forceUpdate() {

    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return session.audiences();
    }
}