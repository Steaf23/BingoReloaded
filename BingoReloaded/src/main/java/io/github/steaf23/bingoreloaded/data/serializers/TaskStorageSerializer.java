package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

public class TaskStorageSerializer implements DataStorageSerializer<TaskData>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull TaskData value) {
        switch (value) {
            case ItemTask itemTask -> {
                storage.setNamespacedKey("item", itemTask.material().getKey());
                storage.setInt("count", itemTask.getRequiredAmount());
            }
            case AdvancementTask advancementTask -> {
                if (advancementTask.advancement() != null) {
                    storage.setNamespacedKey("advancement", advancementTask.advancement().getKey());
                }
            }
            case StatisticTask statisticTask -> {
                storage.setSerializable("statistic", BingoStatistic.class, statisticTask.statistic());
                storage.setInt("count", statisticTask.getRequiredAmount());
            }
            default -> {
            }
        }
    }

    @Override
    public TaskData fromDataStorage(@NotNull DataStorage storage) {
        if (storage.contains("item")) {
            return new ItemTask(Registry.MATERIAL.get(storage.getNamespacedKey("item")), storage.getInt("count", 1));
        }
        else if (storage.contains("advancement")) {
            return new AdvancementTask(Registry.ADVANCEMENT.get(storage.getNamespacedKey("advancement")));
        }
        else if (storage.contains("statistic")) {
            return new StatisticTask(storage.getSerializable("statistic", BingoStatistic.class), storage.getInt("count", 1));
        }

        throw new IllegalArgumentException("Task type not found while reading game task from file!");
    }
}
