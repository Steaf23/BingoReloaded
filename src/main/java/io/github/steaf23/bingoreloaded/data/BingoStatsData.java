package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoMessage;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;


public class BingoStatsData
{
    private static final YmlDataManager data = new YmlDataManager("player_stats.yml");

    public static int getPlayerStat(UUID playerId, BingoStatType statType)
    {
        if (!ConfigData.instance.savePlayerStatistics)
        {
            return -1;
        }

        if (statType == BingoStatType.PLAYED)
            return getPlayerStat(playerId, statType.WINS) + getPlayerStat(playerId, statType.LOSSES);

        if (statType.idx < 0)
            return 0;

        String statsString = getPlayerData(playerId);
        String[] stats = statsString.split(";");
        return Integer.parseInt(stats[statType.idx]);
    }

    public static void incrementPlayerStat(Player player, BingoStatType statType)
    {
        BingoStatsData.incrementPlayerStat(player.getUniqueId(), statType, 1);
    }

    public static void incrementPlayerStat(UUID playerId, BingoStatType statType, int by)
    {
        if (!ConfigData.instance.savePlayerStatistics)
        {
            return;
        }

        if (statType.idx < 0)
            return;

        String statsString = getPlayerData(playerId);
        String[] stats = statsString.split(";");
        int stat = Integer.parseInt(stats[statType.idx]);
        String newStat = Integer.toString(stat + by);
        stats[statType.idx] = newStat;

        setPlayerData(playerId, String.join(";", stats));
    }

    public static String asScoreboard(int numEntries, @Nullable BingoStatType sortedBy)
    {
        //TODO: implement
        return "";
    }

    public static Message getPlayerStatsFormatted(UUID playerId)
    {
        String stats = getPlayerData(playerId);
        String[] statList = stats.split(";");
        return new BingoMessage().untranslated("{0}'s statistics: Wins: {1}, Losses: {2}, Games finished: {3}, Tasks completed: {4}, Wand uses: {5}")
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
    public static Message getPlayerStatsFormatted(String playerName)
    {
        UUID playerId = getPlayerUUID(playerName);
        if (playerId != null)
        {
            return getPlayerStatsFormatted(playerId);
        }
        else
        {
            return new BingoMessage().untranslated("Could not find statistics for player {0}!").color(ChatColor.RED)
                    .arg(playerName).color(ChatColor.WHITE);
        }
    }

    private static String getPlayerData(UUID playerId)
    {
        return data.getConfig().getString(playerId.toString(), "0;0;0;0;0");
    }

    private static UUID getPlayerUUID(String playerName)
    {
        Map<String, Object>  playerData = data.getConfig().getValues(false);
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

    private static void setPlayerData(UUID playerId, String statData)
    {
        data.getConfig().set(playerId.toString(), statData);
        data.saveConfig();
    }
}
