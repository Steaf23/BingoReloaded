package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.api.platform.PaperResources;

/**
 * Specific yaml data accessor for the config.yml file provided by Bukkit.
 */
public class ConfigDataAccessor extends YamlDataStorage implements DataAccessor
{
    private final PaperResources resources;

    public ConfigDataAccessor(PaperResources resources) {
        super(resources.getConfig());
        this.resources = resources;
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
        resources.reloadConfig();
        config = resources.getConfig();
    }

    @Override
    public void saveChanges() {
        resources.saveConfig();
    }

    @Override
    public boolean isInternalReadOnly() {
        return false;
    }
}
