package io.github.steaf23.bingoreloaded.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Hologram
{
    private boolean destroyed;
    private final List<HologramLine> lines;

    public Hologram(Location location, String... lines)
    {
        this.destroyed = false;
        this.lines = new ArrayList<>();
        for (String line : lines)
        {
            addLine(location, line);
        }
    }

    public void addLine(Location location, String line)
    {
        lines.add(new HologramLine(location, line, lines.size()));
        updateLineHeights();
    }

    public void insertLine(Location location, String line, int index)
    {
        lines.add(index, new HologramLine(location, line, index));
        updateLineHeights();
    }

    public void updateLineHeights()
    {
        for (int i = 0; i < lines.size(); i++)
        {
            lines.get(i).setLineNumber(lines.size() - i);
        }
    }

    public void destroy()
    {
        for (HologramLine line : lines)
        {
            line.remove();
        }
        destroyed = true;
    }
}
