package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.entity.Player;

public class PlayerData
{
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("data/players.yml");

    public void savePlayer(Player player, boolean overwriteExisting)
    {
        if (data.getConfig().contains(player.getUniqueId().toString()) && !overwriteExisting)
            return;

        data.getConfig().set(player.getUniqueId().toString(), SerializablePlayer.fromPlayer(BingoReloaded.getPlugin(BingoReloaded.class), player));
        data.saveConfig();
    }

    /**
     * Loads player information from the players.yml. Also removes this player's data from the saved players list
     * @param player
     * @return the players new state
     */
    public Player loadPlayer(Player player)
    {
        if (!data.getConfig().contains(player.getUniqueId().toString()))
        {
            Message.error("Could not find " + player.getDisplayName() + "'s data!");
            return null;
        }

        data.getConfig().getSerializable(player.getUniqueId().toString(), SerializablePlayer.class).toPlayer(player);
        data.getConfig().set(player.getUniqueId().toString(), null);
        data.saveConfig();
        return player;
    }
}