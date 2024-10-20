package io.github.steaf23.bingoreloaded.gui.map;

import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FileUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingoCardMapRenderer extends MapRenderer
{
    private List<GameTask> tasksToRender;
    private final JavaPlugin plugin;

    private final Map<NamespacedKey, Image> allItemImages;

    public BingoCardMapRenderer(JavaPlugin plugin, List<GameTask> tasksToRender) {
        this.plugin = plugin;
        this.tasksToRender = tasksToRender;
        this.allItemImages = new HashMap<>();

        InputStream stream = plugin.getResource("taskimages.7z");
        try (SevenZFile zipped = new SevenZFile(new SeekableInMemoryByteChannel(stream.readAllBytes()))) {
            SevenZArchiveEntry file = zipped.getNextEntry();

            while (file != null) {
                if (file.getName().equals("taskimages")) {
                    file = zipped.getNextEntry();
                    continue;
                }

                byte[] content = new byte[(int) file.getSize()];
                zipped.read(content, 0, content.length);
                Image image = ImageIO.read(new ByteArrayInputStream(content));
                // filename could be something like taskimages/diamond.png, we can extract diamond by removing taskimages/ and the extension at end
                NamespacedKey namespace = NamespacedKey.minecraft(file.getName().substring("taskimages/".length(), file.getName().length() - 4));
                allItemImages.put(namespace, image);
                file = zipped.getNextEntry();
            }

        } catch (IOException e) {
            ConsoleMessenger.error(e.getMessage());
        }

        ConsoleMessenger.log("all files: " + allItemImages.size());
    }

    //TODO: implement bingo card holdable map
    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        Image image = allItemImages.get(NamespacedKey.minecraft("anvil"));
        mapCanvas.drawImage(0, 0, image);
    }
}
