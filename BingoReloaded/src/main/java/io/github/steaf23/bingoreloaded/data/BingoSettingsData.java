package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

public class BingoSettingsData
{
    private final DataAccessor data = BingoReloaded.getDataAccessor("data/presets");

    public @Nullable BingoSettings getSettings(String name) {
        if (name.equals("default"))
        {
            ConsoleMessenger.error("Cannot load settings named 'default'.");
            return null;
        }

        if (data.contains(name)) {
            return data.getSerializable(name, BingoSettings.class);
        }
        else if (!getDefaultSettingsName().isEmpty()) {
            return data.getSerializable(getDefaultSettingsName(), BingoSettings.class);
        }
        return null;
    }

    public void saveSettings(String name, BingoSettings settings) {
        if (name.equals("default"))
        {
            ConsoleMessenger.error("Cannot use name 'default'.");
            return;
        }
        if (data.contains(name)) {
            ConsoleMessenger.log("Overwritten saved preset '" + name + "' with current settings");
            data.erase(name);
        } else {
            ConsoleMessenger.log("Saved preset '" + name + "'");
        }
        data.setSerializable(name, BingoSettings.class, settings);
        data.saveChanges();
    }

    public void removeSettings(String name) {
        if (name.equals("default_settings"))
        {
            ConsoleMessenger.error("Cannot remove default settings!");
            return;
        }

        // reset default_settings
        if (name.equals(getDefaultSettingsName()))
        {
            setDefaultSettings("default_settings");
        }
        ConsoleMessenger.log("Removed preset '" + name + "'");
        data.erase(name);
        data.saveChanges();
    }

    public String getDefaultSettingsName() {
        return data.getString("default", "");
    }

    public @Nullable BingoSettings getDefaultSettings() {
        if (!data.contains("default")) {
            return null;
        }

        String defaultSettingsName = getDefaultSettingsName();
        return getSettings(defaultSettingsName);
    }

    public void setDefaultSettings(String name) {
        data.setString("default", name);
        data.saveChanges();
    }

    public Set<String> getPresetNames() {
        return data.getKeys()
                .stream().filter(k -> !k.equals("default"))
                .collect(Collectors.toSet());
    }
}
