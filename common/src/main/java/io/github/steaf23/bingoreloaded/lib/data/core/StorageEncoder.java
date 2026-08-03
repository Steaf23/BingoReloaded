package io.github.steaf23.bingoreloaded.lib.data.core;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface StorageEncoder<T> {
	void toDataStorage(@NotNull DataStorage storage, @NotNull T value);
}