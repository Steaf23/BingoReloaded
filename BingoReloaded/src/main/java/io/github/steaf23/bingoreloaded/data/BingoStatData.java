package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.hologram.HologramBuilder;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;


public class BingoStatData
{
    private final YmlDataManager data = BingoReloaded.createYmlDataManager("data/player_stats.yml");

    public BingoStatData()
    {
    }

    public int getPlayerStat(UUID playerId, BingoStatType statType)
    {
        if (statType == BingoStatType.PLAYED)
            return getPlayerStat(playerId, BingoStatType.WINS) + getPlayerStat(playerId, BingoStatType.LOSSES);

        if (statType.idx < 0)
            return 0;

        String statsString = getPlayerData(playerId);
        String[] stats = statsString.split(";");
        return Integer.parseInt(stats[statType.idx]);
    }

    public void incrementPlayerStat(Player player, BingoStatType statType)
    {
        incrementPlayerStat(player.getUniqueId(), statType, 1);
    }

    public void incrementPlayerStat(UUID playerId, BingoStatType statType, int by)
    {
        if (statType.idx < 0)
            return;

        String statsString = getPlayerData(playerId);
        String[] stats = statsString.split(";");
        int stat = Integer.parseInt(stats[statType.idx]);
        String newStat = Integer.toString(stat + by);
        stats[statType.idx] = newStat;

        setPlayerData(playerId, String.join(";", stats));
    }

    /**
     * @param firstEntry index of first entry to show on the scoreboard
     * @param entriesPerPage how many entries to show including the first entry
     * @param sortedBy stat to sort the entries by
     * @return
     */
    public HologramBuilder asHologram(int firstEntry, int entriesPerPage, @Nullable BingoStatType sortedBy)
    {
        //TODO: implement
        return new HologramBuilder(BingoReloaded.getPlugin(BingoReloaded.class).holograms());
    }

    public Message getPlayerStatsFormatted(UUID playerId)
    {
        String stats = getPlayerData(playerId);
        String[] statList = stats.split(";");
        return new Message().untranslated("{0}'s statistics: Wins: {1}, Losses: {2}, Games finished: {3}, Tasks completed: {4}, Wand uses: {5}")
                .color(ChatColor.GREEN)
                .arg(Bukkit.getOfflinePlayer(playerId).getName()).color(ChatColor.YELLOW).bold()
                .arg(statList[0]).color(ChatColor.WHITE).bold()
                .arg(statList[1]).color(ChatColor.WHITE).bold()
                .arg(Integer.toString(Integer.parseInt(statList[0]) + Integer.parseInt(statList[1]))).color(ChatColor.WHITE).bold()
                .arg(statList[2]).color(ChatColor.WHITE).bold()
                .arg(statList[4]).color(ChatColor.WHITE).bold();
    }

    /**
     * While it's possible to get the player's statistics from the name,
     * using the UUID directly is less expensive and should be preferred
     * @param playerName
     * @return
     */
    public Message getPlayerStatsFormatted(String playerName)
    {
        UUID playerId = getPlayerUUID(playerName);
        if (playerId != null)
        {
            return getPlayerStatsFormatted(playerId);
        }
        else
        {
            return new Message().untranslated("Could not find statistics for player {0}!").color(ChatColor.RED)
                    .arg(playerName).color(ChatColor.WHITE);
        }
    }

    private String getPlayerData(UUID playerId)
    {
        return data.getConfig().getString(playerId.toString(), "0;0;0;0;0");
    }

    private UUID getPlayerUUID(String playerName)
    {
        Map<String, Object> playerData = data.getConfig().getValues(false);
        for (String recordName : playerData.keySet())
        {
            UUID playerId = UUID.fromString(recordName);
            if (Bukkit.getPlayer(playerId).getName().equals(playerName))
            {
                return playerId;
            }
        }
        return null;
    }

    private void setPlayerData(UUID playerId, String statData)
    {
        data.getConfig().set(playerId.toString(), statData);
        data.saveConfig();
    }
}
