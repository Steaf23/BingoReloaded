package io.github.steaf23.bingoreloaded.data.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

//FIXME: remove this class and use custom impl for all special cases...
public class NonSerializableOption<T> extends ConfigurationOption<T>
{
    public NonSerializableOption(String configName) {
        super(configName);
        withEditUpdate(EditUpdateTime.IMPOSSIBLE);
    }

    @Override
    public ConfigurationOption<T> withEditUpdate(EditUpdateTime editUpdate) {
        if (editUpdate != EditUpdateTime.IMPOSSIBLE) {
            ConsoleMessenger.bug("Cannot edit config option " + getConfigName(), NonSerializableOption.class);
            return this;
        }
        return super.withEditUpdate(editUpdate);
    }

    @Override
    public Optional<T> fromArgument(CommandContext<?> context, String name) {
        return Optional.empty();
    }

    @Override
    public void toDataStorage(DataStorage storage, @NotNull T value) {
        // Can't be serialized...
    }

    @Override
    public ArgumentType<?> argumentType() {
        return null;
    }
}
