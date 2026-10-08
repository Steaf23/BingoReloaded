package io.github.steaf23.bingoreloaded.data.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class IntegerOption extends ConfigurationOption<Integer>
{
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntegerOption(String configName) {
        super(configName);
    }

    @Override
    public Optional<Integer> fromArgument(CommandContext<?> context, String name) {
        return Optional.of(IntegerArgumentType.getInteger(context, name));
    }

    /**
     * Set minimum value on option when parsing from string
     * @param min minimum value this option should have
     * @return this option
     */
    public IntegerOption withMin(int min) {
        this.min = min;
        return this;
    }

    /**
     * Set maximum value on option when parsing from string
     * @param max maximum value this option should have
     * @return this option
     */
    public IntegerOption withMax(int max) {
        this.max = max;
        return this;
    }

    @Override
    public void toDataStorage(DataStorage storage, @NotNull Integer value) {
        storage.setInt(getConfigName(), value);
    }

    @Override
    public ArgumentType<?> argumentType() {
        return IntegerArgumentType.integer(min, max);
    }
}
