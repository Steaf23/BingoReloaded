package io.github.steaf23.bingoreloaded.lib.data.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DataStorageSerializer<T>
{
    void toDataStorage(@NotNull DataStorage storage, @NotNull T value);
    @Nullable
    T fromDataStorage(@NotNull DataStorage storage);
}
