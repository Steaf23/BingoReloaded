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
public record SerializableStatisticProgress(
        UUID player,
        BingoStatistic statistic,
        int progressLeft,
        int previousGlobalProgress
) implements ConfigurationSerializable {
    private static final String PLAYER_ID = "player";
    private static final String STAT_ID = "statistic";
    private static final String PROGRESS_ID = "progress_left";
    private static final String PREV_PROGRESS_ID = "previous_global_progress";

    public SerializableStatisticProgress(StatisticProgress statisticProgress) {
        this(
                statisticProgress.getPlayer().getId(),
                statisticProgress.getStatistic(),
                statisticProgress.progressLeft,
                statisticProgress.previousGlobalProgress
        );
    }

    public static SerializableStatisticProgress deserialize(Map<String, Object> data)
    {
        String playerString = (String)data.getOrDefault(PLAYER_ID, null);
        UUID player = null;
        if (playerString != null) {
            player = UUID.fromString(playerString);
        }
        return new SerializableStatisticProgress(
                player,
                (BingoStatistic) data.getOrDefault(STAT_ID, null),
                (Integer) data.getOrDefault(PROGRESS_ID, 0),
                (Integer) data.getOrDefault(PREV_PROGRESS_ID, 0)

        );
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        if (player != null) {
            data.put(PLAYER_ID, player.toString());
        }
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
