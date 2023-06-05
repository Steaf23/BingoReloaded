package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import org.bukkit.entity.Player;

public class PlayerData
{
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("players.yml");

    public void savePlayer(BingoSession session, Player player)
    {
        data.getConfig().set("yeetus", SerializablePlayer.fromPlayer(BingoReloaded.getPlugin(BingoReloaded.class), player));
        data.saveConfig();
    }

    public Player loadPlayer(BingoSession session, Player player)
    {
        data.getConfig().getSerializable("yeetus", SerializablePlayer.class).toPlayer(player);
        return player;
    }
}