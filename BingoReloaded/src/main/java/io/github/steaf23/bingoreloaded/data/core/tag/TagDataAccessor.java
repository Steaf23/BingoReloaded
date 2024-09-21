package io.github.steaf23.bingoreloaded.data.core.tag;

import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Saves TagTree to and loads from a gzip-compressed .nbt file.
 */
public class TagDataAccessor extends TagDataStorage implements DataAccessor
{
    private final JavaPlugin plugin;
    private final String filepath;

    public TagDataAccessor(JavaPlugin plugin, String filepath) {
        this.plugin = plugin;
        this.filepath = filepath;
    }

    @Override
    public String getLocation() {
        return filepath;
    }

    @Override
    public void load() {
        readTagDataFromFile(this, new File(plugin.getDataFolder(), filepath + ".nbt"));
    }

    @Override
    public void saveChanges() {
        writeTagDataToFile(this, new File(plugin.getDataFolder(), filepath + ".nbt"));
    }

    public static void writeTagDataToFile(TagDataStorage dataStorage, File file) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TagDataType.COMPOUND.writeBytes(dataStorage.getTree(), stream);
        byte[] bytes = stream.toByteArray();

        try (FileOutputStream fileStream = new FileOutputStream(file);
             GZIPOutputStream zipStream = new GZIPOutputStream(fileStream))
        {
            zipStream.write(bytes);
        }catch (IOException e) {
            ConsoleMessenger.bug("Could not write nbt data to file " + file.getAbsolutePath(), dataStorage);
        }
    }

    public static void readTagDataFromFile(TagDataStorage dataStorage, File file) {
        byte[] bytes = new byte[]{};
        try (FileInputStream fileStream = new FileInputStream(file);
             GZIPInputStream zipStream = new GZIPInputStream(fileStream))
        {
            bytes = zipStream.readAllBytes();
        }catch (IOException e) {
            ConsoleMessenger.bug("Could not read nbt data from file " + file.getAbsolutePath(), dataStorage);
        }

        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        TagTree deserialized = TagDataType.COMPOUND.readBytes(stream);
        dataStorage.setTree(deserialized);
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }
}
