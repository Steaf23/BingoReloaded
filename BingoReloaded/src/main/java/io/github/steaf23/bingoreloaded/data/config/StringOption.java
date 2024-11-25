package io.github.steaf23.bingoreloaded.data.config;

public class StringOption extends ConfigurationOption<String>
{
    public StringOption(String configName) {
        super(configName);
    }

    @Override
    public String fromString(String value) {
        return value;
    }
}