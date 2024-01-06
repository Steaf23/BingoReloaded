package io.github.steaf23.bingoreloaded.data.world;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WorldFactory
{
    World createOverworld(@NotNull String worldName);
    World createNether(@NotNull String worldName);
    World createTheEnd(@NotNull String worldName);

    boolean linkNetherPortals(World overworld, World nether);
    boolean linkEndPortals(World overworld, World end);

    boolean clearWorlds();
    boolean removeWorld(@NotNull World world);
}
