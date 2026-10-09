package io.github.steaf23.bingoreloaded.data.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DoubleOption extends ConfigurationOption<Double>
{
    public DoubleOption(String configName) {
        super(configName);
    }

    @Override
    public Optional<Double> fromArgument(CommandContext<?> context, String name) {
        return Optional.of(DoubleArgumentType.getDouble(context, name));
    }

    @Override
    public void toDataStorage(DataStorage storage, @NotNull Double value) {
        storage.setDouble(getConfigName(), value);
    }

    @Override
    public ArgumentType<?> argumentType() {
        return DoubleArgumentType.doubleArg();
    }
}
