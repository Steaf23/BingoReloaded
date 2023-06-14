package io.github.steaf23.bingoreloaded.hologram;

import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.List;

public class HologramBuilder
{
    private Location location;
    private List<String> lines;
    private String id;
    private final HologramManager manager;

    public HologramBuilder(HologramManager manager)
    {
        this.id = "";
        this.manager = manager;
        this.location = null;
    }

    @Nullable
    public Hologram create()
    {
        if (id.isEmpty() || location == null || lines.size() == 0)
            return null;

        return manager.create(id, location, lines.toArray(new String[]{}));
    }

    public HologramBuilder withLocation(Location location)
    {
        this.location = location;
        return this;
    }

    public HologramBuilder withId(String id)
    {
        this.id = id;
        return this;
    }

    /**
     * Insert a line of text into the position at index, where index 0 is at the top of the hologram.
     * @param index
     * @param text
     * @return
     */
    public HologramBuilder addLine(int index, String text)
    {
        lines.add(index, text);
        return this;
    }


    /**
     * Add a line of text to the bottom of the hologram
     * @param text
     * @return
     */
    public HologramBuilder addLine(String text)
    {
        lines.add(text);
        return this;
    }

    /**
     * Add multiple lines to the bottom of the hologram
     * @param lines
     * @return
     */
    public HologramBuilder addLines(String... lines)
    {
        for (String line : lines)
        {
            this.lines.add(line);
        }
        return this;
    }

}
