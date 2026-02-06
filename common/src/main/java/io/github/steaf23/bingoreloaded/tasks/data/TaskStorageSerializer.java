package io.github.steaf23.bingoreloaded.tasks.data;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticDefinition;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorageSerializer;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class TaskStorageSerializer implements DataStorageSerializer<TaskData>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull TaskData value) {
        switch (value) {
            case ItemTask itemTask -> {
                storage.setItemType("item", itemTask.itemType());
                storage.setInt("count", itemTask.getRequiredAmount());
            }
            case AdvancementTask advancementTask -> {
                if (advancementTask.advancement() != null) {
                    storage.setString("advancement", advancementTask.advancement().key().toString());
                }
            }
            case StatisticTask statisticTask -> {
                storage.setSerializable("statistic", StatisticDefinition.class, statisticTask.statistic());
                storage.setInt("count", statisticTask.getRequiredAmount());
            }
            default -> {
            }
        }
    }

    @Override
    public TaskData fromDataStorage(@NotNull DataStorage storage) {
        if (storage.contains("item")) {
            return new ItemTask(storage.getItemType("item"),
                    storage.getInt("count", 1));
        }
        else if (storage.contains("advancement")) {
            return new AdvancementTask(AdvancementHandle.of(
                    Key.key(storage.getString("advancement", ""))));
        }
        else if (storage.contains("statistic")) {
            return new StatisticTask(
                    storage.getSerializable("statistic", StatisticDefinition.class),
                    storage.getInt("count", 1));
        }

        throw new IllegalArgumentException("Task type not found while reading game task from file!");
    }
}
