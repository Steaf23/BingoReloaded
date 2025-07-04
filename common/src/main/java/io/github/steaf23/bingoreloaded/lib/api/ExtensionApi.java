package io.github.steaf23.bingoreloaded.lib.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.event.EventContext;
import java.util.Collection;
import java.util.UUID;

public class ExtensionApi {

	public static Collection<PlayerHandle> getOnlinePlayers() {
		return getExtension().platformHandle().getOnlinePlayers();
	}

	public static @Nullable WorldHandle getWorld(UUID worldId) {
		return getExtension().getWorld(worldId.toString());// FIXME: REFACTOR obviously wrong...
	}

	public static @Nullable WorldHandle getWorld(String worldId) {
		return getExtension().getWorld(worldId);
	}

	public static void callEvent(EventContext context) {

	}

	private static @NotNull Extension getExtension() {
		return null;
	}

}
