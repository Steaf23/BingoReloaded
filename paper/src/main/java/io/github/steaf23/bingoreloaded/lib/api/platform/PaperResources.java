package io.github.steaf23.bingoreloaded.lib.api.platform;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;

public class PaperResources implements PlatformResources {

	private final JavaPlugin plugin;

	public PaperResources(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public InputStream getResource(String filePath) {
		return plugin.getResource(filePath);
	}

	@Override
	public void saveResource(String name, boolean replace) {
		plugin.saveResource(name, replace);
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataFolder();
	}

	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}

	public void saveConfig() {
		plugin.saveConfig();
	}

	public void reloadConfig() {
		plugin.reloadConfig();
	}
}
