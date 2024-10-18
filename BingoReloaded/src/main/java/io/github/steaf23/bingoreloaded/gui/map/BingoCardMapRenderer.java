package io.github.steaf23.bingoreloaded.gui.map;

import io.github.steaf23.bingoreloaded.tasks.GameTask;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BingoCardMapRenderer extends MapRenderer
{
    private List<GameTask> tasksToRender;
    private final JavaPlugin plugin;

    public BingoCardMapRenderer(JavaPlugin plugin, List<GameTask> tasksToRender) {
        this.plugin = plugin;
        this.tasksToRender = tasksToRender;
    }

    //TODO: implement bingo card holdable map
    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
//        ConsoleMessenger.log("RENDERING");
//        File file = new File(plugin.getDataFolder(), "map_test.png");
//        if (!file.exists()) {
//            plugin.saveResource( "map_test.png", false);
//        }
//
//        try {
//            BufferedImage image = ImageIO.read(file);
//            mapCanvas.drawImage(0, 0, image);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
