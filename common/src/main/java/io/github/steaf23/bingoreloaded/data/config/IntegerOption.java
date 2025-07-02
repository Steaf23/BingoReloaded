package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;

import java.util.Optional;

public class IntegerOption extends ConfigurationOption<Integer>
{
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntegerOption(String configName) {
        super(configName);
    }

    /**
     * Set minimum value on option when parsing from string
     * @param min minimum value this option should have
     * @return this option
     */
    public IntegerOption withMin(int min) {
        this.min = min;
        return this;
    }

    /**
     * Set maximum value on option when parsing from string
     * @param max maximum value this option should have
     * @return this option
     */
    public IntegerOption withMax(int max) {
        this.max = max;
        return this;
    }

    @Override
    public Optional<Integer> fromString(String value) {
        try {
            int val = Integer.parseInt(value);
            return Optional.of(Math.clamp(val, min, max));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public void toDataStorage(DataStorage storage, Integer value) {
        storage.setInt(getConfigName(), value);
    }
}
