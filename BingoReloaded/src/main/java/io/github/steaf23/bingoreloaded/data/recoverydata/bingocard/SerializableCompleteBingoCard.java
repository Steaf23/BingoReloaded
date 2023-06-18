package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.cards.CompleteBingoCard;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.steaf23.bingoreloaded.data.recoverydata.bingocard.SerializableBasicBingoCard.BINGO_TASKS_ID;
import static io.github.steaf23.bingoreloaded.data.recoverydata.bingocard.SerializableBasicBingoCard.CARD_SIZE_ID;

@SerializableAs("CompleteBingoCard")
public record SerializableCompleteBingoCard(
        List<SerializableBingoTask> bingoTaskList,
        SerializableCardSize cardSize
) implements ConfigurationSerializable, SerializableBingoCard {

    public SerializableCompleteBingoCard(CompleteBingoCard bingoCard) {
        this(
                bingoCard.tasks
                        .stream()
                        .map(SerializableBingoTask::new)
                        .toList(),
                new SerializableCardSize(bingoCard.size)
        );
    }

    public static SerializableCompleteBingoCard deserialize(Map<String, Object> data)
    {
        return new SerializableCompleteBingoCard(
                (List<SerializableBingoTask>) data.getOrDefault(BINGO_TASKS_ID, null),
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

    @Override
    public BingoCard toBingoCard(BingoSession session) {
        List<BingoTask> tasks = bingoTaskList.stream()
                .map(bingoTask -> bingoTask.toBingoTask(session))
                .toList();
        CardSize cardSize = this.cardSize.toCardSize();
        return new CompleteBingoCard(cardSize, tasks);
    }
}
