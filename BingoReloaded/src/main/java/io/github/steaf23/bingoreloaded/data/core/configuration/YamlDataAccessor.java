package io.github.steaf23.bingoreloaded.data.core.configuration;

import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class YamlDataAccessor extends YamlDataStorage implements DataAccessor
{
    private final JavaPlugin plugin;
    private final String location;
    private final boolean internalOnly;

    public YamlDataAccessor(JavaPlugin plugin, String location, boolean internalOnly) {
        // create default config to not throw null pointers everywhere when trying to use this class before its loaded
        super(new YamlConfiguration());
        this.plugin = plugin;
        this.location = location;
        this.internalOnly = internalOnly;
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
            InputStream stream = plugin.getResource(getLocation() + getFileExtension());
            if (stream != null) {
                config = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            }

            return;
        }

        File file = new File(plugin.getDataFolder(), getLocation() + getFileExtension());
        if (!file.exists()) {
            plugin.saveResource(Paths.get(getLocation() + getFileExtension()).toString(), false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        // We have to fill this config with our plugin defaults, for when users decide to just remove parts of the file that we still want to use.
        InputStream stream = plugin.getResource(getLocation() + getFileExtension());
        if (stream != null) {
            YamlConfiguration defaultValues = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            ((YamlConfiguration) config).setDefaults(defaultValues);
        }
    }

    @Override
    public void saveChanges() {
        if (config == null || isInternalReadOnly()) {
            return;
        }

        try {
            ((YamlConfiguration) config).save(new File(plugin.getDataFolder(), getLocation() + getFileExtension()));
        } catch (IOException e) {
            ConsoleMessenger.log(e.getMessage());
        }
    }

    @Override
    public boolean isInternalReadOnly() {
        return internalOnly;
    }
}
