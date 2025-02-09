package io.github.steaf23.bingoreloaded.data.core.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataStorage;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The Json data accessor is nothing more than a TagDataStorage that gets converted upon write and read to/from disk
 */
public class JsonDataAccessor extends TagDataStorage implements DataAccessor
{
    private final JavaPlugin plugin;
    private final String filepath;
    private final boolean readOnly;

    public JsonDataAccessor(JavaPlugin plugin, String filepath, boolean readOnly) {
        this.plugin = plugin;
        this.filepath = filepath;
        this.readOnly = readOnly;
    }

    @Override
    public String getLocation() {
        return filepath;
    }

    @Override
    public String getFileExtension() {
        return ".json";
    }

    @Override
    public void load() {
        if (isInternalReadOnly()) {
            InputStream stream = plugin.getResource(getLocation() + getFileExtension());
            if (stream != null) {
                readJsonFromFile(this, stream);
            }
            return;
        }

        File file = new File(plugin.getDataFolder(), getLocation() + getFileExtension());
        if (!file.exists()) {
            plugin.saveResource(getLocation() + getFileExtension(), false);
        }

        try (InputStream input = new FileInputStream(file)) {
            readJsonFromFile(this, input);
        }
        catch (IOException e) {
            ConsoleMessenger.bug("Could not open json file for reading", this);
        }
    }

    @Override
    public void saveChanges() {
        if (isInternalReadOnly()) {
            return;
        }

        //FIXME: implement
        ConsoleMessenger.error("Writing json files from JsonDataAccessor is not implemented yet!");
    }

    @Override
    public boolean isInternalReadOnly() {
        return readOnly;
    }


    public void readJsonFromFile(DataAccessor accessor, InputStream input) {
        clear();

        JsonElement element = JsonParser.parseReader(new InputStreamReader(input));
    }
}
