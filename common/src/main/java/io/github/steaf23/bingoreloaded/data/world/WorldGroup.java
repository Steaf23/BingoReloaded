package io.github.steaf23.bingoreloaded.data.world;

import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.platform.ServerSoftware;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * A world group represents a group of 3 worlds, world_name, world_name_nether and world_name_the_end
 * These worlds are saved in the plugin's data folder under the levelKey worlds
 * If the nether or end are disabled, they will be stored with the same UUID as the overworld.
 */
public record WorldGroup(PlatformServer server, Key overworldKey, boolean hasNether, boolean hasTheEnd) implements Keyed
{
    public void teleportPlayer(PlayerHandle player) {
        player.teleportBlocking(server.getWorld(overworldKey).spawnPoint());
    }

    public @Nullable WorldHandle getOverworld() {
        return overworldKey == null ? null : server.getWorld(overworldKey);
    }

    public @Nullable WorldHandle getNetherWorld() {
        return hasNether ? server.getWorld(netherKey(overworldKey)) : null;
    }

    public @Nullable WorldHandle getEndWorld() {
        return hasTheEnd ? server.getWorld(theEndKey(overworldKey)) : null;
    }

    public static Key netherKey(Key overworld) {
        return Key.key(overworld.namespace(), overworld.value() + "_the_nether");
    }

    public static Key theEndKey(Key overworld) {
        return Key.key(overworld.namespace(), overworld.value() + "_the_end");
    }

    public boolean hasWorld(Key key) {
        return overworldKey.equals(key) || netherKey(overworldKey).equals(key) || theEndKey(overworldKey).equals(key);
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

    @Override
    public @NotNull Key key() {
        return overworldKey;
    }
}
