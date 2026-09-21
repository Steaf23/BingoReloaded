package io.github.steaf23.bingoreloadedcompanion.client.hud;

public record ConfigOption(String stringOption, boolean boolOption, int intOption) {

	public static final ConfigOption DEFAULT = new ConfigOption("", false, 0);

	public ConfigOption(String stringOption) {
		this(stringOption, DEFAULT.boolOption(), DEFAULT.intOption());
	}

	public ConfigOption(boolean boolOption) {
		this(DEFAULT.stringOption(), boolOption, DEFAULT.intOption());
	}

	public ConfigOption(int intOption) {
		this(DEFAULT.stringOption(), DEFAULT.boolOption(), intOption);
	}

}
