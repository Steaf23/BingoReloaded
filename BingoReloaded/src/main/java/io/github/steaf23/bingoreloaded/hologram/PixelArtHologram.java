package io.github.steaf23.bingoreloaded.hologram;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a hologram of a png image file for up to 16x16 pixels from the specified filepath
 */
public class PixelArtHologram extends Hologram
{
    public PixelArtHologram(Location location, String imagePath, ChatColor backgroundColor) throws IOException
    {
        super(location, linesFromImage(imagePath, backgroundColor));
    }

    private static String[] linesFromImage(String path, ChatColor backgroundColor) throws IOException
    {
        BufferedImage img = ImageIO.read(BingoReloaded.getPlugin(BingoReloaded.class).getResource(path));

        List<String> lines = new ArrayList<>();
        int width = Math.min(16, img.getWidth());
        int height = Math.min(16, img.getHeight());
        for (int row = 0; row < height; row++) {
            String line = "";
            for (int col = 0; col < width; col++) {
                if (((img.getRGB(col, row) >> 24) & 0xff) != 0)
                {
                    line += ChatColor.of("#" + Integer.toHexString((img.getRGB(col, row) & 0xffffff) | 0x1000000).substring(1)) + "█";
                }
                else
                {
                    line += "  ";
                }
            }
            lines.add(line);
        }

        return lines.toArray(new String[]{});
    }
}
