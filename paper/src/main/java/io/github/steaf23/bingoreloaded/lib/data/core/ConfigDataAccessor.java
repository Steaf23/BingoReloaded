package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.api.PaperServerSoftware;

/**
 * Specific yaml data accessor for the config.yml file provided by Bukkit.
 */
public class ConfigDataAccessor extends YamlDataStorage implements DataAccessor
{
    private final PaperServerSoftware platform;

    public ConfigDataAccessor(PaperServerSoftware platform) {
        super(platform.getConfig());
        this.platform = platform;
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
        platform.reloadConfig();
        config = platform.getConfig();
    }

    @Override
    public void saveChanges() {
        platform.saveConfig();
    }

    @Override
    public boolean isInternalReadOnly() {
        return false;
    }
}
