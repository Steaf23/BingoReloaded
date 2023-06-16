package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SerializableAs("LockoutBingoCard")
public class SerializableLockoutBingoCard extends SerializableBingoCard implements ConfigurationSerializable {
    private int teamCount;
    private final String TEAM_COUNT_ID = "team_count";
    private int currentMaxTasks;
    private final String MAX_TASKS_ID = "current_max_tasks";

    public SerializableLockoutBingoCard(LockoutBingoCard bingoCard) {
        super(bingoCard);
        teamCount = bingoCard.teamCount;
        currentMaxTasks = bingoCard.currentMaxTasks;
    }

    public SerializableLockoutBingoCard(Map<String, Object> data) {
        super(data);
        teamCount = (Integer) data.getOrDefault(TEAM_COUNT_ID, 0);
        currentMaxTasks = (Integer) data.getOrDefault(MAX_TASKS_ID, 0);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = super.serialize();

        data.put(TEAM_COUNT_ID, teamCount);
        data.put(MAX_TASKS_ID, currentMaxTasks);

        return super.serialize();
    }

    @Override
    public BingoCard toBingoCard(BingoSession session) {
        List<BingoTask> tasks = Arrays.stream(super.bingoTaskList)
                .map(bingoTask -> bingoTask.toBingoTask(session))
                .toList();
        CardSize cardSize = this.cardSize.toCardSize();
        return new LockoutBingoCard(cardSize, tasks, teamCount, currentMaxTasks);
    }
}
