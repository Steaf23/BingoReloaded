package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.util.Message;

import java.util.Set;

public class BingoSettingsData
{
    private final YmlDataManager data;

    public BingoSettingsData()
    {
        this.data = BingoReloaded.createYmlDataManager("data/presets.yml");
    }

    public BingoSettings getSettings(String name)
    {
        if (data.getConfig().contains(name))
        {
            return data.getConfig().getSerializable(name, BingoSettings.class);
        }
        return BingoSettings.getDefaultSettings();
    }

    public void saveSettings(String name, BingoSettings settings)
    {
        if (data.getConfig().contains(name))
        {
            Message.log("Overwritten saved preset '" + name + "'");
            data.getConfig().set(name, null);
        }
        data.getConfig().set(name, settings);
        data.saveConfig();
    }

    public void removeSettings(String name)
    {
        Message.log("Removed preset '" + name + "'");
        data.getConfig().set(name, null);
        data.saveConfig();
    }

    public Set<String> getPresetNames()
    {
        return data.getConfig().getKeys(false);
    }
}
