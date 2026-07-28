package io.github.steaf23.bingoreloaded.lib.api.platform;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

//FIXME: Split into several classes (resources, worlds, resolvers, etc..) ?
//

/**
 * Used by the common code to execute platform specific code.
 */
public interface PlatformStatics {

	PlatformRegistries registries();
	PlatformItemStacker itemStacker();

	ComponentLogger getComponentLogger();
}
