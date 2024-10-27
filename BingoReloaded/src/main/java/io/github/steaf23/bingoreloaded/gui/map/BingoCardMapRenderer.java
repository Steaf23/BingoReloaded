package io.github.steaf23.bingoreloaded.gui.map;

import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
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
    BingoTeam team;
    private List<GameTask> tasksToRender;
    private final JavaPlugin plugin;

    private static final Map<NamespacedKey, Image> allItemImages = new HashMap<>();
    private static Image COMPLETED_OVERLAY = null;

    public BingoCardMapRenderer(JavaPlugin plugin, List<GameTask> tasksToRender, BingoTeam team) {
        this.plugin = plugin;
        this.tasksToRender = tasksToRender;

        if (!allItemImages.isEmpty()) {
            return;
        }

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
            InputStream overlayStream = plugin.getResource("task_completed.png");
            if (overlayStream != null)
                COMPLETED_OVERLAY = ImageIO.read(overlayStream);

        } catch (IOException e) {
            ConsoleMessenger.error(e.getMessage());
        }

        ConsoleMessenger.log("Added all texture files: " + allItemImages.size());
    }

    //TODO: implement bingo card holdable map
    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        int extraOffset = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                GameTask task = tasksToRender.get(y * 5 + x);
                Material mat = task.toItem().getMaterial();
                if (!mat.isBlock()) {
                    extraOffset = 4;
                } else {
                    extraOffset = 0;
                }

                NamespacedKey key = mat.getKey();
                if (!allItemImages.containsKey(key)) {
                    ConsoleMessenger.warn("Unknown item: " + key);
                    continue;
                }
                mapCanvas.drawImage(x * 24 + 4 + extraOffset, y * 24 + 4 + extraOffset, allItemImages.get(task.toItem().getMaterial().getKey()));
                if (task.isCompleted() && COMPLETED_OVERLAY != null) {
                    mapCanvas.drawImage(x * 24 + 4, y * 24 + 4, COMPLETED_OVERLAY);
                }
            }
        }
    }
}
