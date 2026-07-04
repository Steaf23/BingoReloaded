package io.github.steaf23.bingoreloaded.data.world;

import io.github.steaf23.bingoreloaded.lib.api.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A world group represents a group of 3 worlds, world_name, world_name_nether and world_name_the_end
 * These worlds are saved in the plugin's data folder under the levelKey worlds
 * If the nether or end are disabled, they will be stored with the same UUID as the overworld.
 */
public record WorldGroup(ServerSoftware platform, Key worldKey, UUID overworldId, UUID netherId, UUID endId)
{
    public void teleportPlayer(PlayerHandle player) {
        player.teleportBlocking(platform.getWorld(overworldId).spawnPoint());
    }

    public @Nullable WorldHandle getOverworld() {
        return overworldId == null ? null : platform.getWorld(overworldId);
    }

    public @Nullable WorldHandle getNetherWorld() {
        return netherId == null ? null : platform.getWorld(netherId);
    }

    public @Nullable WorldHandle getEndWorld() {
        return endId == null ? null : platform.getWorld(endId);
    }

    public static Key netherKey(Key overworld) {
        return Key.key(overworld.namespace(), overworld.value() + "_nether");
    }

    public static Key theEndKey(Key overworld) {
        return Key.key(overworld.namespace(), overworld.value() + "_the_end");
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
