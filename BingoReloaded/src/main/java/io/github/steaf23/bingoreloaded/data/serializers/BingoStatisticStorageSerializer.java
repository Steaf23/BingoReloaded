package io.github.steaf23.bingoreloaded.data.serializers;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.data.core.tag.DataStorageSerializer;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BingoStatisticStorageSerializer implements DataStorageSerializer<BingoStatistic>
{
    @Override
    public void toDataStorage(@NotNull DataStorage storage, BingoStatistic value) {
        storage.setNamespacedKey("statistic", value.stat().getKey());
        if (value.entityType() != null)
            storage.setNamespacedKey("entity", value.entityType().getKey());
        if (value.materialType() != null)
            storage.setNamespacedKey("item", value.materialType().getKey());
    }

    @Override
    public @Nullable BingoStatistic fromDataStorage(@NotNull DataStorage storage) {
        Statistic statistic = Registry.STATISTIC.get(storage.getNamespacedKey("statistic"));
        if (statistic == null) {
            return null;
        }
        return new BingoStatistic(
                statistic,
                Registry.ENTITY_TYPE.get(storage.getNamespacedKey("entity")),
                Registry.MATERIAL.get(storage.getNamespacedKey("item")));
    }
}
