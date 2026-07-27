package io.github.steaf23.bingoreloaded.lib.api.platform;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class FabricServerSoftware implements ServerSoftware {
	private final String mod;

	public FabricServerSoftware(String modId) {
		this.mod = modId;
	}

	@Override
	public ComponentLogger getComponentLogger() {
		return ComponentLogger.logger(mod);
	}

	@Override
	public void sendConsoleCommand(String command) {

	}

	public String modId() {
		return mod;
	}
}
