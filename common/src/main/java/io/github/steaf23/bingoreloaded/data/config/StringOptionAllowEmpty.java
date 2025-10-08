package io.github.steaf23.bingoreloaded.data.config;

import java.util.Optional;

public class StringOptionAllowEmpty extends StringOption
{

	public StringOptionAllowEmpty(String configName) {
		super(configName);
	}

	@Override
	public Optional<String> fromString(String value) {
		if (value.isEmpty() || value.equals("null")) {
			return Optional.of("");
		}
		return Optional.of(value);
	}
}
