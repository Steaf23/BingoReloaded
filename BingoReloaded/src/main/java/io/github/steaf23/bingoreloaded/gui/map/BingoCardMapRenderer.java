package io.github.steaf23.bingoreloaded.gui.map;

import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BingoCardMapRenderer extends MapRenderer
{
    BingoTeam team;
    private TaskCard card;
    private final JavaPlugin plugin;

    private static final Map<NamespacedKey, Image> allItemImages = new HashMap<>();
    private static final Set<NamespacedKey> flatItems = new HashSet<>();

    private static Image COMPLETED_OVERLAY = null;

    public BingoCardMapRenderer(JavaPlugin plugin, TaskCard card, BingoTeam team) {
        this.plugin = plugin;
        this.card = card;

        if (!allItemImages.isEmpty()) {
            return;
        }

        InputStream stream = plugin.getResource("taskimages.7z");
        try (SevenZFile zipped = new SevenZFile(new SeekableInMemoryByteChannel(stream.readAllBytes()))) {
            SevenZArchiveEntry file = zipped.getNextEntry();

            while (file != null) {
                if (!file.getName().startsWith("items/") && !file.getName().startsWith("blocks/")) {
                    file = zipped.getNextEntry();
                    continue;
                }

                byte[] content = new byte[(int) file.getSize()];
                zipped.read(content, 0, content.length);
                Image image = ImageIO.read(new ByteArrayInputStream(content));
                // filename could be something like items/diamond.png, we can extract diamond by removing items/ (or blocks/ ) and the extension at end
                String itemName = file.getName().split("/")[1];
                NamespacedKey namespace = NamespacedKey.minecraft(itemName.substring(0, itemName.length() - 4));
                allItemImages.put(namespace, image);
                if (file.getName().startsWith("items")) {
                    flatItems.add(namespace);
                }
                file = zipped.getNextEntry();
            }
            InputStream overlayStream = plugin.getResource("task_completed.png");
            if (overlayStream != null)
                COMPLETED_OVERLAY = ImageIO.read(overlayStream);

        } catch (IOException e) {
            ConsoleMessenger.error(e.getMessage());
        }

        for (Material mat : Registry.MATERIAL.stream().toList()) {
            if (!mat.isItem() || mat.isAir()) continue;

            if (!allItemImages.containsKey(mat.getKey())) {
                ConsoleMessenger.warn("No task image found for item " + mat.name());
            }
        }

        ConsoleMessenger.log("Added all texture files: " + allItemImages.size());
    }

    //TODO: implement bingo card holdable map
    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        int cardSize = card.size.size;

        List<GameTask> tasks = card.getTasks();
        int offsetFromTopLeft = (5 - cardSize) / 2;

        for (int y = 0; y < cardSize; y++) {
            for (int x = 0; x < cardSize; x++) {
                GameTask task = tasks.get(y * cardSize + x);
                drawTaskOnGrid(mapCanvas, task, x + offsetFromTopLeft, y + offsetFromTopLeft);
            }
        }
    }

    public void drawTaskOnGrid(MapCanvas canvas, GameTask task, int gridX, int gridY) {

        Material mat = task.toItem().getMaterial();
        NamespacedKey key = mat.getKey();

        int extraOffset = 0;
        if (!allItemImages.containsKey(key)) {
            return;
        }

        if (flatItems.contains(key)) {
            extraOffset = 4;
        }

        canvas.drawImage(gridX * 24 + 8 + extraOffset, gridY * 24 + 4 + extraOffset, allItemImages.get(mat.getKey()));
        if (task.isCompleted() && COMPLETED_OVERLAY != null) {
            canvas.drawImage(gridX * 24 + 6, gridY * 24 + 2, COMPLETED_OVERLAY);
        }
    }
}
