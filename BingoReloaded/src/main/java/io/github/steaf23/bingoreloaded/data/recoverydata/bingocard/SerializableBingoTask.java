package io.github.steaf23.bingoreloaded.data.recoverydata.bingocard;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@SerializableAs("BingoTask")
public class SerializableBingoTask implements ConfigurationSerializable {
    private UUID completedBy;
    private final String COMPLETED_BY_ID = "completed_by";
    private long completedAt;
    private final String COMPLETED_AT_ID = "completed_at";
    private boolean voided;
    private final String VOIDED_ID = "voided";
    private TaskData taskData;
    private final String DATA_ID = "task_data";

    public SerializableBingoTask(BingoTask task) {
        completedBy = task.completedBy.map(BingoParticipant::getId).orElse(null);
        completedAt = task.completedAt;
        voided = task.isVoided();
        taskData = task.data;
    }

    public SerializableBingoTask(Map<String, Object> data) {
        completedBy = (UUID) data.getOrDefault(COMPLETED_BY_ID, null);
        completedAt = (Long) data.getOrDefault(COMPLETED_AT_ID, -1L);
        voided = (Boolean) data.getOrDefault(VOIDED_ID, false);
        taskData = (TaskData) data.getOrDefault(DATA_ID, null);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put(COMPLETED_BY_ID, completedBy);
        data.put(COMPLETED_AT_ID, completedAt);
        data.put(VOIDED_ID, voided);
        data.put(DATA_ID, taskData);

        return data;
    }

    public BingoTask toBingoTask(BingoSession session) {
        BingoTask task = new BingoTask(taskData);
        if (completedBy != null) {
            BingoParticipant completedParticipant = session.teamManager.getBingoParticipant(completedBy);
            if (completedParticipant != null) {
                task.completedBy = Optional.of(completedParticipant);
                task.completedAt = completedAt;
            }
        }
        task.setVoided(voided);
        return task;
    }
}
