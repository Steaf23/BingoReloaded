package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerSerializationData
{
    private final DataAccessor data = BingoReloaded.getDataAccessor("data/players");

    public void savePlayer(@NotNull SerializablePlayer player, boolean overwriteExisting) {
        if (data.contains(player.playerId.toString()) && !overwriteExisting)
            return;

        data.setSerializable(player.playerId.toString(), SerializablePlayer.class, player);
        data.saveChanges();
    }

    /**
     * Loads player information from the players.yml. Also removes this player's data from the saved players list
     *
     * @return the players new state
     */
    public @Nullable SerializablePlayer loadPlayer(@NotNull PlayerHandle player) {
        if (!data.contains(player.getUniqueId().toString())) {
            return null;
        }

        SerializablePlayer playerData = data.getSerializable(player.getUniqueId().toString(), SerializablePlayer.class);
        if (playerData == null) {
            return null;
        }
        data.erase(player.getUniqueId().toString());
        data.saveChanges();
        playerData.apply(player);
        return playerData;
    }

    public void removePlayer(UUID playerId) {
        data.erase(playerId.toString());
        data.saveChanges();
    }

    public Set<UUID> getSavedPlayers() {
        return data.getKeys().stream().map(stringId -> UUID.fromString(stringId)).collect(Collectors.toSet());
    }
}