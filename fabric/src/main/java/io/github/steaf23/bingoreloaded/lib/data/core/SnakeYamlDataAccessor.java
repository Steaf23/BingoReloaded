package io.github.steaf23.bingoreloaded.lib.data.core;

import io.github.steaf23.bingoreloaded.lib.api.FabricServerSoftware;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SnakeYamlDataAccessor extends PlainDataStorage implements DataAccessor {

	private final String location;
	private final FabricServerSoftware platform;

	public SnakeYamlDataAccessor(FabricServerSoftware platform, String location) {
		this.location = location;
		this.platform = platform;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getFileExtension() {
		return ".yml";
	}

	@Override
	public void load() {
		if (isInternalReadOnly()) {
			InputStream stream = platform.getResource(getLocation() + getFileExtension());
			if (stream != null) {
				root = new Yaml().load(stream);
			}

			return;
		}

		File file = new File(platform.getDataFolder(), getLocation() + getFileExtension());
		if (!file.exists()) {
			platform.saveResource(getLocation() + getFileExtension(), false);
		}

		try {
			root = new Yaml().load(new FileInputStream(file));
		} catch (IOException exception) {
			ConsoleMessenger.bug("CANNOT LOAD YAML FILE INPUT", this);
		}

//		// We have to fill this config with our plugin defaults, for when users decide to just remove parts of the file that we still want to use.
//		InputStream stream = platform.getResource(getLocation() + getFileExtension());
//		if (stream != null) {
//			YamlConfiguration defaultValues = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
//			((YamlConfiguration) root).setDefaults(defaultValues);
//		}
	}

	@Override
	public void saveChanges() {

	}

	@Override
	public boolean isInternalReadOnly() {
		return false;
	}
}
