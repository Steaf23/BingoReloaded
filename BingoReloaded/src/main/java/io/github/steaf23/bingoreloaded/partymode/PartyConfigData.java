package io.github.steaf23.brpartymode;

import io.github.steaf23.bingoreloaded.data.ConfigData;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.ObjectInputFilter;

public class PartyConfigData extends ConfigData
{
    public String worldName;

    @Override
    public void loadConfig(FileConfiguration config)
    {
        super.loadConfig(config);

        this.worldName = config.getString("worldName", "");
    }
}
