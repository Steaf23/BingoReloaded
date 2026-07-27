package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FabricResources implements PlatformResources {

	private final String modId;

	public FabricResources(String modId) {
		this.modId = modId;
	}

	@Override
	public InputStream getResource(String filePath) {
		// Using the class of your mod initializer
		return FabricResources.class.getClassLoader().getResourceAsStream(filePath);
	}

	@Override
	public void saveResource(String name, boolean replace) {
		// copy from jar if missing
		if (!replace && Files.exists(getDataFolder().toPath().resolve(name))) {
			return;
		}

		InputStream in = getResource(name);
		if (in == null) {
			ConsoleMessenger.bug("Could not locate the file at " + name, this);
			return;
		}

		try  {
			Files.createDirectories(getDataFolder().toPath().resolve(name).getParent());
			Files.copy(in, getDataFolder().toPath().resolve(name), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException exception) {
			ConsoleMessenger.bug(exception.toString(), this);
			ConsoleMessenger.bug("Could not save the file at " + getDataFolder().toPath().resolve(name) + " from " + name, this);
		}
	}

	@Override
	public File getDataFolder() {
		Path configDir = FabricLoader.getInstance().getConfigDir();
		configDir = configDir.resolve(modId);
		return configDir.toFile();
	}
}
