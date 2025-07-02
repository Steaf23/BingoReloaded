package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;

import java.util.Optional;

public class BooleanOption extends ConfigurationOption<Boolean>
{
    public BooleanOption(String configName) {
        super(configName);
    }

    @Override
    public Optional<Boolean> fromString(String value) {
        return Optional.of(value.equals("true"));
    }

    @Override
    public void toDataStorage(DataStorage storage, Boolean value) {
        storage.setBoolean(getConfigName(), value);
    }
}
