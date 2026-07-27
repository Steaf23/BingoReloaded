package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformResources;
import io.github.steaf23.bingoreloaded.lib.api.platform.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;

public class YamlDataAccessor extends YamlDataStorage implements DataAccessor
{
    private final PlatformResources resources;
    private final String location;
    private final boolean internalOnly;

    public YamlDataAccessor(PlatformResources resources, String location, boolean internalOnly) {
        // create default config to not throw null pointers everywhere when trying to use this class before its loaded
        super(new YamlConfiguration());
        this.resources = resources;
        this.location = location;
        this.internalOnly = internalOnly;
    }

    // Updates the config by copying all user set values over into a newly created config file.
    private void patchUserConfig(YamlConfiguration userConfig, YamlConfiguration defaultConfig) {
        if (isInternalReadOnly()) {
            return;
        }

        // Only update leaf nodes, otherwise new child settings will not get copied.
        List<String> userKeys = userConfig.getKeys(true).stream().filter(key -> !userConfig.isConfigurationSection(key)).toList();
        for (String key : userKeys) {
            if (defaultConfig.contains(key)) {
                defaultConfig.set(key, userConfig.get(key));
            }
        }

        config = defaultConfig;
        try {
            defaultConfig.save(new File(resources.getDataFolder(), getLocation() + getFileExtension()));
        } catch (IOException e) {
            ConsoleMessenger.bug("Could not update " + getLocation() + getFileExtension() + " to new version", this);
        }
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getFileExtension() {
        return ".yml";
    }

    @Override
    public void load() {
        if (isInternalReadOnly()) {
            InputStream stream = resources.getResource(getLocation() + getFileExtension());
            if (stream != null) {
                config = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            }
            return;
        }

        File userFile = new File(resources.getDataFolder(), getLocation() + getFileExtension());
        if (!userFile.exists()) {
            resources.saveResource(Paths.get(getLocation() + getFileExtension()).toString(), false);
        }

        // Patch user config to add new settings but don't erase user settings.
        InputStream stream = resources.getResource(getLocation() + getFileExtension());
        if (stream != null) {
            YamlConfiguration defaultValues = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            patchUserConfig(YamlConfiguration.loadConfiguration(userFile), defaultValues);
        }
    }

    @Override
    public void saveChanges() {
        if (config == null || isInternalReadOnly()) {
            return;
        }

        try {
            ((YamlConfiguration) config).save(new File(resources.getDataFolder(), getLocation() + getFileExtension()));
        } catch (IOException e) {
            ConsoleMessenger.log(e.getMessage());
        }
    }

    @Override
    public boolean isInternalReadOnly() {
        return internalOnly;
    }
}
