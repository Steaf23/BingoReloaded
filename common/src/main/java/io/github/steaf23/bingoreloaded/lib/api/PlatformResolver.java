package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformItemStacker;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformRegistries;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformStatics;
import org.intellij.lang.annotations.Subst;

public class PlatformResolver {
	private static PlatformStatics PLATFORM;

	public static void set(PlatformStatics platform) {
		if (PLATFORM != null) throw new IllegalStateException("Platform already initialized");
		PLATFORM = platform;
	}

	@Subst("")
	public static PlatformStatics get() {
		if (PLATFORM == null) throw new IllegalStateException("Platform not initialized");
		return PLATFORM;
	}

	public static PlatformRegistries getRegistries() {
		return PLATFORM.registries();
	}

	public static PlatformItemStacker getItemStacker() {
		return PLATFORM.itemStacker();
	}
}
