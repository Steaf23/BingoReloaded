package io.github.steaf23.bingoreloaded.data.config;

public class DoubleOption extends ConfigurationOption<Double>
{
    public DoubleOption(String configName) {
        super(configName);
    }

    @Override
    public Double fromString(String value) {
        return Double.parseDouble(value);
    }
}
