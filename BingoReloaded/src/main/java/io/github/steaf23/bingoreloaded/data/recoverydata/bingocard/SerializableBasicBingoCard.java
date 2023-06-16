package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SerializableAs("BasicBingoCard")
public record SerializableBasicBingoCard(
        List<SerializableBingoTask> bingoTaskList,
        SerializableCardSize cardSize
) implements ConfigurationSerializable, SerializableBingoCard {
    static final String BINGO_TASKS_ID = "bingo_tasks";
    static final String CARD_SIZE_ID = "card_size";

    public SerializableBasicBingoCard(BingoCard bingoCard) {
        this(
                bingoCard.tasks
                        .stream()
                        .map(SerializableBingoTask::new)
                        .toList(),
                new SerializableCardSize(bingoCard.size)
        );
    }

    public static SerializableBasicBingoCard deserialize(Map<String, Object> data)
    {
        return new SerializableBasicBingoCard(
                (List<SerializableBingoTask>) data.getOrDefault(BINGO_TASKS_ID, new SerializableBingoTask[0]),
                (SerializableCardSize) data.getOrDefault(CARD_SIZE_ID, null)
        );
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put(BINGO_TASKS_ID, bingoTaskList);
        data.put(CARD_SIZE_ID, cardSize);

        return data;
    }

    public BingoCard toBingoCard(BingoSession session) {
        List<BingoTask> tasks = bingoTaskList
                .stream()
                .map(bingoTask -> bingoTask.toBingoTask(session))
                .toList();
        CardSize cardSize = this.cardSize.toCardSize();
        return new BingoCard(cardSize, tasks);
    }
}
