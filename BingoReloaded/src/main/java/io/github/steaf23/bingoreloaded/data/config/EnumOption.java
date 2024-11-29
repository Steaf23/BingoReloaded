package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.data.core.DataStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

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
    public @Nullable Optional<T> fromString(String value) {
        try {
            return Optional.of(Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.of(defaultValue);
        }
    }

    @Override
    public void toDataStorage(DataStorage storage, T value) {
        storage.setString(getConfigName(), value.name());
    }
}
