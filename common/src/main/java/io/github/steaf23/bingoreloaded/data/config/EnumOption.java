package io.github.steaf23.bingoreloaded.data.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
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

    public Class<T> enumClass() {
        return enumClass;
    }

    @Override
    public Optional<T> fromArgument(CommandContext<?> context, String name) {
        String value = StringArgumentType.getString(context, name);
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

    @Override
    public ArgumentType<?> argumentType() {
        return StringArgumentType.string();
    }
}
