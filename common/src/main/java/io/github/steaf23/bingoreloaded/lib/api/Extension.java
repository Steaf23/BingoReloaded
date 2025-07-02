package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.event.EventListener;
import net.kyori.adventure.key.Namespaced;

public interface Extension extends Namespaced {

	ComponentLogger getComponentLogger();

	void registerListener(EventListener listener);
	void unregisterListener(EventListener listener);
}
