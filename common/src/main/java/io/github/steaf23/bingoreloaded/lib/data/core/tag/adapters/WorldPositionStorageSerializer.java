package io.github.steaf23.bingoreloaded.lib.data.core.tag.adapters;

import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class WorldPositionStorageSerializer implements DataStorageSerializer<GlobalPosition>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull GlobalPosition value) {
        storage.setKey("world", value.dimension());
        storage.setDouble("x", value.x());
        storage.setDouble("y", value.y());
        storage.setDouble("z", value.z());
        storage.setFloat("yaw", (float)value.yaw());
        storage.setFloat("pitch", (float)value.pitch());
    }

    @Override
    public GlobalPosition fromDataStorage(@NotNull DataStorage storage) {
        Key world = storage.getKey("world");
        if (world == null) {
            return null;
        }

        double x = storage.getDouble("x", 0.0D);
        double y = storage.getDouble("y", 0.0D);
        double z = storage.getDouble("z", 0.0D);
        float yaw = storage.getFloat("yaw", 0.0f);
        float pitch = storage.getFloat("pitch", 0.0f);
        return new GlobalPosition(world, x, y, z);
    }
}
