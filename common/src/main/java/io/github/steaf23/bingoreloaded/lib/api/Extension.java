package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Namespaced;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Extension extends Namespaced {

	ComponentLogger getComponentLogger();

	@Nullable WorldHandle getWorld(String worldName);

	void registerListener(EventListener listener);
	void unregisterListener(EventListener listener);

	ServerHandle platformHandle();

	ExtensionTask runTaskTimer(long repeatTicks, long startDelay, Runnable runnable);
}
