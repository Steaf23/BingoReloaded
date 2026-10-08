package io.github.steaf23.bingoreloaded.data.config;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class StringListOption extends ConfigurationOption<List<String>> {

	public StringListOption(@NotNull String configName) {
		super(configName);
	}

	@Override
	public Optional<List<String>> fromArgument(CommandContext<?> context, String name) {
		String value = StringArgumentType.getString(context, name);
		String parsed = value.trim();
		if (parsed.startsWith("[") && parsed.endsWith("]")) {
			return Optional.empty();
		}

		if (value.isEmpty() || value.equals("null")) {
			return Optional.of(List.of());
		}
		return Optional.of(List.of(value));
	}

	@Override
	public void toDataStorage(DataStorage storage, @NotNull List<String> value) {
		storage.setList(getConfigName(), TagDataType.STRING, value);
	}

	@Override
	public ArgumentType<?> argumentType() {
		return StringArgumentType.greedyString();
	}
}
