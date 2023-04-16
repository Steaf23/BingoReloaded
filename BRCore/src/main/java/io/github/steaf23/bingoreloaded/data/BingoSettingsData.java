package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloadedCore;
import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.BingoSettings;
import io.github.steaf23.bingoreloaded.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.util.Message;

public class BingoSettingsData
{
    private final YmlDataManager data;

    public BingoSettingsData()
    {
        this.data = BingoReloadedCore.createYmlDataManager("presets.yml");
    }

    public BingoSettings getSettings(String name, BingoSession session)
    {
        if (data.getConfig().contains(name))
        {
            return data.getConfig().getSerializable(name, BingoSettings.class);
        }
        return new BingoSettingsBuilder(session).view();
    }

    public void saveSettings(String name, BingoSettingsBuilder settings)
    {
        if (data.getConfig().contains(name))
        {
            Message.log("Overwritten saved preset '" + name + "'");
            data.getConfig().set(name, null);
        }
        data.getConfig().set(name, settings);
        data.saveConfig();
    }
}
