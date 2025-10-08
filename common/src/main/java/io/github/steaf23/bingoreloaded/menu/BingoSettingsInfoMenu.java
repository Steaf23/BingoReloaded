package io.github.steaf23.bingoreloaded.menu;

import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.lib.menu.InfoMenu;
import io.github.steaf23.bingoreloaded.player.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Manager of multiple player HUDs, to show similar contents, but allows for per-player options as well
 */
public class BingoSettingsInfoMenu extends InfoMenu
{
    public BingoSettingsInfoMenu() {
        setStatus(Component.empty());
    }

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
}