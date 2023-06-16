package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.cards.CompleteBingoCard;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SerializableAs("CompleteBingoCard")
public class SerializableCompleteBingoCard extends SerializableBingoCard implements ConfigurationSerializable {

    public SerializableCompleteBingoCard(CompleteBingoCard bingoCard) {
        super(bingoCard);
        this.bingoTaskList = bingoCard.tasks
                .stream()
                .map(SerializableBingoTask::new)
                .toList()
                .toArray(new SerializableBingoTask[0]);
        this.cardSize = new SerializableCardSize(bingoCard.size);
    }

    public SerializableCompleteBingoCard(Map<String, Object> data) {
        super(data);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public BingoCard toBingoCard(BingoSession session) {
        List<BingoTask> tasks = Arrays.stream(bingoTaskList)
                .map(bingoTask -> bingoTask.toBingoTask(session))
                .toList();
        CardSize cardSize = this.cardSize.toCardSize();
        return new CompleteBingoCard(cardSize, tasks);
    }
}
