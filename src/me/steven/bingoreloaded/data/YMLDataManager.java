package me.steven.bingoreloaded.data;

import me.steven.bingoreloaded.BingoReloaded;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class YMLDataManager
{
    private final Plugin plugin;
    private final String fileName;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public YMLDataManager(String fileName)
    {
        this.plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        this.fileName = fileName;

        saveDefaultConfig();
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
            BingoReloaded.print(e.getMessage());
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
