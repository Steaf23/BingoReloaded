package io.github.steaf23.bingoreloaded.lib.api;

import java.util.Collection;

public interface ServerHandle {
	void saveResource(String name, boolean replace);

	Collection<PlayerHandle> getOnlinePlayers();
}
