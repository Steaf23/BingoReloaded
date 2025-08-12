package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.gui.inventory.creator.TaskPickerMenu;
import io.github.steaf23.bingoreloaded.lib.api.Position;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.json.JsonDataAccessor;
import io.github.steaf23.bingoreloaded.lib.data.core.json.JsonDataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.ExtraMath;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BingoCardMapRenderer extends MapRenderer
{
    BingoTeam team;
    private final TaskCard card;

    private final Random random;
    private final List<Position> stampOffsets;

    private static final Map<Key, BufferedImage> allItemImages = new HashMap<>();
    private static final Set<Key> flatItems = new HashSet<>();

    private static BufferedImage COMPLETED_OVERLAY = null;
    private static BufferedImage BACKGROUND = null;
    private static BufferedImage ADVANCEMENT_ICON = null;
    private static BufferedImage STATISTIC_ICON = null;

    public BingoCardMapRenderer(ServerSoftware platform, TaskCard card, BingoTeam team) {
        this.card = card;

        random = new Random();
        stampOffsets = new ArrayList<>();

        // initialize stamp offsets at once so they are not drawn in different positions every time.
        for (int i  = 0; i < card.size.fullCardSize; i++) {
            stampOffsets.add(getStampOffset(0, 4));
        }

        if (!allItemImages.isEmpty()) {
            return;
        }

        try {
            JsonDataStorage atlas = new JsonDataStorage();
            JsonDataAccessor.readJsonFromFile(atlas, platform.getResource("taskimages/item_atlas.json"));
            DataStorage blocks = atlas.getStorage("blocks");
            DataStorage items = atlas.getStorage("items");

            if (items == null || blocks == null) {
                ConsoleMessenger.bug("Error loading task images from atlas.", this);
                return;
            }

            addImagesFromAtlas(platform.getResource("taskimages/blocks.png"), blocks, false);
            addImagesFromAtlas(platform.getResource("taskimages/items.png"), items, true);

            InputStream overlayStream = platform.getResource("taskimages/completed_stamp.png");
            if (overlayStream != null)
                COMPLETED_OVERLAY = ImageIO.read(overlayStream);

            InputStream backgroundStream = platform.getResource("taskimages/card_background.png");
            if (backgroundStream != null)
                BACKGROUND = ImageIO.read(backgroundStream);

            InputStream iconStream = platform.getResource("taskimages/advancement_icon.png");
            if (iconStream != null)
                ADVANCEMENT_ICON = ImageIO.read(iconStream);

            iconStream = platform.getResource("taskimages/statistic_icon.png");
            if (iconStream != null)
                STATISTIC_ICON = ImageIO.read(iconStream);

        } catch (IOException e) {
            ConsoleMessenger.error(e.getMessage());
        }

        for (Material mat : Registry.MATERIAL.stream().toList()) {
            if (!mat.isItem() || mat.isAir()) continue;

            if (!allItemImages.containsKey(mat.getKey())) {
                ConsoleMessenger.warn("No task image found for item " + mat.name());
            }
        }

        ConsoleMessenger.log("Added " + allItemImages.size() + " item images for use in the map renderer.");
    }

    private static void addImagesFromAtlas(InputStream atlas, DataStorage atlasInfo, boolean renderAsItems) throws IOException {
        List<String> itemNames = atlasInfo.getList("names", TagDataType.STRING);
        int rowCount = atlasInfo.getInt("rows", 1);
        List<Integer> sizeVec = atlasInfo.getList("texture_size", TagDataType.INT);
        int sizeX = sizeVec.get(0);
        int sizeY = sizeVec.get(1);

        BufferedImage image = ImageIO.read(atlas);

        int index = 0;
        for (String name : itemNames) {
            NamespacedKey nameKey = NamespacedKey.minecraft(name);
            BufferedImage subImage = image.getSubimage((index % rowCount) * sizeX, (index / rowCount) * sizeY, sizeX, sizeY);
            index++;
            allItemImages.put(nameKey, subImage);
            if (renderAsItems) {
                flatItems.add(nameKey);
            }
        }
    }

    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
        int cardSize = card.size.size;

        List<GameTask> tasks = card.getTasks();
        int offsetFromTopLeft = (5 - cardSize) / 2;

        if (BACKGROUND != null)
            mapCanvas.drawImage(0, 0, BACKGROUND);

        for (int y = 0; y < cardSize; y++) {
            for (int x = 0; x < cardSize; x++) {
                GameTask task = tasks.get(y * cardSize + x);
                drawTaskOnGrid(mapCanvas, task, x + offsetFromTopLeft, y + offsetFromTopLeft, stampOffsets.get(y * cardSize + x));
            }
        }
    }

    public void drawTaskOnGrid(MapCanvas canvas, GameTask task, int gridX, int gridY, Position stampOffset) {
        Key key = task.data.getDisplayMaterial(TaskPickerMenu.DUMMY_DISPLAY_INFO).key();
        int amount = task.data.getRequiredAmount();

        int extraOffset = 1;
        if (!allItemImages.containsKey(key)) {
            return;
        }

        if (flatItems.contains(key)) {
            extraOffset = 4;
        }

        drawImageAlphaScissor(canvas, gridX * 24 + 4 + extraOffset, gridY * 24 + 4 + extraOffset, allItemImages.get(key), null);

        if (amount > 1) {
            drawTaskAmount(canvas, gridX, gridY, amount);
        }

        if (task.data instanceof AdvancementTask) {
            drawImageAlphaScissor(canvas, gridX * 24 + 2, gridY * 24 + 15, ADVANCEMENT_ICON, null);
        } else if (task.data instanceof StatisticTask) {
            drawImageAlphaScissor(canvas, gridX * 24 + 2, gridY * 24 + 15, STATISTIC_ICON, null);
        }

        if (task.isCompleted() && task.getCompletedByTeam().isPresent() && COMPLETED_OVERLAY != null) {
            TextColor color = task.getCompletedByTeam().get().getColor();
            drawImageAlphaScissor(canvas, gridX * 24 + 4 + stampOffset.blockX(), gridY * 24 + 4 + stampOffset.blockY(), COMPLETED_OVERLAY, color);
        }
    }

    private void drawTaskAmount(MapCanvas canvas, int gridX, int gridY, int amount) {
        String amountString = "" + amount;

        int xStartOffset = 0;
        if (amountString.length() == 1) {
            xStartOffset = 6;
        }
        canvas.drawText(gridX * 24 + 17 + xStartOffset, gridY * 24 + 21, MinecraftFont.Font, "§47;" + amount); // dark gray shadow
        canvas.drawText(gridX * 24 + 16 + xStartOffset, gridY * 24 + 20, MinecraftFont.Font, "§58;" + amount); // white foreground
    }

    private void drawImageAlphaScissor(MapCanvas canvas, int x, int y, BufferedImage image, @Nullable TextColor modulate)
    {
        if (modulate == null) {
            for (int x2 = 0; x2 < image.getWidth(null); ++x2) {
                for (int y2 = 0; y2 < image.getHeight(null); ++y2) {
                    int alpha = (image.getRGB(x2, y2) >> 24) & 0xff;
                    if (alpha == 0)
                    {
                        continue;
                    }
                    canvas.setPixelColor(x + x2, y + y2, new Color(image.getRGB(x2, y2)));
                }
            }

            return;
        }

        Color modulateColor = new Color(modulate.red(), modulate.green(), modulate.blue());
        for (int x2 = 0; x2 < image.getWidth(null); ++x2) {
            for (int y2 = 0; y2 < image.getHeight(null); ++y2) {
                int alpha = (image.getRGB(x2, y2) >> 24) & 0xff;
                if (alpha == 0)
                {
                    continue;
                }
                canvas.setPixelColor(x + x2, y + y2, ExtraMath.modulateColor(new Color(image.getRGB(x2, y2)), modulateColor));
            }
        }
    }

    private Position getStampOffset(int minOffset, int maxOffset) {
        int range = maxOffset - minOffset;
        Position randomVec = new Position(random.nextInt(range + 1), random.nextInt(range + 1), random.nextInt(range + 1));
        return randomVec.add(new Position(minOffset, minOffset, minOffset));
    }

}
