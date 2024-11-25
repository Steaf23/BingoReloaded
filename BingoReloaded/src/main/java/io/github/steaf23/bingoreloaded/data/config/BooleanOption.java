package io.github.steaf23.bingoreloaded.data.config;

public class BooleanOption extends ConfigurationOption<Boolean>
{
    public BooleanOption(String configName) {
        super(configName);
    }

    @Override
    public Boolean fromString(String value) {
        return value.equals("true");
    }
}
