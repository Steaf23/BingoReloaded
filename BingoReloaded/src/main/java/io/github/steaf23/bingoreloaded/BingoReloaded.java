package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.AutoBingoCommand;
import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.command.BingoConfigCommand;
import io.github.steaf23.bingoreloaded.command.BingoTestCommand;
import io.github.steaf23.bingoreloaded.command.TeamChatCommand;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoStatData;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.DataUpdaterV1;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.data.TexturedMenuData;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.data.core.DataAccessor;
import io.github.steaf23.bingoreloaded.data.core.DataStorageSerializerRegistry;
import io.github.steaf23.bingoreloaded.data.core.VirtualDataAccessor;
import io.github.steaf23.bingoreloaded.data.core.configuration.ConfigDataAccessor;
import io.github.steaf23.bingoreloaded.data.core.configuration.YamlDataAccessor;
import io.github.steaf23.bingoreloaded.data.core.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.core.tag.TagDataAccessor;
import io.github.steaf23.bingoreloaded.data.serializers.BingoSettingsStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.BingoStatisticStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.CustomKitStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.ItemStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.PlayerStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.TaskStorageSerializer;
import io.github.steaf23.bingoreloaded.data.serializers.TeamTemplateStorageSerializer;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.SingularGameManager;
import io.github.steaf23.bingoreloaded.gui.inventory.BingoMenuBoard;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import io.github.steaf23.bingoreloaded.placeholder.BingoReloadedPlaceholderExpansion;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.data.TaskData;
import io.github.steaf23.bingoreloaded.util.bstats.Metrics;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import io.github.steaf23.playerdisplay.inventory.BasicMenu;
import io.github.steaf23.playerdisplay.scoreboard.HUDRegistry;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class BingoReloaded extends JavaPlugin
{
    public static final String RESOURCE_PACK_URL = "https://github.com/Steaf23/BingoReloaded/raw/menu-frontend-split-for-resource-pack/resourcepack/BingoReloaded.zip";
    public static final String RESOURCE_PACK_HASH = "6fb0aa69a5c6076eb8d55d964493195588676301";
    public static final ResourcePackInfo RESOURCE_PACK = ResourcePackInfo.resourcePackInfo()
            .uri(URI.create(RESOURCE_PACK_URL))
            .hash(RESOURCE_PACK_HASH).build();

    public static final String CARD_1_21 = "lists_1_21";

    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;
    public static boolean PLACEHOLDER_API_ENABLED = false;

    private static BingoReloaded INSTANCE;

    private BingoConfigurationData config;
    private GameManager gameManager;
    private BingoMenuBoard menuBoard;
    private TexturedMenuData textureData;
    private HUDRegistry hudRegistry;

    @Override
    public void onLoad() {
        // Kinda ugly, but we can assume there will only be one instance of this class anyway.
        INSTANCE = this;
        PlayerDisplay.setPlugin(this);
    }

    @Override
    public void onEnable() {
        PlayerDisplay.onPluginEnable();

        saveResource("bingoreloaded.zip", true);
        saveResource("bingoreloaded_lite.zip", true);

        // Data file updaters
        {
            DataUpdaterV1 updater = new DataUpdaterV1(this);
            updater.update();
        }

        setupConfig();

        DataStorageSerializerRegistry.addSerializer(new CustomKitStorageSerializer(), CustomKit.class);
        DataStorageSerializerRegistry.addSerializer(new TaskStorageSerializer(), TaskData.class);
        DataStorageSerializerRegistry.addSerializer(new PlayerStorageSerializer(), SerializablePlayer.class);
        DataStorageSerializerRegistry.addSerializer(new TeamTemplateStorageSerializer(), TeamData.TeamTemplate.class);
        DataStorageSerializerRegistry.addSerializer(new BingoSettingsStorageSerializer(), BingoSettings.class);
        DataStorageSerializerRegistry.addSerializer(new BingoStatisticStorageSerializer(), BingoStatistic.class);
        DataStorageSerializerRegistry.addSerializer(new ItemStorageSerializer(), SerializableItem.class);

        // Create data accessors
        addDataAccessor(new YamlDataAccessor(this, "scoreboards", false));
        addDataAccessor(new YamlDataAccessor(this, "placeholders", false));
        addDataAccessor(new TagDataAccessor(this, "data/cards", false));
        addDataAccessor(new TagDataAccessor(this, "data/textures", false));
        addDataAccessor(new TagDataAccessor(this, "data/kits", false));
        addDataAccessor(new TagDataAccessor(this, "data/" + getDefaultTasksVersion(), false));
        addDataAccessor(new TagDataAccessor(this, "data/presets", false));
        addDataAccessor(new TagDataAccessor(this, "data/player_stats", false));
        addDataAccessor(new TagDataAccessor(this, "data/teams", false));
        addDataAccessor(new TagDataAccessor(this, "data/players", false));

        PLACEHOLDER_API_ENABLED = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (PLACEHOLDER_API_ENABLED) {
            new BingoReloadedPlaceholderExpansion(this).register();
            ConsoleMessenger.log(Component.text("Enabled Bingo Reloaded Placeholder expansion").color(NamedTextColor.GREEN));
        }

        this.config = new BingoConfigurationData(new ConfigDataAccessor(this));
        PlayerDisplay.enableDebugLogging(config.getOptionValue(BingoOptions.ENABLE_DEBUG_LOGGING));

        PlayerDisplay.setUseCustomTextures(config.getOptionValue(BingoOptions.USE_INCLUDED_RESOURCE_PACK));
        String language = config.getOptionValue(BingoOptions.LANGUAGE).replace(".yml", "");
        setLanguage(language);

        BasicMenu.pluginTitlePrefix = BingoMessage.MENU_PREFIX.asPhrase();

        this.textureData = new TexturedMenuData();
        this.menuBoard = new BingoMenuBoard();
        this.hudRegistry = new HUDRegistry();

        reloadManager();

        Bukkit.getPluginManager().registerEvents(menuBoard, this);
        Bukkit.getPluginManager().registerEvents(hudRegistry, this);

        Metrics bStatsMetrics = new Metrics(this, 22586);
        bStatsMetrics.addCustomChart(new Metrics.SimplePie("selected_language",
                () -> config.getOptionValue(BingoOptions.LANGUAGE).replace(".yml", "").replace("languages/", "")));
        bStatsMetrics.addCustomChart(new Metrics.SimplePie("plugin_configuration",
                () -> config.getOptionValue(BingoOptions.CONFIGURATION) == BingoOptions.PluginConfiguration.SINGULAR ? "Singular" : "Multiple"));

        ConsoleMessenger.log(Component.text("Enabled " + getName()).color(NamedTextColor.GREEN));
    }

    public void registerCommand(String commandName, TabExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }

    public void onDisable() {
        if (gameManager != null) {
            gameManager.onPluginDisable();
        }

        HandlerList.unregisterAll(menuBoard);
        PlayerDisplay.disable();
    }

    public BingoConfigurationData config() {
        return config;
    }

    public static void incrementPlayerStat(Player player, BingoStatType stat) {
        boolean savePlayerStatistics = INSTANCE.config.getOptionValue(BingoOptions.SAVE_PLAYER_STATISTICS);
        if (savePlayerStatistics) {
            BingoStatData statsData = new BingoStatData();
            statsData.incrementPlayerStat(player, stat);
        }
    }

    public static void setPlayerStat(Player player, BingoStatType stat, int value) {
        boolean savePlayerStatistics = INSTANCE.config.getOptionValue(BingoOptions.SAVE_PLAYER_STATISTICS);
        if (savePlayerStatistics) {
            BingoStatData statsData = new BingoStatData();
            statsData.setPlayerStat(player.getUniqueId(), stat, value);
        }
    }

    public static int getPlayerStat(Player player, BingoStatType stat) {
        boolean savePlayerStatistics = INSTANCE.config.getOptionValue(BingoOptions.SAVE_PLAYER_STATISTICS);
        if (savePlayerStatistics) {
            BingoStatData statsData = new BingoStatData();
            return statsData.getPlayerStat(player.getUniqueId(), stat);
        }
        return 0;
    }

    public static boolean areAdvancementsDisabled() {
        return !Bukkit.advancementIterator().hasNext() || Bukkit.advancementIterator().next() == null;
    }

    public static BingoReloaded getInstance() {
        return INSTANCE;
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task) {
        BingoReloaded.scheduleTask(task, 0);
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task, long delay) {
        if (delay <= 0)
            Bukkit.getScheduler().runTask(INSTANCE, task);
        else
            Bukkit.getScheduler().runTaskLater(INSTANCE, task, delay);
    }

    public static String getDefaultTasksVersion() {
        return CARD_1_21;
    }

    public static void sendResourcePack(Player player) {
        if (!PlayerDisplay.useCustomTextures()) {
            return;
        }
        player.sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                .packs(RESOURCE_PACK)
                .required(true)
                .build());
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public TexturedMenuData getTextureData() {
        return textureData;
    }

    private static final Map<String, DataAccessor> accessorMap = new HashMap<>();

    @NotNull
    public static DataAccessor getDataAccessor(@NotNull String location) {
        if (location.isEmpty()) {
            ConsoleMessenger.bug("No location specified for data accessor, returning empty data accessor.", BingoReloaded.getInstance());
            return new VirtualDataAccessor(location);
        }

        if (!accessorMap.containsKey(location)) {
            ConsoleMessenger.bug("No data accessor exists for the specified location (" + location + "), returning empty data accessor.", BingoReloaded.getInstance());
            return new VirtualDataAccessor(location);
        }

        return accessorMap.get(location);
    }

    public static DataAccessor addDataAccessor(DataAccessor accessor) {
        if (!containsDataAccessor(accessor.getLocation())) {
            accessorMap.put(accessor.getLocation(), accessor);
        }

        accessor.load();
        return accessor;
    }

    public static boolean containsDataAccessor(String name) {
        return accessorMap.containsKey(name);
    }

    public void reloadConfigFromFile() {
        setupConfig();
        this.config.reload();
    }

    public void reloadScoreboards() {
        getDataAccessor("scoreboards").load();
    }

    public void reloadPlaceholders() {
        getDataAccessor("placeholders").load();
    }

    public void reloadData() {
        getDataAccessor("data/cards").load();
        getDataAccessor("data/textures").load();
        getDataAccessor("data/kits").load();
        getDataAccessor("data/" + getDefaultTasksVersion()).load();
        getDataAccessor("data/presets").load();
        getDataAccessor("data/player_stats").load();
        getDataAccessor("data/teams").load();
        getDataAccessor("data/players").load();
    }

    public void reloadLanguage() {
        ConsoleMessenger.warn("Reloading languages, however due to how plugins are loaded this may not affect all text");
        ConsoleMessenger.warn("To fully reload the languages restart the server.");
        String selectedLanguage = config.getOptionValue(BingoOptions.LANGUAGE);
        String langString = selectedLanguage.replace(".yml", "");

        setLanguage(langString);
    }

    private void setupConfig() {
        saveConfig();
        saveDefaultConfig();

        // load default config
        YamlConfiguration defaultConfigFull = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("config.yml")));

        // load current user config to copy values from
        FileConfiguration userConfig = getConfig();

        for (String key : userConfig.getKeys(true)) {
            if (defaultConfigFull.contains(key)) {
                defaultConfigFull.set(key, userConfig.get(key));
            }
        }

        try {
            defaultConfigFull.save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            ConsoleMessenger.bug("Could not update config.yml to new version", this);
        }
    }

    public void setLanguage(String language) {
        language = "languages/" + language;

        // only reload the language when it exists
        if (this.getResource(language + ".yml") != null) {
            DataAccessor newLang = addDataAccessor(new YamlDataAccessor(this, language, false));
            BingoMessage.setLanguage(newLang, addDataAccessor(new YamlDataAccessor(this, "languages/en_us", false)));

            PlayerDisplay.setItemTranslation(key -> switch (key) {
                case MENU_PREVIOUS -> BingoMessage.MENU_PREV.asPhrase();
                case MENU_NEXT -> BingoMessage.MENU_NEXT.asPhrase();
                case MENU_ACCEPT -> BingoMessage.MENU_ACCEPT.asPhrase();
                case MENU_SAVE_EXIT -> BingoMessage.MENU_SAVE_EXIT.asPhrase();
                case MENU_FILTER -> BingoMessage.MENU_FILTER.asPhrase();
                case MENU_CLEAR_FILTER -> BingoMessage.MENU_CLEAR_FILTER.asPhrase();
            });

            ConsoleMessenger.log(BingoMessage.CHANGED_LANGUAGE.asPhrase().color(NamedTextColor.GREEN));
        } else {
            ConsoleMessenger.error("Could not set language, a translation for language '" + language + "' could not be found!");
        }
    }

    public void reloadManager() {
        if (gameManager != null) {
            for (String name : gameManager.getSessionNames()) {
                gameManager.endGame(name);

                // Reset player data regardless of whether saving player information is enabled, because we need to begin with a clean slate.
                for (UUID playerId : gameManager.getPlayerData().getSavedPlayers()) {
                    Player player = Bukkit.getPlayer(playerId);

                    if (player != null) {
                        gameManager.getPlayerData().loadPlayer(player);
                    }
                }

                gameManager.destroySession(name);
            }
            gameManager.onPluginDisable();
        }

        if (config.getOptionValue(BingoOptions.CONFIGURATION) == BingoOptions.PluginConfiguration.SINGULAR) {
            this.gameManager = new SingularGameManager(this, config, menuBoard, hudRegistry);
        } else {
            this.gameManager = new GameManager(this, config, menuBoard, hudRegistry);
        }

        this.gameManager.setup(config.getOptionValue(BingoOptions.DEFAULT_WORLDS));

        menuBoard.setPlayerOpenPredicate(player -> player instanceof Player p && this.gameManager.canPlayerOpenMenus(p));

        TabExecutor autoBingoCommand = new AutoBingoCommand(gameManager);
        TabExecutor bingoConfigCommand = new BingoConfigCommand(config);

        registerCommand("bingo", new BingoCommand(this, config, gameManager, menuBoard));
        registerCommand("autobingo", autoBingoCommand);
        registerCommand("bingoconfig", bingoConfigCommand);
        registerCommand("bingotest", new BingoTestCommand(this, menuBoard));
        if (config.getOptionValue(BingoOptions.ENABLE_TEAM_CHAT)) {
            TeamChatCommand command = new TeamChatCommand(player -> gameManager.getSessionFromWorld(player.getWorld()));
            registerCommand("btc", command);
            Bukkit.getPluginManager().registerEvents(command, this);
        }
    }
}
