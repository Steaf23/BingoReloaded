package io.github.steaf23.bingoreloaded.lib.data.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface StorageDecoder<T> {
	@Nullable
	T fromDataStorage(@NotNull DataStorage storage);
}
