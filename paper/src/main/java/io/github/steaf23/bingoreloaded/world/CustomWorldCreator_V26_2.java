package io.github.steaf23.bingoreloaded.world;

import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.DebugLogger;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomWorldCreator_V26_2 {

	/**
	 * Here be NMS Craft-magic dragons!
	 */
	public static @Nullable World createBingoWorld(@NotNull Key worldKey, @Nullable Key noiseSettingsLocation) {

		return null;
	}

	private static LevelStem createCustomStem(net.minecraft.core.Registry<LevelStem> contextLevelStemRegistry, ResourceKey<LevelStem> actualDimension, Key noiseSettingsLocation, DedicatedServer console) {
		// Create a new stem with our custom generator in here directly, without registering it like a normal datapack.
		LevelStem existingStem = contextLevelStemRegistry.getValue(actualDimension);
		LevelStem customStem;
		if (noiseSettingsLocation == null)
		{
			DebugLogger.addLog("Noise generation settings location null (invalid namespaced key");
			customStem = existingStem;
		}
		else if (existingStem == null) {
			DebugLogger.addLog("No existing level stem found for overworld dimension? (big oopsie)");
			customStem = null;
		}
		else {
			ResourceKey<NoiseGeneratorSettings> noiseSettingsKey = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.fromNamespaceAndPath(noiseSettingsLocation.namespace(), noiseSettingsLocation.value()));
			var settingsRegistry = console.registryAccess().lookupOrThrow(Registries.NOISE_SETTINGS);
			var bingoNoiseSettings = settingsRegistry.get(noiseSettingsKey);

			if (bingoNoiseSettings.isPresent())
			{
				ChunkGenerator chunkGen = new NoiseBasedChunkGenerator(existingStem.generator().getBiomeSource(), bingoNoiseSettings.get());
				customStem = new LevelStem(existingStem.type(), chunkGen);
			} else {
				ConsoleMessenger.error("Noise generation settings called " + noiseSettingsLocation + " could not be found in enabled datapacks, please double check all your installed datapacks and verify that the generation settings are present.");
				ConsoleMessenger.log("To prevent this message from showing you can also set the config option customWorldGeneration to 'null' to use vanilla generation instead");
				customStem = existingStem;
			}
		}
		return customStem;
	}
}
