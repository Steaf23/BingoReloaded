package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.data.core.helper.SerializablePlayer;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

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
    public @Nullable SerializablePlayer loadPlayer(Player player) {
        if (!data.contains(player.getUniqueId().toString())) {
            return null;
        }

        SerializablePlayer playerData = data.getSerializable(player.getUniqueId().toString(), SerializablePlayer.class);
        if (playerData == null) {
            return null;
        }
        ConsoleMessenger.warn(Arrays.toString(playerData.inventory));
        data.erase(player.getUniqueId().toString());
        data.saveChanges();
        playerData.apply(player);
        return playerData;
    }

    public void removePlayer(UUID playerId) {
        data.erase(playerId.toString());
        data.saveChanges();
    }
}