package io.github.steaf23.bingoreloaded.data.config;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigurationOption<Data>
{
    public static class StringList extends ArrayList<String>
    {
        public StringList(List<String> in)
        {
            this.addAll(in);
        }
    }

    private final String configName;

    public ConfigurationOption(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }

    abstract public @Nullable Data fromString(String value);
}
