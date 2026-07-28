package io.github.steaf23.bingoreloaded.lib.api.platform;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class FabricStatics implements PlatformStatics {
	private final String mod;

	private final PlatformRegistries registries = new FabricRegistries();
	private final PlatformItemStacker itemStacker = new FabricItemStacker();

	public FabricStatics(String modId) {
		this.mod = modId;
	}

	@Override
	public PlatformRegistries registries() {
		return registries;
	}

	@Override
	public PlatformItemStacker itemStacker() {
		return itemStacker;
	}

	@Override
	public ComponentLogger getComponentLogger() {
		return ComponentLogger.logger(mod);
	}

	public String modId() {
		return mod;
	}
}
