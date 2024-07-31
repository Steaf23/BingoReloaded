package io.github.steaf23.bingoreloaded.hologram;

import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HologramBuilder
{
    private Location location;
    private final List<String> lines;
    private String id;
    private final HologramManager manager;

    public HologramBuilder(HologramManager manager)
    {
        this.lines = new ArrayList<>();
        this.id = "";
        this.manager = manager;
        this.location = null;
    }

    @Nullable
    public Hologram create()
    {
        if (id.isEmpty() || location == null || lines.isEmpty())
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
     */
    public HologramBuilder addLine(int index, String text)
    {
        lines.add(index, text);
        return this;
    }


    /**
     * Add a line of text to the bottom of the hologram
     */
    public HologramBuilder addLine(String text)
    {
        lines.add(text);
        return this;
    }

    /**
     * Add multiple lines to the bottom of the hologram
     */
    public HologramBuilder addLines(String... lines)
    {
        Collections.addAll(this.lines, lines);
        return this;
    }

}
