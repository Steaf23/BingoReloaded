package io.github.steaf23.bingoreloaded.data.core.tag.adapters;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LocationStorageSerializer implements DataStorageSerializer<Location>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull Location value) {
        storage.setUUID("world", value.getWorld().getUID());
        storage.setDouble("x", value.getX());
        storage.setDouble("y", value.getY());
        storage.setDouble("z", value.getZ());
        storage.setFloat("yaw", value.getYaw());
        storage.setFloat("pitch", value.getPitch());
    }

    @Override
    public Location fromDataStorage(@NotNull DataStorage storage) {
        UUID id = storage.getUUID("world");
        if (id == null) {
            return null;
        }
        World world = Bukkit.getWorld(id);
        if (world == null) {
            return null;
        }

        double x = storage.getDouble("x", 0.0D);
        double y = storage.getDouble("y", 0.0D);
        double z = storage.getDouble("z", 0.0D);
        float yaw = storage.getFloat("yaw", 0.0f);
        float pitch = storage.getFloat("pitch", 0.0f);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
