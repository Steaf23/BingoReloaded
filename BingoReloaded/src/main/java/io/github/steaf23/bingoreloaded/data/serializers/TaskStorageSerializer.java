package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.tasks.data.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.data.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.data.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.TaskData;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

public class TaskStorageSerializer implements DataStorageSerializer<TaskData>
{
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ADVANCEMENT = 1;
    private static final int TYPE_STATISTIC = 2;

    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull TaskData value) {
        int type = -1;
        switch (value) {
            case ItemTask itemTask -> {
                type = TYPE_ITEM;
                storage.setNamespacedKey("item", itemTask.material().getKey());
                storage.setInt("count", itemTask.getRequiredAmount());
            }
            case AdvancementTask advancementTask -> {
                type = TYPE_ADVANCEMENT;
                if (advancementTask.advancement() != null) {
                    storage.setNamespacedKey("advancement", advancementTask.advancement().getKey());
                }
            }
            case StatisticTask statisticTask -> {
                type = TYPE_STATISTIC;
                storage.setSerializable("statistic", BingoStatistic.class, statisticTask.statistic());
                storage.setInt("count", statisticTask.getRequiredAmount());
            }
            default -> {
            }
        }
        storage.setInt("type", type);
    }

    @Override
    public TaskData fromDataStorage(@NotNull DataStorage storage) {
        int type = storage.getInt("type", -1);
        return switch (type) {
            case TYPE_ITEM:
                yield new ItemTask(Registry.MATERIAL.get(storage.getNamespacedKey("item")), storage.getInt("count", 1));
            case TYPE_ADVANCEMENT: {
                if (storage.contains("advancement")) {
                    yield new AdvancementTask(Registry.ADVANCEMENT.get(storage.getNamespacedKey("advancement")));
                } else {
                    yield new AdvancementTask(null);
                }
            }
            case TYPE_STATISTIC:
                yield new StatisticTask(storage.getSerializable("statistic", BingoStatistic.class), storage.getInt("count", 1));
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
