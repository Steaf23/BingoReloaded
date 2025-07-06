package io.github.steaf23.bingoreloaded.data.world;

import io.github.steaf23.bingoreloaded.lib.api.PlatformBridge;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A world group represents a group of 3 worlds, world_name, world_name_nether and world_name_the_end
 * These worlds are saved in the plugin's data folder under the name worlds
 */
public record WorldGroup(PlatformBridge platform, String worldName, UUID overworldId, UUID netherId, UUID endId)
{
    public void teleportPlayer(PlayerHandle player) {
        //FIXME: Refactor Teleport Cause
        player.teleport(platform.getWorld(overworldId).spawnPoint());
    }

    public @Nullable WorldHandle getOverworld() {
        return overworldId == null ? null : platform.getWorld(overworldId);
    }

    public @Nullable WorldHandle getNetherWorld() {
        return netherId == null ? null : platform.getWorld(overworldId);
    }

    public @Nullable WorldHandle getEndWorld() {
        return endId == null ? null : platform.getWorld(overworldId);
    }

    public boolean hasWorld(UUID uuid) {
        return overworldId.equals(uuid) || netherId.equals(uuid) || endId.equals(uuid);
    }

    public Set<PlayerHandle> getPlayers() {
        Set<PlayerHandle> players = new HashSet<>();
        if (getOverworld() != null)
            players.addAll(getOverworld().players());
        if (getNetherWorld() != null)
            players.addAll(getNetherWorld().players());
        if (getEndWorld() != null)
            players.addAll(getEndWorld().players());
        return players;
    }
}
