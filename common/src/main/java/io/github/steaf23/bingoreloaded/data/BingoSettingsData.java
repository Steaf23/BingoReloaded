package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BingoSettingsData
{
    private final DataAccessor data = BingoReloaded.getDataAccessor("data/presets");

    public @Nullable BingoSettings getSettings(String name) {
        if (name.equals("default"))
        {
            ConsoleMessenger.error("Cannot load settings named 'default'.");
            return null;
        }

        if (data.contains("presets." + name)) {
            return data.getSerializable("presets." + name, BingoSettings.class);
        }
        else if (!getDefaultSettingsName().isEmpty()) {
            return data.getSerializable("presets." + getDefaultSettingsName(), BingoSettings.class);
        }
        return null;
    }

    public void saveSettings(String name, BingoSettings settings) {
        if (name.equals("default"))
        {
            ConsoleMessenger.error("Cannot use name 'default'.");
            return;
        }
        if (data.contains("presets." + name)) {
            ConsoleMessenger.log("Overwritten saved preset '" + name + "' with current settings");
            data.erase("presets." + name);
        } else {
            ConsoleMessenger.log("Saved preset '" + name + "'");
        }
        data.setSerializable("presets." + name, BingoSettings.class, settings);
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
        data.erase("presets." + name);
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
        return new HashSet<>(data.getStorage("presets").getKeys());
    }
}
