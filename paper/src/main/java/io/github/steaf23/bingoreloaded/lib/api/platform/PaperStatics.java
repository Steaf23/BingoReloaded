package io.github.steaf23.bingoreloaded.lib.api.platform;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperStatics implements PlatformStatics {

	private final JavaPlugin plugin;
	private final PlatformRegistries registries;
	private final PlatformItemStacker stacker = new PaperItemStacker();

	public PaperStatics(JavaPlugin plugin) {
		this.plugin = plugin;
		this.registries = new PaperRegistries();
	}

	@Override
	public PlatformRegistries registries() {
		return registries;
	}

	@Override
	public PlatformItemStacker itemStacker() {
		return stacker;
	}

	@Override
	public ComponentLogger getComponentLogger() {
		return plugin.getComponentLogger();
	}

	public JavaPlugin plugin() {
		return plugin;
	}
}
