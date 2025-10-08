package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.api.FabricServerSoftware;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;

/**
 * Specific yaml data accessor for the config.yml file provided by Bukkit.
 */
public class ConfigDataAccessor extends TagDataStorage implements DataAccessor
{
	public ConfigDataAccessor(FabricServerSoftware platform) {
		super();
	}

	/**
	 * Not needed since this is the main config file.
	 * @return empty string
	 */
	@Override
	public String getLocation() {
		return "";
	}

	@Override
	public String getFileExtension() {
		return ".yml";
	}

	@Override
	public void load() {
	}

	@Override
	public void saveChanges() {
	}

	@Override
	public boolean isInternalReadOnly() {
		return false;
	}
}
