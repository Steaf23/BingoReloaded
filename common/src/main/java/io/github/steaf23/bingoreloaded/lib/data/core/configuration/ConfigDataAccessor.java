package io.github.steaf23.bingoreloaded.lib.data.core.configuration;

import io.github.steaf23.bingoreloaded.lib.api.Extension;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;

/**
 * Specific yaml data accessor for the config.yml file provided by Bukkit.
 */
public class ConfigDataAccessor extends YamlDataStorage implements DataAccessor
{
    private final Extension extension;

    public ConfigDataAccessor(Extension extension) {
        super(extension.getConfig());
        this.extension = extension;
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
        extension.reloadConfig();
        config = extension.getConfig();
    }

    @Override
    public void saveChanges() {
        extension.saveConfig();
    }

    @Override
    public boolean isInternalReadOnly() {
        return false;
    }
}
