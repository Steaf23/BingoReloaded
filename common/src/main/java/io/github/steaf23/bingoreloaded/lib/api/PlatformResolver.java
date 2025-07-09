package io.github.steaf23.bingoreloaded.lib.api;

import org.intellij.lang.annotations.Subst;

public class PlatformResolver {
	private static ServerSoftware PLATFORM;

	public static void set(ServerSoftware platform) {
		if (PLATFORM != null) throw new IllegalStateException("Platform already initialized");
		PLATFORM = platform;
	}

	@Subst("")
	public static ServerSoftware get() {
		if (PLATFORM == null) throw new IllegalStateException("Platform not initialized");
		return PLATFORM;
	}
}
