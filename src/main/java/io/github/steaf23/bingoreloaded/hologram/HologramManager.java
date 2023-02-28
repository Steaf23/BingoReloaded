package io.github.steaf23.bingoreloaded.hologram;

import io.github.steaf23.bingoreloaded.core.data.YmlDataManager;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HologramManager
{
    private Map<String, Hologram> holograms;
    private static HologramManager INSTANCE;

    private static YmlDataManager data = new YmlDataManager("holograms.yml");

    private HologramManager()
    {
        this.holograms = new HashMap<>();
    }

    public static HologramManager get()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new HologramManager();
        }
        return INSTANCE;
    }

    public static Hologram create(String id, Location location, String... lines)
    {
        var holograms = get().holograms;
        if (holograms.containsKey(id))
        {
            Message.warn("Hologram with id " + id + " already exists");
            return holograms.get(id);
        }

        Hologram holo = new Hologram(location, lines);
        holograms.put(id, holo);
        return holo;
    }

    public static Hologram createImage(String id, Location location, String imagePath, ChatColor backgroundColor) throws IOException
    {
        var holograms = get().holograms;
        if (holograms.containsKey(id))
        {
            Message.warn("Hologram with id " + id + " already exists");
            return holograms.get(id);
        }

        Hologram holo = new PixelArtHologram(location, imagePath, backgroundColor);
        holograms.put(id, holo);
        return holo;
    }

    public static void destroy(String id)
    {
        var holograms = get().holograms;
        if (holograms.containsKey(id))
        {
            holograms.get(id).destroy();
            holograms.remove(id);
        }
        else
        {
            Message.warn("Hologram with id " + id + " does not exist");
        }
    }
}
