package io.github.steaf23.bingoreloaded.lib.api.platform;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperServerSoftware implements ServerSoftware {

	private final JavaPlugin plugin;

	public PaperServerSoftware(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public ComponentLogger getComponentLogger() {
		return plugin.getComponentLogger();
	}

	@Override
	public void sendConsoleCommand(String command) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public JavaPlugin plugin() {
		return plugin;
	}
}
