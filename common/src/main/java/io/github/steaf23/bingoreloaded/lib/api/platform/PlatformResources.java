package io.github.steaf23.bingoreloaded.lib.api.platform;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public interface PlatformResources {

	/**
	 * Should be called to get a resource directly embedded in the jar.
	 */
	@Nullable InputStream getResource(String filePath);

	/**
	 * Should be used to save a copy resource that's embedded in the jar into the data folder.
	 */
	void saveResource(String name, boolean replace);

	/**
	 * @return the folder where data and special config stuff is saved that is not contained in the default config file.
	 */
	File getDataFolder();
}
