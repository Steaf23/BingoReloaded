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
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Saves TagTree to and loads from a gzip-compressed .nbt file.
 */
public class TagDataAccessor extends TagDataStorage implements DataAccessor
{
    private final JavaPlugin plugin;
    private final String filepath;
    private final boolean readOnly;

    public TagDataAccessor(JavaPlugin plugin, String filepath, boolean readOnly) {
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
        return ".nbt";
    }

    @Override
    public void load() {
        File file = new File(plugin.getDataFolder(), getLocation() + getFileExtension());
        if (!file.exists()) {
            plugin.saveResource(getLocation() + getFileExtension(), false);
        }
        readTagDataFromFile(this, file);
    }

    @Override
    public void saveChanges() {
        if (isInternalReadOnly()) {
            return;
        }
        writeTagDataToFile(this, new File(plugin.getDataFolder(), getLocation() + getFileExtension()));
    }

    @Override
    public boolean isInternalReadOnly() {
        return false;
    }

    public static void writeTagDataToFile(TagDataStorage dataStorage, File file) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        stream.write(TagDataType.COMPOUND.getId());
        ByteHelper.writeString("", stream);
        TagDataType.COMPOUND.writeBytes(dataStorage.getTree(), stream);
        byte[] bytes = stream.toByteArray();

        try {
            Files.createDirectories(file.getParentFile().toPath());
        } catch (IOException e) {
            ConsoleMessenger.error(e.getMessage());
        }

        try (FileOutputStream fileStream = new FileOutputStream(file);
             GZIPOutputStream zipStream = new GZIPOutputStream(fileStream))
        {
            zipStream.write(bytes);
        }catch (IOException e) {
            ConsoleMessenger.bug("Could not write nbt data to file " + file.getAbsolutePath(), dataStorage);
            ConsoleMessenger.error(e.getMessage());
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
        // read first compound tag info
        int ignored = stream.read();
        String ignored2 = ByteHelper.readString(stream);

        TagTree deserialized = TagDataType.COMPOUND.readBytes(stream);
        dataStorage.setTree(deserialized);
    }
}
