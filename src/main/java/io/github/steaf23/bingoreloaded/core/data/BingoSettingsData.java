package io.github.steaf23.bingoreloaded.core.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.BingoSession;
import io.github.steaf23.bingoreloaded.core.BingoSettings;
import io.github.steaf23.bingoreloaded.core.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.util.Message;

public class BingoSettingsData
{
    private final YmlDataManager data;

    public BingoSettingsData()
    {
        this.data = new YmlDataManager(BingoReloaded.get(), "presets.yml");
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
