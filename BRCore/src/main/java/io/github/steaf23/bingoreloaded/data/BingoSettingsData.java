package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.BingoSettings;
import io.github.steaf23.bingoreloaded.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.util.Message;

import java.util.List;
import java.util.Set;

public class BingoSettingsData
{
    private final YmlDataManager data;

    public BingoSettingsData()
    {
        this.data = BingoReloadedCore.createYmlDataManager("presets.yml");
    }

    public BingoSettings getSettings(String name)
    {
        if (data.getConfig().contains(name))
        {
            return data.getConfig().getSerializable(name, BingoSettings.class);
        }
        return new BingoSettingsBuilder().view();
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
