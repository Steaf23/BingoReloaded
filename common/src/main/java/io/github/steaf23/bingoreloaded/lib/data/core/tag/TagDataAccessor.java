package io.github.steaf23.bingoreloaded.lib.data.core.tag;

import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Saves TagTree to and loads from a gzip-compressed .nbt file.
 */
public class TagDataAccessor extends TagDataStorage implements DataAccessor
{
    private final ServerSoftware platform;
    private final String filepath;
    private final boolean internalOnly;

    public TagDataAccessor(ServerSoftware platform, String filepath, boolean internalReadOnly) {
        this.platform = platform;
        this.filepath = filepath;
        this.internalOnly = internalReadOnly;
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
        InputStream inputStream;
        if (isInternalReadOnly()) {
            inputStream = platform.getResource(getLocation() + getFileExtension());
        }
        else {
            File file = new File(platform.getDataFolder(), getLocation() + getFileExtension());
            if (!file.exists()) {
                platform.saveResource(getLocation() + getFileExtension(), false);
            }

            try {
                inputStream = new FileInputStream(file);
            }
            catch (IOException e) {
                ConsoleMessenger.bug("Could not read nbt data from file " + file.getAbsoluteFile(), this);
                return;
            }
        }

        readTagDataFromInput(this, inputStream);
    }

    @Override
    public void saveChanges() {
        if (isInternalReadOnly()) {
            return;
        }
        writeTagDataToFile(this, new File(platform.getDataFolder(), getLocation() + getFileExtension()));
    }

    @Override
    public boolean isInternalReadOnly() {
        return internalOnly;
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
            ConsoleMessenger.bug("Could not write nbt data to file " + file.getAbsolutePath(), TagDataAccessor.class);
            ConsoleMessenger.error(e.getMessage());
        }
    }

    public static void readTagDataFromInput(TagDataStorage dataStorage, InputStream fileStream) {
        byte[] bytes = new byte[]{};
        try (GZIPInputStream zipStream = new GZIPInputStream(fileStream))
        {
            bytes = zipStream.readAllBytes();
        }catch (IOException e) {
            ConsoleMessenger.bug("Could not read nbt data from file.", TagDataAccessor.class);
        }

        TagTree deserialized = readTagDataFromRawBytes(bytes);
        dataStorage.setTree(deserialized);
    }

    public static TagTree readTagDataFromRawBytes(byte[] bytes) {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        // read first compound tag info
        int ignored = stream.read();
        String ignored2 = ByteHelper.readString(stream);

		return TagDataType.COMPOUND.readBytes(stream);
    }
}
