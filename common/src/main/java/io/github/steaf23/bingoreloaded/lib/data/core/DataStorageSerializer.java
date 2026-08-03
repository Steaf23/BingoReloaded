package io.github.steaf23.bingoreloaded.lib.data.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DataStorageSerializer<T>(Class<T> type, StorageEncoder<T> encoder, StorageDecoder<T> decoder) implements StorageEncoder<T>, StorageDecoder<T>
{
    public static <U> DataStorageSerializer<U> of(Class<U> type, StorageEncoder<U> encoder, StorageDecoder<U> decoder) {
        return new DataStorageSerializer<>(type, encoder, decoder);
    }

    @Override
    public @Nullable T fromDataStorage(@NotNull DataStorage storage) {
        return decoder.fromDataStorage(storage);
    }

    @Override
    public void toDataStorage(@NotNull DataStorage storage, @NotNull T value) {
        encoder.toDataStorage(storage, value);
    }
}
