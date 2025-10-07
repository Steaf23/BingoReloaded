package io.github.steaf23.bingoreloadedcompanion;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BingoReloadedCompanion implements ModInitializer {

	public static final String ADDON_ID = "bingoreloaded";

    @Override
    public void onInitialize() {
    }

	public static String modVersion() {
		Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer("bingoreloadedcompanion");

		return mod.map(container -> container.getMetadata().getVersion().getFriendlyString())
				.orElse("0.3");
	}

	public static boolean isCurrentVersionNewer(@NotNull String version) {
		String current = modVersion();

		if (version.isEmpty()) {
			return false;
		}
		String[] givenVersions = version.split("\\.");
		String[] currentVersions = current.split("\\.");
		if (currentVersions.length != 2 || givenVersions.length != 2) {
			return false;
		}

		int givenMajor = Integer.parseInt(givenVersions[0]);
		int givenMinor = Integer.parseInt(givenVersions[1]);

		int currentMajor = Integer.parseInt(currentVersions[0]);
		int currentMinor = Integer.parseInt(currentVersions[1]);

		return currentMajor > givenMajor || (currentMajor == givenMajor && currentMinor > givenMinor);
	}
}
