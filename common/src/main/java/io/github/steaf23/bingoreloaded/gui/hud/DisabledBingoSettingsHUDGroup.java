package io.github.steaf23.bingoreloaded.gui.hud;

import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created for the sake of disabling the scoreboard if desired.
 */
public class DisabledBingoSettingsHUDGroup extends BingoSettingsHUDGroup
{

    @Override
    public void updateSettings(@Nullable BingoSettings settings, BingoConfigurationData config) {
    }

    @Override
    public void setStatus(@NotNull Component status) {
    }
}
