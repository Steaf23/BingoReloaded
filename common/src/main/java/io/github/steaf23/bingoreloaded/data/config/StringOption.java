package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;

import java.util.Optional;

public class StringOption extends ConfigurationOption<String>
{
    public StringOption(String configName) {
        super(configName);
    }

    @Override
    public Optional<String> fromString(String value) {
		if (value.isEmpty() || value.equals("null")) {
			return Optional.empty();
		}
        return Optional.of(value);
    }

    @Override
    public void toDataStorage(DataStorage storage, String value) {
        storage.setString(getConfigName(), value);
    }
}