package io.github.steaf23.bingoreloaded.data.core.node.datatype;

import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocationDataType implements NodeDataType<Location>
{
    @Override
    public void serializeValue(OutputStream stream, Location value) throws IOException {
        NodeDataType.UUID.serializeValue(stream, value.getWorld().getUID());
        NodeDataType.DOUBLE.serializeValue(stream, value.getX());
        NodeDataType.DOUBLE.serializeValue(stream, value.getY());
        NodeDataType.DOUBLE.serializeValue(stream, value.getZ());
        NodeDataType.DOUBLE.serializeValue(stream, (double)value.getYaw());
        NodeDataType.DOUBLE.serializeValue(stream, (double)value.getPitch());
    }

    @Override
    public Location deserializeValue(InputStream stream) throws IOException {
        java.util.UUID id = NodeDataType.UUID.deserializeValue(stream);
        World world = Bukkit.getWorld(id);
        if (world == null) {
            ConsoleMessenger.bug("Cannot deserialize Location, world with id " + id + " is not loaded", this);
        }

        double x = NodeDataType.DOUBLE.deserializeValue(stream);
        double y = NodeDataType.DOUBLE.deserializeValue(stream);
        double z = NodeDataType.DOUBLE.deserializeValue(stream);
        double yaw = NodeDataType.DOUBLE.deserializeValue(stream);
        double pitch = NodeDataType.DOUBLE.deserializeValue(stream);

        return new Location(world, x, y, z, (float)yaw, (float)pitch);
    }
}
