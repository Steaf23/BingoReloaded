package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;

import java.util.Optional;

public class DoubleOption extends ConfigurationOption<Double>
{
    public DoubleOption(String configName) {
        super(configName);
    }

    @Override
    public Optional<Double> fromString(String value) {
        try {
            double val = Double.parseDouble(value);
            return Optional.of(val);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public void toDataStorage(DataStorage storage, Double value) {
        storage.setDouble(getConfigName(), value);
    }
}
