package io.github.steaf23.bingoreloaded.lib.api;

import org.intellij.lang.annotations.Subst;

public class PlatformResolver {
	private static PlatformBridge PLATFORM;

	public static void set(PlatformBridge platform) {
		if (PLATFORM != null) throw new IllegalStateException("Platform already initialized");
		PLATFORM = platform;
	}

	@Subst("")
	public static PlatformBridge get() {
		if (PLATFORM == null) throw new IllegalStateException("Platform not initialized");
		return PLATFORM;
	}
}
