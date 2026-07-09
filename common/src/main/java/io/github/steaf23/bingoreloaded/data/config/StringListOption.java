package io.github.steaf23.bingoreloaded.data.config;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class StringListOption extends ConfigurationOption<List<String>> {

	public StringListOption(@NotNull String configName) {
		super(configName);
	}

	//TODO: Add support for string list (and any kinda list) options in fromString by adding more extensive edit API
	@Override
	public Optional<List<String>> fromString(String value) {
		String parsed = value.trim();
		if (!parsed.startsWith("[") && parsed.endsWith("]")) {
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
}
