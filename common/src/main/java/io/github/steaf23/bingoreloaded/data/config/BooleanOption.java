package io.github.steaf23.bingoreloaded.data.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BooleanOption extends ConfigurationOption<Boolean>
{
    public BooleanOption(String configName) {
        super(configName);
    }

    @Override
    public Optional<Boolean> fromArgument(CommandContext<?> context, String name) {
        return Optional.of(BoolArgumentType.getBool(context, name));
    }

    @Override
    public void toDataStorage(DataStorage storage, @NotNull Boolean value) {
        storage.setBoolean(getConfigName(), value);
    }

    @Override
    public ArgumentType<?> argumentType() {
        return BoolArgumentType.bool();
    }
}
