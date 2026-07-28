package io.github.steaf23.bingoreloaded.lib.api.platform;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperServerSoftware implements PlatformStatics {

	private final JavaPlugin plugin;
	private final PlatformRegistries registries;

	public PaperServerSoftware(JavaPlugin plugin) {
		this.plugin = plugin;
		this.registries = new PaperRegistries();
	}

	@Override
	public PlatformRegistries registries() {
		return registries;
	}

	@Override
	public PlatformItemStacker itemStacker() {
		return null;
	}

	@Override
	public ComponentLogger getComponentLogger() {
		return plugin.getComponentLogger();
	}

	public JavaPlugin plugin() {
		return plugin;
	}
}
