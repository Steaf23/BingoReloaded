package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.steaf23.bingoreloaded.data.recoverydata.bingocard.SerializableBasicBingoCard.BINGO_TASKS_ID;
import static io.github.steaf23.bingoreloaded.data.recoverydata.bingocard.SerializableBasicBingoCard.CARD_SIZE_ID;

@SerializableAs("LockoutBingoCard")
public record SerializableLockoutBingoCard(
        List<SerializableBingoTask> bingoTaskList,
        SerializableCardSize cardSize,
        int teamCount,
        int currentMaxTasks
) implements ConfigurationSerializable, SerializableBingoCard {
    private static final String TEAM_COUNT_ID = "team_count";
    private static final String MAX_TASKS_ID = "current_max_tasks";

    public SerializableLockoutBingoCard(LockoutBingoCard bingoCard) {
        this(
                bingoCard.tasks
                        .stream()
                        .map(SerializableBingoTask::new)
                        .toList(),
                new SerializableCardSize(bingoCard.size),
                bingoCard.teamCount,
                bingoCard.currentMaxTasks
        );
    }

    public static SerializableLockoutBingoCard deserialize(Map<String, Object> data)
    {
        return new SerializableLockoutBingoCard(
                (List<SerializableBingoTask>) data.getOrDefault(BINGO_TASKS_ID, null),
                (SerializableCardSize) data.getOrDefault(CARD_SIZE_ID, null),
                (Integer) data.getOrDefault(TEAM_COUNT_ID, 0),
                (Integer) data.getOrDefault(MAX_TASKS_ID, 0)
        );
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put(BINGO_TASKS_ID, bingoTaskList);
        data.put(CARD_SIZE_ID, cardSize);
        data.put(TEAM_COUNT_ID, teamCount);
        data.put(MAX_TASKS_ID, currentMaxTasks);

        return data;
    }

    @Override
    public BingoCard toBingoCard(BingoSession session) {
        List<BingoTask> tasks = bingoTaskList.stream()
                .map(bingoTask -> bingoTask.toBingoTask(session))
                .toList();
        CardSize cardSize = this.cardSize.toCardSize();
        return new LockoutBingoCard(cardSize, tasks, teamCount, currentMaxTasks);
    }
}
