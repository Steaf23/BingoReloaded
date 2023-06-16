package io.github.steaf23.bingoreloaded.data.recoverydata;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import io.github.steaf23.bingoreloaded.tasks.statistics.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.statistics.StatisticProgress;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@SerializableAs("StatisticProgress")
public class SerializableStatisticProgress implements ConfigurationSerializable {
    private UUID player;
    private final String PLAYER_ID = "player";
    private BingoStatistic statistic;
    private final String STAT_ID = "statistic";
    private int progressLeft;
    private final String PROGRESS_ID = "progress_left";
    private int previousGlobalProgress;
    private final String PREV_PROGRESS_ID = "previous_global_progress";

    public SerializableStatisticProgress(StatisticProgress statisticProgress) {
        player = statisticProgress.getPlayer().getId();
        statistic = statisticProgress.getStatistic();
        progressLeft = statisticProgress.progressLeft;
        previousGlobalProgress = statisticProgress.previousGlobalProgress;
    }

    public SerializableStatisticProgress(Map<String, Object> data) {
        player = (UUID) data.getOrDefault(PLAYER_ID, null);
        progressLeft = (Integer) data.getOrDefault(PROGRESS_ID, 0);
        previousGlobalProgress = (Integer) data.getOrDefault(PREV_PROGRESS_ID, 0);
        statistic = (BingoStatistic) data.getOrDefault(STAT_ID, null);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put(PLAYER_ID, player);
        data.put(PROGRESS_ID, progressLeft);
        data.put(PREV_PROGRESS_ID, previousGlobalProgress);
        data.put(STAT_ID, statistic);

        return data;
    }

    public StatisticProgress toStatisticProgress(BingoSession session) {
        if (player != null) {
            BingoParticipant participant = session.teamManager.getBingoParticipant(player);
            if (participant instanceof BingoPlayer) {
                return new StatisticProgress(statistic, (BingoPlayer) participant, progressLeft, previousGlobalProgress);
            }
        }
        return null;
    }
}
