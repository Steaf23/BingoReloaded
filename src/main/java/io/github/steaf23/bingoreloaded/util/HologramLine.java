package io.github.steaf23.bingoreloaded.util;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class HologramLine
{
    private final ArmorStand stand;
    private int lineNumber;
    private Location hologramLocation;

    public HologramLine(Location location, String line, int lineNumber)
    {
        this.stand = (ArmorStand)location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(line);
        stand.setBasePlate(false);
        this.lineNumber = lineNumber;
        this.hologramLocation = location;
    }

    public void move(Location newLocation)
    {
        hologramLocation = newLocation;
        updateLocation();
    }

    public void setLineNumber(int lineNr)
    {
        lineNumber = lineNr;
        updateLocation();
    }

    public void setText(String newText)
    {
        stand.setCustomName(newText);
    }

    public void remove()
    {
        stand.remove();
    }

    private void updateLocation()
    {
        Location newLocation = hologramLocation.clone().add(0, 0.25 * lineNumber, 0);
        if (newLocation.equals(stand.getLocation()))
            return;

        stand.teleport(newLocation);
    }
}
