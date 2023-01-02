package io.github.steaf23.bingoreloaded.data;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

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

    public static void incrementPlayerStat(UUID playerId, BingoStatType statType)
    {
        BingoStatsData.incrementPlayerStat(playerId, statType, 1);
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

    private static String getPlayerData(UUID playerId)
    {
        return data.getConfig().getString(playerId.toString(), "0;0;0;0;0");
    }

    private static void setPlayerData(UUID playerId, String statData)
    {
        data.getConfig().set(playerId.toString(), statData);
        data.saveConfig();
    }
}
