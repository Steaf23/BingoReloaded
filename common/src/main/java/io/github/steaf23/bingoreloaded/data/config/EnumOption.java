package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import org.jetbrains.annotations.NotNull;

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
    public Optional<T> fromString(String value) {
        try {
            return Optional.of(Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.ofNullable(defaultValue);
        }
    }

    @Override
    public void toDataStorage(DataStorage storage, @NotNull T value) {
        storage.setString(getConfigName(), value.name());
    }
}
