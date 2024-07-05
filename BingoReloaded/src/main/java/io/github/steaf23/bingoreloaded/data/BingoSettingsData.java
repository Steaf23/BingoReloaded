package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.BingoSettingsBuilder;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

public class BingoSettingsData
{
    private final YmlDataManager data;

    public BingoSettingsData() {
        this.data = BingoReloaded.createYmlDataManager("data/presets.yml");
    }

    public @Nullable BingoSettings getSettings(String name) {
        if (name.equals("default"))
        {
            ConsoleMessenger.error("Cannot load settings named 'default'.");
            return null;
        }

        if (data.getConfig().contains(name)) {
            return data.getConfig().getSerializable(name, BingoSettings.class);
        }
        else if (!getDefaultSettingsName().isEmpty()) {
            return data.getConfig().getSerializable(getDefaultSettingsName(), BingoSettings.class);
        }
        return null;
    }

    public void saveSettings(String name, BingoSettings settings) {
        if (name.equals("default"))
        {
            ConsoleMessenger.error("Cannot use name 'default'.");
            return;
        }
        if (data.getConfig().contains(name)) {
            ConsoleMessenger.log("Overwritten saved preset '" + name + "' with current settings");
            data.getConfig().set(name, null);
        } else {
            ConsoleMessenger.log("Saved preset '" + name + "'");
        }
        data.getConfig().set(name, settings);
        data.saveConfig();
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
        data.getConfig().set(name, null);
        data.saveConfig();
    }

    public String getDefaultSettingsName() {
        return data.getConfig().get("default", "").toString();
    }

    public @Nullable BingoSettings getDefaultSettings() {
        if (!data.getConfig().contains("default")) {
            return null;
        }

        String defaultSettingsName = data.getConfig().get("default").toString();
        return getSettings(defaultSettingsName);
    }

    public void setDefaultSettings(String name) {
        data.getConfig().set("default", name);
        data.saveConfig();
    }

    public Set<String> getPresetNames() {
        return data.getConfig().getKeys(false)
                .stream().filter(k -> !k.equals("default"))
                .collect(Collectors.toSet());
    }
}
