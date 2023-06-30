package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData
{
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("data/players.yml");

    public void savePlayer(SerializablePlayer player, boolean overwriteExisting) {
        if (data.getConfig().contains(player.playerId.toString()) && !overwriteExisting)
            return;

        data.getConfig().set(player.playerId.toString(), player);
        data.saveConfig();
    }

    /**
     * Loads player information from the players.yml. Also removes this player's data from the saved players list
     *
     * @param player
     * @return the players new state
     */
    public SerializablePlayer loadPlayer(Player player) {
        if (!data.getConfig().contains(player.getUniqueId().toString())) {
            return null;
        }

        SerializablePlayer playerData = data.getConfig().getSerializable(player.getUniqueId().toString(), SerializablePlayer.class);
        playerData.toPlayer(player);
        data.getConfig().set(player.getUniqueId().toString(), null);
        data.saveConfig();
        return playerData;
    }

    public void removePlayer(UUID playerId) {
        data.getConfig().set(playerId.toString(), null);
        data.saveConfig();
    }
}