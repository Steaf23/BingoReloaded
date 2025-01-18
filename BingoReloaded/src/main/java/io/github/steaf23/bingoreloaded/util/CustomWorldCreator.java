package io.github.steaf23.bingoreloaded.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.validation.ContentValidationException;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CustomWorldCreator
{
    public static World createWorld(JavaPlugin plugin, String worldName) {
        String worldFolder = getWorldsFolder(plugin);
        return createBingoWorld(worldFolder + worldName, new NamespacedKey("biome_preset", "small"));
    }

    /**
     * NMS Craft-magic is happening here, be warned!
     */
    private static World createBingoWorld(String worldName, NamespacedKey noiseSettingsLocation) {

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

//        ResourceKey<LevelStem> actualDimension = ResourceKey.create(Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath("bingoreloaded", "bingo"));
        ResourceKey<LevelStem> actualDimension = LevelStem.OVERWORLD;

        LevelStorageSource.LevelStorageAccess levelStorageAccess;
        try {
            levelStorageAccess = LevelStorageSource.createDefault(Bukkit.getWorldContainer().toPath()).validateAndCreateAccess(name, actualDimension);
        } catch (IOException | ContentValidationException ex) {
            throw new RuntimeException(ex);
        }

        Dynamic<?> dataTag;
        if (levelStorageAccess.hasWorldData()) {
            net.minecraft.world.level.storage.LevelSummary summary;
            try {
                dataTag = levelStorageAccess.getDataTag();
                summary = levelStorageAccess.getSummary(dataTag);
            } catch (NbtException | ReportedNbtException | IOException e) {
                LevelStorageSource.LevelDirectory levelDirectory = levelStorageAccess.getLevelDirectory();
                MinecraftServer.LOGGER.warn("Failed to load world data from {}", levelDirectory.dataFile(), e);
                MinecraftServer.LOGGER.info("Attempting to use fallback");

                try {
                    dataTag = levelStorageAccess.getDataTagFallback();
                    summary = levelStorageAccess.getSummary(dataTag);
                } catch (NbtException | ReportedNbtException | IOException e1) {
                    MinecraftServer.LOGGER.error("Failed to load world data from {}", levelDirectory.oldDataFile(), e1);
                    MinecraftServer.LOGGER.error(
                            "Failed to load world data from {} and {}. World files may be corrupted. Shutting down.",
                            levelDirectory.dataFile(),
                            levelDirectory.oldDataFile()
                    );
                    return null;
                }

                levelStorageAccess.restoreLevelDataFromOld();
            }

            if (summary.requiresManualConversion()) {
                MinecraftServer.LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
                return null;
            }

            if (!summary.isCompatible()) {
                MinecraftServer.LOGGER.info("This world was created by an incompatible version.");
                return null;
            }
        } else {
            dataTag = null;
        }

        WorldLoader.DataLoadContext context = console.worldLoader;
        RegistryAccess.Frozen registryAccess = context.datapackDimensions();
        net.minecraft.core.Registry<LevelStem> contextLevelStemRegistry = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);

        PrimaryLevelData primaryLevelData;
        if (dataTag != null) {
            LevelDataAndDimensions levelDataAndDimensions = LevelStorageSource.getLevelDataAndDimensions(
                    dataTag, context.dataConfiguration(), contextLevelStemRegistry, context.datapackWorldgen()
            );
            primaryLevelData = (PrimaryLevelData) levelDataAndDimensions.worldData();
            registryAccess = levelDataAndDimensions.dimensions().dimensionsRegistryAccess();
        } else {
            LevelSettings levelSettings;
            WorldOptions worldOptions = new WorldOptions((new Random()).nextLong(), true, false);
            WorldDimensions worldDimensions;

            DedicatedServerProperties.WorldDimensionData properties = new DedicatedServerProperties.WorldDimensionData(GsonHelper.parse("{}"), WorldType.NORMAL.name().toLowerCase(Locale.ROOT));
            levelSettings = new LevelSettings(
                    name,
                    GameType.byId(Bukkit.getDefaultGameMode().getValue()),
                    false, Difficulty.EASY,
                    false,
                    new GameRules(context.dataConfiguration().enabledFeatures()),
                    context.dataConfiguration())
            ;
            worldDimensions = properties.create(context.datapackWorldgen());

            WorldDimensions.Complete complete = worldDimensions.bake(contextLevelStemRegistry);
            Lifecycle lifecycle = complete.lifecycle().add(context.datapackWorldgen().allRegistriesLifecycle());

            primaryLevelData = new PrimaryLevelData(levelSettings, worldOptions, complete.specialWorldProperty(), lifecycle);
            registryAccess = complete.dimensionsRegistryAccess();
        }
        contextLevelStemRegistry = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);

        // Create a new stem with our custom generator in here directly, without registering it like a normal datapack.
        LevelStem existingStem = contextLevelStemRegistry.getValue(actualDimension);
        ResourceKey<NoiseGeneratorSettings> noiseSettingsKey = ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.fromNamespaceAndPath(noiseSettingsLocation.getNamespace(), noiseSettingsLocation.getKey()));
        var settingsRegistry = console.registryAccess().lookupOrThrow(Registries.NOISE_SETTINGS);
        var bingoNoiseSettings = settingsRegistry.get(noiseSettingsKey);

        LevelStem customStem;
        if (bingoNoiseSettings.isPresent())
        {
            ChunkGenerator chunkGen = new NoiseBasedChunkGenerator(existingStem.generator().getBiomeSource(), bingoNoiseSettings.get());
            customStem = new LevelStem(existingStem.type(), chunkGen);
        } else {
            ConsoleMessenger.bug("bingoreloaded datapack is not loaded!", CustomWorldCreator.class);
            customStem = existingStem;
        }

        primaryLevelData.customDimensions = contextLevelStemRegistry;
        primaryLevelData.checkName(name);
        primaryLevelData.setModdedInfo(console.getServerModName(), console.getModdedStatus().shouldReportAsModified());

        if (console.options.has("forceUpgrade")) {
            net.minecraft.server.Main.forceUpgrade(levelStorageAccess, DataFixers.getDataFixer(), console.options.has("eraseCache"), () -> true, registryAccess, console.options.has("recreateRegionFiles"));
        }

        long i = BiomeManager.obfuscateSeed(primaryLevelData.worldGenOptions().seed());
        List<CustomSpawner> additionalSpawners = ImmutableList.of(
                new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(primaryLevelData)
        );

        ResourceKey<Level> dimensionKey;
        String levelName = console.getProperties().levelName;
        if (name.equals(levelName + "_nether")) {
            dimensionKey = net.minecraft.world.level.Level.NETHER;
        } else if (name.equals(levelName + "_the_end")) {
            dimensionKey = net.minecraft.world.level.Level.END;
        } else {
            dimensionKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("minecraft", name.toLowerCase(Locale.ROOT)));
        }

        // Disable spawn chunks for bingo since players are going to run away from spawn anyway.
        primaryLevelData.getGameRules().getRule(GameRules.RULE_SPAWN_CHUNK_RADIUS).set(0, null);

        ServerLevel serverLevel = new ServerLevel(
                console,
                console.executor,
                levelStorageAccess,
                primaryLevelData,
                dimensionKey,
                customStem,
                console.progressListenerFactory.create(primaryLevelData.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS)),
                primaryLevelData.isDebugWorld(),
                i,
                additionalSpawners,
                true,
                console.overworld().getRandomSequences(),
                World.Environment.NORMAL,
                null, null
        );

        if (craftServer.getWorld(name) == null) {
            return null;
        }

        console.addLevel(serverLevel); // Paper - Put world into worldlist before initing the world; move up
        console.initWorld(serverLevel, primaryLevelData, primaryLevelData, primaryLevelData.worldGenOptions());

        serverLevel.setSpawnSettings(true);
        // Paper - Put world into worldlist before initing the world; move up

        console.prepareLevels(serverLevel.getChunkSource().chunkMap.progressListener, serverLevel);
        // TODO: RE-ENABLE THIS
//        serverLevel.entit.tickEntityManager(serverLevel); // SPIGOT-6526: Load pending entities so they are available to the API // Paper - chunk system

        Bukkit.getPluginManager().callEvent(new WorldLoadEvent(serverLevel.getWorld()));
        return serverLevel.getWorld();
    }

    private static String getWorldsFolder(JavaPlugin plugin) {
        return plugin.getDataFolder().getPath().replace("\\", "/") + "/worlds/";
    }
}
