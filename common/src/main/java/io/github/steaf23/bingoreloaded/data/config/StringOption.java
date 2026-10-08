package io.github.steaf23.bingoreloaded.data.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class StringOption extends ConfigurationOption<String>
{
    public StringOption(String configName) {
        super(configName);
    }

	@Override
	public Optional<String> fromArgument(CommandContext<?> context, String name) {
		String rawValue = StringArgumentType.getString(context, name);
		if (rawValue.isBlank() || rawValue.equals("null")) {
			return Optional.empty();
		}
		return Optional.of(rawValue);
	}

    @Override
    public void toDataStorage(DataStorage storage, @NotNull String value) {
        storage.setString(getConfigName(), value);
    }

	@Override
	public ArgumentType<?> argumentType() {
		return StringArgumentType.greedyString();
	}
}