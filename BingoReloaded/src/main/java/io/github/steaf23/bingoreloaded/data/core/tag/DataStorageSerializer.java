package io.github.steaf23.bingoreloaded.data.core.tag;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DataStorageSerializer<T>
{
    void toDataStorage(@NotNull DataStorage storage, T value);
    @Nullable
    T fromDataStorage(@NotNull DataStorage storage);
}
