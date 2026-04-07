package io.github.steaf23.bingoreloaded.world;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.DebugLogger;
import io.papermc.paper.world.PaperWorldLoader;
import io.papermc.paper.world.migration.WorldFolderMigration;
import net.kyori.adventure.key.Key;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.CraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CustomWorldCreator_V26_1_1 {

	/**
	 * Here be NMS Craft-magic dragons!
	 */
	public static @Nullable World createBingoWorld(@NotNull String worldName, @Nullable Key noiseSettingsLocation) {

		CraftServer craftServer = (CraftServer) Bukkit.getServer();
		DedicatedServer console = craftServer.getServer();

		Preconditions.checkState(console.getAllLevels().iterator().hasNext(), "Cannot create additional worlds on STARTUP");
		//Preconditions.checkState(!this.console.isIteratingOverLevels, "Cannot create a world while worlds are being ticked"); // Paper - Cat - Temp disable. We'll see how this goes.

		String name = worldName;

		File folder = new File(Bukkit.getWorldContainer(), name);
		World world = Bukkit.getWorld(name);

		if (world != null) {
			return world;
		}

		if (folder.exists()) {
			Preconditions.checkArgument(folder.isDirectory(), "File (%s) exists and isn't a folder", name);
		}

		ResourceKey<LevelStem> actualDimension = LevelStem.OVERWORLD;

		NamespacedKey worldKey = NamespacedKey.fromString("bingoreloaded:" + name);
		if (worldKey == null) {
			return null;
		}
		final ResourceKey<net.minecraft.world.level.Level> dimensionKey = PaperWorldLoader.dimensionKey(worldKey);
		WorldLoader.DataLoadContext context = console.worldLoaderContext;
		RegistryAccess.Frozen registryAccess = context.datapackDimensions();
		net.minecraft.core.Registry<LevelStem> contextLevelStemRegistry = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
		final LevelStem configuredStem = console.registryAccess().lookupOrThrow(Registries.LEVEL_STEM).getValue(actualDimension);

		if (configuredStem == null) {
			throw new IllegalStateException("Missing configured level stem " + actualDimension);
		}
		try {
			WorldFolderMigration.migrateApiWorld(
					console.storageSource,
					console.registryAccess(),
					name,
					actualDimension,
					dimensionKey
			);
		} catch (final IOException ex) {
			throw new RuntimeException("Failed to migrate legacy world " + name, ex);
		}

		PaperWorldLoader.LoadedWorldData loadedWorldData = PaperWorldLoader.loadWorldData(
				console,
				dimensionKey,
				name
		);
		final PrimaryLevelData primaryLevelData = (PrimaryLevelData) console.getWorldData();
		WorldGenSettings worldGenSettings = LevelStorageSource.readExistingSavedData(console.storageSource, dimensionKey, console.registryAccess(), WorldGenSettings.TYPE)
				.result()
				.orElse(null);
		if (worldGenSettings == null) {
			WorldOptions worldOptions = new WorldOptions(new Random().nextLong(), true, false);

			DedicatedServerProperties.WorldDimensionData properties = new DedicatedServerProperties.WorldDimensionData(GsonHelper.parse("{}"), WorldType.NORMAL.name().toLowerCase(Locale.ROOT));
			WorldDimensions worldDimensions = properties.create(context.datapackWorldgen());

			WorldDimensions.Complete complete = worldDimensions.bake(contextLevelStemRegistry);
			if (complete.dimensions().getValue(actualDimension) == null) {
				throw new IllegalStateException("Missing generated level stem " + actualDimension + " for world " + name);
			}

			worldGenSettings = new WorldGenSettings(worldOptions, worldDimensions);
			registryAccess = complete.dimensionsRegistryAccess();
			loadedWorldData.levelOverrides().setHardcore(false);
			loadedWorldData = new PaperWorldLoader.LoadedWorldData(
					loadedWorldData.bukkitName(),
					loadedWorldData.uuid(),
					loadedWorldData.pdc(),
					loadedWorldData.levelOverrides()
			);
		}
		final WorldGenSettings genSettingsFinal = worldGenSettings;

		contextLevelStemRegistry = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);

		if (console.options.has("forceUpgrade")) {
			net.minecraft.server.Main.forceUpgrade(console.storageSource, DataFixers.getDataFixer(), console.options.has("eraseCache"), () -> true, registryAccess, console.options.has("recreateRegionFiles"));
		}

		long biomeZoomSeed = BiomeManager.obfuscateSeed(genSettingsFinal.options().seed());

		// -------------- BINGO RELOADED (Replace customStem with the Bingo noise-generator-injected LevelStem--------------------
		LevelStem customStem = createCustomStem(contextLevelStemRegistry, actualDimension, noiseSettingsLocation, console);
		if (customStem == null) {
			ConsoleMessenger.bug("Could not create a bingo world with smaller biomes", CustomWorldCreator_V26_1_1.class);
			customStem = genSettingsFinal.dimensions().get(actualDimension).orElse(null);
		}
		if (customStem == null) {
			customStem = contextLevelStemRegistry.getValue(actualDimension);
		}
		if (customStem == null) {
			throw new IllegalStateException("Missing level stem for world " + name + " using key " + actualDimension);
		}
		// -------------- BINGO RELOADED END ------------------

		final SavedDataStorage savedDataStorage = new SavedDataStorage(console.storageSource.getDimensionPath(dimensionKey).resolve(LevelResource.DATA.id()), console.getFixerUpper(), console.registryAccess());
		savedDataStorage.set(WorldGenSettings.TYPE, new WorldGenSettings(genSettingsFinal.options(), genSettingsFinal.dimensions()));
		List<CustomSpawner> list = ImmutableList.of(
				new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(savedDataStorage)
		);

		ServerLevel serverLevel = new ServerLevel(
				console,
				console.executor,
				console.storageSource,
				genSettingsFinal,
				dimensionKey,
				customStem,
				primaryLevelData.isDebugWorld(),
				biomeZoomSeed,
				list,
				true,
				actualDimension,
				World.Environment.NORMAL,
				null,
				null,
				savedDataStorage,
				loadedWorldData
		);

		if (craftServer.getWorld(name.toLowerCase(Locale.ROOT)) == null) {
			return null;
		}

		console.addLevel(serverLevel);
		console.initWorld(serverLevel);

		serverLevel.setSpawnSettings(true);

		console.prepareLevel(serverLevel);
		return serverLevel.getWorld();
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
