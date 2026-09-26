package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerInfo;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class BingoStatData {

	private final PlatformServer server;
	private final DataAccessor data = BingoReloaded.getDataAccessor("data/player_stats");

	public BingoStatData(PlatformServer server) {
		this.server = server;
	}

	public int getPlayerStat(UUID playerId, BingoStatType statType) {
		return getStatMap(playerId).getOrDefault(statType, 0);
	}

	public void incrementPlayerStat(PlayerHandle player, BingoStatType statType) {
		incrementPlayerStat(player.uniqueId(), statType, 1);
	}

	public void incrementPlayerStat(UUID playerId, BingoStatType statType, int by) {
		setPlayerStat(playerId, statType, getPlayerStat(playerId, statType) + by);
	}

	public void setPlayerStat(UUID playerId, BingoStatType statType, int value) {
		if (statType.idx < 0)
			return;

		Map<BingoStatType, Integer> map = new HashMap<>(getStatMap(playerId));
		map.put(statType, value);
		setPlayerData(playerId, map);
	}

	//TODO: Implement
//    /**
//     * @param firstEntry index of first entry to show on the scoreboard
//     * @param entriesPerPage how many entries to show including the first entry
//     * @param sortedBy stat to sort the entries by
//     */
//    public HologramBuilder asHologram(int firstEntry, int entriesPerPage, @Nullable BingoStatType sortedBy)
//    {
//        return new HologramBuilder(null);
//    }

	public Component getPlayerStatsFormatted(UUID playerId) {
		Map<BingoStatType, Integer> stats = getStatMap(playerId);

		String playerName = server.getPlayerInfo(playerId).playerName();
		if (playerName == null) {
			return Component.text("Statistics for invalid id " + playerId + " unavailable.");
		}

		Component[] text = BingoMessage.configStringAsMultiline("{0}'s statistics: Wins: {1}, Losses: {2}, Games finished: {3}, Tasks completed: {4}, Tasks Completed Record: {5}, Item uses: {6}", Style.style(NamedTextColor.GREEN),
				Component.text(playerName, NamedTextColor.YELLOW, TextDecoration.BOLD),
				Component.text(stats.get(BingoStatType.WINS), NamedTextColor.WHITE, TextDecoration.BOLD),
				Component.text(stats.get(BingoStatType.LOSSES), NamedTextColor.WHITE, TextDecoration.BOLD),
				Component.text(stats.get(BingoStatType.PLAYED), NamedTextColor.WHITE, TextDecoration.BOLD),
				Component.text(stats.get(BingoStatType.TASKS), NamedTextColor.WHITE, TextDecoration.BOLD),
				Component.text(stats.get(BingoStatType.RECORD_TASKS), NamedTextColor.WHITE, TextDecoration.BOLD),
				Component.text(stats.get(BingoStatType.ITEM_USES), NamedTextColor.WHITE, TextDecoration.BOLD));

		return Arrays.stream(text).reduce(Component::append).orElseThrow();
	}

	/**
	 * While it's possible to get the player's statistics from the name,
	 * using the UUID directly is less expensive and should be preferred
	 */
	public Component getPlayerStatsFormatted(String playerName) {
		UUID playerId = getPlayerUUID(playerName);
		return getPlayerStatsFormatted(playerId);
	}

	public Map<BingoStatType, Integer> getStatMap(UUID playerId) {
		Map<BingoStatType, Integer> result = new HashMap<>();
		String raw = data.getString(playerId.toString(), "0");
		String[] rawSplit = raw.split(";");
		for (BingoStatType type : BingoStatType.values()) {
			if (type.idx == -1) {
				continue;
			}
			if (rawSplit.length > type.idx) {
				result.put(type, Integer.parseInt(rawSplit[type.idx]));
			} else {
				result.put(type, 0);
			}
		}

		result.put(BingoStatType.PLAYED, result.getOrDefault(BingoStatType.WINS, 0) +
				result.getOrDefault(BingoStatType.LOSSES, 0));
		result.put(BingoStatType.ITEM_USES, result.getOrDefault(BingoStatType.WAND_USES, 0) +
				result.getOrDefault(BingoStatType.PEARL_USES, 0) +
				result.getOrDefault(BingoStatType.POUCH_USES, 0) +
				result.getOrDefault(BingoStatType.TELEPORTER_USES, 0));
		return result;
	}

	private @NotNull UUID getPlayerUUID(String playerName) {
		PlayerInfo player = server.getPlayerInfo(playerName);
		return player.uniqueId();
	}

	private void setPlayerData(UUID playerId, Map<BingoStatType, Integer> statData) {
		data.setString(playerId.toString(), String.format("%d;%d;%d;%d;%d;%d;%d;%d",
				statData.getOrDefault(BingoStatType.WINS, 0),
				statData.getOrDefault(BingoStatType.LOSSES, 0),
				statData.getOrDefault(BingoStatType.TASKS, 0),
				statData.getOrDefault(BingoStatType.RECORD_TASKS, 0),
				statData.getOrDefault(BingoStatType.WAND_USES, 0),
				statData.getOrDefault(BingoStatType.PEARL_USES, 0),
				statData.getOrDefault(BingoStatType.POUCH_USES, 0),
				statData.getOrDefault(BingoStatType.TELEPORTER_USES, 0)
		));
		data.saveChanges();
	}
}
