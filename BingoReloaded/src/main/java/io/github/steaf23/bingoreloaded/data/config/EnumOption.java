package io.github.steaf23.bingoreloaded.data.config;

import org.jetbrains.annotations.Nullable;

public class EnumOption<T extends Enum<T>> extends ConfigurationOption<T>
{
    private final T defaultValue;
    private final Class<T> enumClass;

    public EnumOption(String configName, Class<T> enumClass, T defaultValue) {
        super(configName);
        this.enumClass = enumClass;
        this.defaultValue = defaultValue;
    }

    @Override
    public @Nullable T fromString(String value) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
