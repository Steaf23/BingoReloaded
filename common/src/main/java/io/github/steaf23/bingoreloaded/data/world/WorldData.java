package io.github.steaf23.bingoreloaded.data.world;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.DimensionType;
import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldOptions;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WorldData
{
    public record Options(@Nullable Key noiseGenerationSettings, boolean createNether, boolean createEnd) {}

    private final ServerSoftware platform;
    private final Options options;

    public WorldData(ServerSoftware platform, Options options) {
        this.platform = platform;
        this.options = options;
    }

    /**
     * Removes all levels in the world/dimensions/bingoreloaded folder
     *
     * @return false if 1 or more worlds could not be removed for any reason
     */
    public boolean clearWorlds() {
        int removeCount = 0;
        for (Key worldKey : platform.getAllWorldKeysOnDisk().stream()
                .filter(key -> key.namespace().equals(BingoReloaded.NAMESPACE))
                .toList()) {
            if (destroyWorld(worldKey)) {
                removeCount++;
            }
        }
        ConsoleMessenger.log(Component.text("Removed " + removeCount + " bingo worlds on startup").color(NamedTextColor.LIGHT_PURPLE));
        return true;
    }

    public WorldGroup createWorldGroupInSession(String sessionName) {
        return createWorldGroup(BingoReloaded.resourceKey(sessionName));
    }

    /**
     * Creates a world group, creating all worlds if they do not exist yet.
     * If worlds by the same levelKey exist, this will just construct a world group with the pre-existing worlds
     * @return created WorldGroup
     */
    public WorldGroup createWorldGroup(Key overworldKey) {
        WorldHandle overworld = BingoReloaded.runtime().createBingoOverworld(overworldKey, options.noiseGenerationSettings);
        if (overworld == null) {
            ConsoleMessenger.bug("Could not create world using bingo small biome generation.", this);
            createWorld(overworldKey, DimensionType.OVERWORLD);
        }

        boolean hasNether = false;
        boolean hasTheEnd = false;
        if (options.createNether()) {
            WorldHandle nether = createWorld(WorldGroup.theEndKey(overworldKey), DimensionType.NETHER);
            hasNether = nether != null;
        }

        if (options.createEnd()) {
            WorldHandle end = createWorld(WorldGroup.theEndKey(overworldKey), DimensionType.THE_END);
            hasTheEnd = end != null;
        }

        return new WorldGroup(platform, overworldKey, hasNether, hasTheEnd);
    }

    public @Nullable WorldGroup getWorldGroupInSession(String sessionName) {
        return getWorldGroup(BingoReloaded.resourceKey(sessionName));
    }

    public @Nullable WorldGroup getWorldGroup(Key overworldKey) {
        WorldHandle overworld = platform.getWorld(overworldKey);
        WorldHandle nether = platform.getWorld(WorldGroup.netherKey(overworldKey));
        WorldHandle theEnd = platform.getWorld(WorldGroup.theEndKey(overworldKey));

        if (overworld == null) {
            ConsoleMessenger.error("Could not fetch world group; " + overworldKey + " does not exist. Make sure the world exists and reload the plugin.");
            return null;
        }

        if (options.createNether()) {
            if (nether == null) {
                ConsoleMessenger.error("Could not fetch world group; " + overworldKey + "_nether does not exist. Make sure the world exists and reload the plugin.");
                return null;
            }
        }

        if (options.createEnd()) {
            if (theEnd == null) {
                ConsoleMessenger.error("Could not fetch world group; " + overworldKey + "_the_end does not exist. Make sure the world exists and reload the plugin.");
                return null;
            }
        }

        return new WorldGroup(platform, overworldKey, options.createNether(), options.createEnd());
    }

    /**
     * !Also removes the worlds from the plugin folder permanently!
     * @return true if the worlds in the world group could be destroyed correctly
     */
    public boolean destroyWorldGroup(@NotNull WorldGroup worldGroup) {
        //TODO: add logic for when end or nether were never created in the first place.
        boolean success = destroyWorld(worldGroup.key());
        success = success && destroyWorld(WorldGroup.netherKey(worldGroup.key()));
        success = success && destroyWorld(WorldGroup.theEndKey(worldGroup.key()));
        return success;
    }

    private String getWorldsFolder() {
        return platform.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }

    private @Nullable WorldHandle createWorld(Key worldName, @NotNull DimensionType dimension) {
        WorldOptions options = new WorldOptions(worldName, dimension);
        return platform.createWorld(options);
    }

    private boolean destroyWorld(Key worldKey) {
        return platform.deleteWorld(worldKey);
    }
}
