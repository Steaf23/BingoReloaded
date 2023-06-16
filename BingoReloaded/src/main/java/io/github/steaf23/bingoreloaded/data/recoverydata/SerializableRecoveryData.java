package io.github.steaf23.bingoreloaded.data.recoverydata;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.CompleteBingoCard;
import io.github.steaf23.bingoreloaded.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.data.recoverydata.bingocard.SerializableBingoCard;
import io.github.steaf23.bingoreloaded.data.recoverydata.bingocard.SerializableCardSize;
import io.github.steaf23.bingoreloaded.data.recoverydata.bingocard.SerializableCompleteBingoCard;
import io.github.steaf23.bingoreloaded.data.recoverydata.bingocard.SerializableLockoutBingoCard;
import io.github.steaf23.bingoreloaded.data.recoverydata.timer.SerializableCountdownTimer;
import io.github.steaf23.bingoreloaded.data.recoverydata.timer.SerializableCounterTimer;
import io.github.steaf23.bingoreloaded.data.recoverydata.timer.SerializableGameTimer;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.tasks.statistics.StatisticProgress;
import io.github.steaf23.bingoreloaded.tasks.statistics.StatisticTracker;
import io.github.steaf23.bingoreloaded.util.timer.CountdownTimer;
import io.github.steaf23.bingoreloaded.util.timer.CounterTimer;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.*;


@SerializableAs("RecoveryData")
public class SerializableRecoveryData implements ConfigurationSerializable {
    private final SerializableBingoCard bingoCard;
    private final String BINGO_CARD_ID = "bingo_card";
    private final SerializableGameTimer timer;
    private final String TIMER_ID = "game_timer";
    private final BingoSettings settings;
    private final String SETTINGS_ID = "settings";
    private final SerializableStatisticProgress[] statisticProgresses;
    private final String STATS_ID = "statistic_progresses";

    public SerializableRecoveryData(BingoCard bingoCard, GameTimer timer, BingoSettings settings, StatisticTracker statisticTracker) {
        if (bingoCard instanceof LockoutBingoCard) {
            this.bingoCard = new SerializableLockoutBingoCard((LockoutBingoCard) bingoCard);
        } else if (bingoCard instanceof CompleteBingoCard) {
            this.bingoCard = new SerializableCompleteBingoCard((CompleteBingoCard) bingoCard);
        } else {
            this.bingoCard = new SerializableBingoCard(bingoCard);
        }
        if (timer instanceof CountdownTimer) {
            this.timer = new SerializableCountdownTimer((CountdownTimer) timer);
        } else if (timer instanceof CounterTimer) {
            this.timer = new SerializableCounterTimer((CounterTimer) timer);
        } else {
            this.timer = null;
        }
        this.settings = settings;
        if (statisticTracker == null) {
            this.statisticProgresses = null;
        } else {
            this.statisticProgresses = statisticTracker.getStatistics()
                    .stream()
                    .map(SerializableStatisticProgress::new)
                    .toList()
                    .toArray(new SerializableStatisticProgress[0]);
        }
    }

    public SerializableRecoveryData(Map<String, Object> data) {
        bingoCard = (SerializableBingoCard) data.getOrDefault(BINGO_CARD_ID, null);
        timer = (SerializableGameTimer) data.getOrDefault(TIMER_ID, null);
        settings = (BingoSettings) data.getOrDefault(SETTINGS_ID, null);
        statisticProgresses = (SerializableStatisticProgress[]) data.getOrDefault(STATS_ID, null);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put(BINGO_CARD_ID, bingoCard);
        data.put(TIMER_ID, timer);
        data.put(SETTINGS_ID, settings);
        data.put(STATS_ID, statisticProgresses);

        return data;
    }

    public RecoveryData toRecoveryData(BingoSession session) {
        StatisticTracker tracker = null;
        if (statisticProgresses != null) {
            List<StatisticProgress> stats = Arrays.stream(statisticProgresses)
                    .map(statisticProgress -> statisticProgress.toStatisticProgress(session))
                    .toList();
            tracker = new StatisticTracker(session.worldName, stats)
        }
        return new RecoveryData(
                bingoCard.toBingoCard(session),
                timer.toGameTimer(session),
                settings,
                tracker
        );
    }
}
