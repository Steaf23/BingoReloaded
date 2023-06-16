package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("BingoCard")
public class SerializableBingoCard implements ConfigurationSerializable {
    SerializableBingoTask[] bingoTaskList;
    private final String BINGO_TASKS_ID = "bingo_tasks";
    SerializableCardSize cardSize;
    private final String CARD_SIZE_ID = "card_size";

    public SerializableBingoCard(BingoCard bingoCard) {
        this.bingoTaskList = bingoCard.tasks
                .stream()
                .map(SerializableBingoTask::new)
                .toList()
                .toArray(new SerializableBingoTask[0]);
        this.cardSize = new SerializableCardSize(bingoCard.size);
    }

    public SerializableBingoCard(Map<String, Object> data) {
        bingoTaskList = (SerializableBingoTask[]) data.getOrDefault(BINGO_TASKS_ID, new SerializableBingoTask[0]);
        cardSize = (SerializableCardSize) data.getOrDefault(CARD_SIZE_ID, null);
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
        List<BingoTask> tasks = Arrays.stream(bingoTaskList)
                .map(bingoTask -> bingoTask.toBingoTask(session))
                .toList();
        CardSize cardSize = this.cardSize.toCardSize();
        return new BingoCard(cardSize, tasks);
    }
}
