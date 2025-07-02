package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;

import java.util.Optional;

//FIXME: remove this class and use custom impl for all special cases...
public class NonSerializableOption<T> extends ConfigurationOption<T>
{
    public NonSerializableOption(String configName) {
        super(configName);
        withEditUpdate(EditUpdateTime.IMPOSSIBLE);
    }

    @Override
    public Optional<T> fromString(String value) {
        return Optional.empty();
    }

    @Override
    public void toDataStorage(DataStorage storage, T value) {
        // Can't be serialized...
    }
}
