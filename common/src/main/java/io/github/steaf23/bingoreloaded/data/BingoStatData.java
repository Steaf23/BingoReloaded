package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.hologram.HologramBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;


public class BingoStatData
{
    private final DataAccessor data = BingoReloaded.getDataAccessor("data/player_stats");

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
       setPlayerStat(playerId, statType, getPlayerStat(playerId, statType) + by);
    }

    public void setPlayerStat(UUID playerId, BingoStatType statType, int value) {
        if (statType.idx < 0)
            return;

        String statsString = getPlayerData(playerId);
        String[] stats = statsString.split(";");
        String newStat = Integer.toString(value);
        stats[statType.idx] = newStat;

        setPlayerData(playerId, String.join(";", stats));
    }

    /**
     * @param firstEntry index of first entry to show on the scoreboard
     * @param entriesPerPage how many entries to show including the first entry
     * @param sortedBy stat to sort the entries by
     */
    public HologramBuilder asHologram(int firstEntry, int entriesPerPage, @Nullable BingoStatType sortedBy)
    {
        //TODO: implement
        return new HologramBuilder(null);
    }

    public Component getPlayerStatsFormatted(UUID playerId)
    {
        String stats = getPlayerData(playerId);
        String[] statList = stats.split(";");

        String playerName = Bukkit.getOfflinePlayer(playerId).getName();
        if (playerName == null) {
            return Component.text("Statistics for invalid id " + playerId + " unavailable.");
        }

        Component[] text = BingoMessage.configStringAsMultiline("{0}'s statistics: Wins: {1}, Losses: {2}, Games finished: {3}, Tasks completed: {4}, Tasks Completed Record: {5}, Wand uses: {6}", NamedTextColor.GREEN,
                Component.text(playerName, NamedTextColor.YELLOW, TextDecoration.BOLD),
                Component.text(statList[0], NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.text(statList[1], NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.text(Integer.parseInt(statList[0]) + Integer.parseInt(statList[1]), NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.text(statList[2], NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.text(statList[3], NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.text(statList[4], NamedTextColor.WHITE, TextDecoration.BOLD));

        return Arrays.stream(text).reduce(Component::append).get();
    }

    /**
     * While it's possible to get the player's statistics from the name,
     * using the UUID directly is less expensive and should be preferred
     */
    public Component getPlayerStatsFormatted(String playerName)
    {
        UUID playerId = getPlayerUUID(playerName);
        return getPlayerStatsFormatted(playerId);
    }

    private String getPlayerData(UUID playerId)
    {
        return data.getString(playerId.toString(), "0;0;0;0;0");
    }

    private @NotNull UUID getPlayerUUID(String playerName)
    {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        return player.getUniqueId();
    }

    private void setPlayerData(UUID playerId, String statData)
    {
        data.setString(playerId.toString(), statData);
        data.saveChanges();
    }
}
