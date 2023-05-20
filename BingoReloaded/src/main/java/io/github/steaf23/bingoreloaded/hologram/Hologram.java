package io.github.steaf23.bingoreloaded.hologram;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Hologram
{
    private boolean destroyed;
    private final List<HologramLine> lines;
    private final HologramManager manager;

    public Hologram(HologramManager manager, Location location, String... lines)
    {
        this.destroyed = false;
        this.lines = new ArrayList<>();
        for (String line : lines)
        {
            addLine(location, line);
        }
        this.manager = manager;
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
