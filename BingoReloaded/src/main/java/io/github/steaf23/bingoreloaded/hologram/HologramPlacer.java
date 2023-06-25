package io.github.steaf23.bingoreloaded.hologram;

import io.github.steaf23.bingoreloaded.gui.base.FilterType;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HologramPlacer
{
    private final HologramManager manager;
    // Store a map of block positions and hologram id's for lookup purposes
    private final Map<Location, String> placedHolograms;

    public final static MenuItem HOLOGRAM_WAND = new MenuItem(Material.BLAZE_ROD,
            "" + ChatColor.AQUA + ChatColor.BOLD + "Hologram Wand",
            ChatColor.GRAY + "Right-Click in the air to select a hologram to place",
            ChatColor.GRAY + "Right-Click on a block to place selected hologram",
            ChatColor.GRAY + "Left-Click on a block to remove any placed hologram").setCompareKey("hologram_wand");

    public HologramPlacer(HologramManager manager)
    {
        this.manager = manager;
        this.placedHolograms = new HashMap<>();
    }

    public void handlePlayerInteractEvent(final PlayerInteractEvent event)
    {
        if (!HOLOGRAM_WAND.isCompareKeyEqual(event.getItem()))
            return;

        Location targetLocation = event.getClickedBlock().getLocation();

        switch (event.getAction())
        {
            case RIGHT_CLICK_BLOCK ->
            {
                placeHologram(new HologramBuilder(manager).withId("test"), targetLocation);
            }
            case RIGHT_CLICK_AIR ->
            {
                selectHologram(event.getPlayer());
            }
            case LEFT_CLICK_BLOCK ->
            {
                removeHologram(targetLocation);
            }
        }
    }

    private void placeHologram(HologramBuilder builder, Location targetLocation)
    {
        placedHolograms.put(targetLocation, "placer_gram0");
        manager.create("placer_gram0", targetLocation.add(0.0, 1.0, 0.0), "");
    }

    private void removeHologram(Location targetLocation)
    {
        if (!placedHolograms.containsKey(targetLocation))
        return;

        manager.destroy(placedHolograms.get(targetLocation));
        placedHolograms.remove(targetLocation);
    }

    private void selectHologram(Player player)
    {
        Consumer<String> result = (hologram) -> {
            Message.log("chillz");
        };

//        List<MenuItem> items = new ArrayList<>();
//        items.add(new MenuItem(Material.GLOBE_BANNER_PATTERN, "test").setCompareKey("test"));
//
//        PaginatedPickerMenu hologramPicker = new PaginatedPickerMenu(items, "Select a Hologram", null, FilterType.ITEM_KEY)
//        {
//            @Override
//            public void onOptionClickedDelegate(InventoryClickEvent event, MenuItem clickedOption, Player player)
//            {
//                result.accept(clickedOption.getCompareKey());
//            }
//        };
    }
}
