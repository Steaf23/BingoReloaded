package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.playerdisplay.scoreboard.HUDRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Created for the sake of disabling the scoreboard if desired.
 */
public class DisabledBingoSettingsHUDGroup extends BingoSettingsHUDGroup
{
    public DisabledBingoSettingsHUDGroup(HUDRegistry registry) {
        super(registry);
    }

    @Override
    public void removeAllPlayers() {
    }

    @Override
    public void removePlayer(Player player) {
    }

    @Override
    public void addPlayer(Player player) {
    }

    @Override
    public void updateSettings(@Nullable BingoSettings settings, BingoConfigurationData config) {
    }

    @Override
    public void setStatus(Component status) {
    }
}
