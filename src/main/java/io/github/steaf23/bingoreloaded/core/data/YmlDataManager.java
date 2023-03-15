package io.github.steaf23.bingoreloaded.core.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class YmlDataManager
{
    private final Plugin plugin;
    private final String fileName;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public YmlDataManager(Plugin plugin, String fileName)
    {
        this.plugin = plugin;
        this.fileName = fileName;

        try
        {
            saveDefaultConfig();
        }
        catch(IllegalArgumentException exc)
        {
            Message.log(exc.getMessage());
        }
    }

    public void reloadConfig()
    {
        dataConfig = YamlConfiguration.loadConfiguration(getConfigFile());

        //create InputStream
        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream != null)
        {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig()
    {
        if (dataConfig == null)
            reloadConfig();

        return dataConfig;
    }

    public void saveConfig()
    {
        if (dataConfig == null || configFile == null) return;

        try
        {
            getConfig().save(configFile);
        }
        catch (IOException e)
        {
            Message.log(e.getMessage());
        }
    }

    public void saveDefaultConfig()
    {
        if (!getConfigFile().exists())
        {
            plugin.saveResource(fileName, false);
        }
    }

    private File getConfigFile()
    {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), fileName);

        return configFile;
    }
}
