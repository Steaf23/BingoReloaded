package io.github.steaf23.bingoreloaded.data.core.configuration;

import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Specific yaml data accessor for the config.yml file provided by Bukkit.
 */
public class ConfigDataAccessor extends YamlDataStorage implements DataAccessor
{
    private final JavaPlugin plugin;

    public ConfigDataAccessor(JavaPlugin plugin) {
        super(plugin.getConfig());
        this.plugin = plugin;
    }

    /**
     * Not needed since this is the main config file.
     * @return empty string
     */
    @Override
    public String getLocation() {
        return "";
    }

    @Override
    public String getFileExtension() {
        return ".yml";
    }

    @Override
    public void load() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    @Override
    public void saveChanges() {
        plugin.saveConfig();
    }

    @Override
    public boolean isInternalReadOnly() {
        return false;
    }
}
