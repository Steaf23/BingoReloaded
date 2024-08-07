package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.AutoBingoCommand;
import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.command.BingoTestCommand;
import io.github.steaf23.bingoreloaded.command.TeamChatCommand;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.BingoStatData;
import io.github.steaf23.bingoreloaded.data.BingoStatType;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.data.TexturedMenuData;
import io.github.steaf23.bingoreloaded.data.TeamData;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.SingularGameManager;
import io.github.steaf23.bingoreloaded.gui.inventory.BingoMenuBoard;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import io.github.steaf23.bingoreloaded.placeholder.BingoReloadedPlaceholderExpansion;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
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
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.function.Consumer;

public class BingoReloaded extends JavaPlugin
{
    public static final String RESOURCE_PACK_URL = "https://github.com/Steaf23/BingoReloaded/raw/menu-frontend-split-for-resource-pack/resourcepack/BingoReloaded.zip";
    public static final String RESOURCE_PACK_HASH = "6fb0aa69a5c6076eb8d55d964493195588676301";
    public static final ResourcePackInfo RESOURCE_PACK = ResourcePackInfo.resourcePackInfo()
            .uri(URI.create(RESOURCE_PACK_URL))
            .hash(RESOURCE_PACK_HASH).build();

    public static final String CARD_1_20_6 = "lists_1_20.yml";
    public static final String CARD_1_21 = "lists_1_21.yml";

    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;
    public static boolean PLACEHOLDER_API_ENABLED = false;

    private static BingoReloaded INSTANCE;

    private ConfigData config;
    private GameManager gameManager;
    private BingoMenuBoard menuBoard;
    private TexturedMenuData textureData;

    @Override
    public void onLoad() {
        PlayerDisplay.setPlugin(this);
    }

    @Override
    public void onEnable() {
        PlayerDisplay.onPluginEnable();
        reloadConfig();
        saveDefaultConfig();
        // Kinda ugly, but we can assume there will only be one instance of this class anyway.
        INSTANCE = this;
        PLACEHOLDER_API_ENABLED = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (PLACEHOLDER_API_ENABLED) {
            new BingoReloadedPlaceholderExpansion(this).register();
            ConsoleMessenger.log(Component.text("Enabled Bingo Reloaded Placeholder expansion").color(NamedTextColor.GREEN));
        }

        PlayerDisplay.setItemTranslation(key -> switch (key) {
            case MENU_PREVIOUS -> BingoMessage.MENU_PREV.asPhrase();
            case MENU_NEXT -> BingoMessage.MENU_NEXT.asPhrase();
            case MENU_ACCEPT -> BingoMessage.MENU_ACCEPT.asPhrase();
            case MENU_SAVE_EXIT -> BingoMessage.MENU_SAVE_EXIT.asPhrase();
            case MENU_FILTER -> BingoMessage.MENU_FILTER.asPhrase();
            case MENU_CLEAR_FILTER -> BingoMessage.MENU_CLEAR_FILTER.asPhrase();
        });

        ConfigurationSerialization.registerClass(BingoSettings.class);
        ConfigurationSerialization.registerClass(ItemTask.class);
        ConfigurationSerialization.registerClass(AdvancementTask.class);
        ConfigurationSerialization.registerClass(StatisticTask.class);
        ConfigurationSerialization.registerClass(BingoStatistic.class);
        ConfigurationSerialization.registerClass(CustomKit.class);
        ConfigurationSerialization.registerClass(SerializableItem.class);
        ConfigurationSerialization.registerClass(SerializablePlayer.class);
        ConfigurationSerialization.registerClass(TeamData.TeamTemplate.class);

        this.config = new ConfigData(getConfig());

        PlayerDisplay.setUseCustomTextures(config.useIncludedResourcepack);
        BingoMessage.setLanguage(createYmlDataManager(config.language).getConfig(), createYmlDataManager("languages/en_us.yml").getConfig());
        ConsoleMessenger.log(BingoMessage.CHANGED_LANGUAGE.asPhrase().color(NamedTextColor.GREEN));

        BasicMenu.pluginTitlePrefix = BingoMessage.MENU_PREFIX.asPhrase();
        WorldData.clearWorlds(this);

        this.textureData = new TexturedMenuData();
        this.menuBoard = new BingoMenuBoard();
        HUDRegistry hudRegistry = new HUDRegistry();
        if (config.configuration == ConfigData.PluginConfiguration.SINGULAR) {
            this.gameManager = new SingularGameManager(this, config, menuBoard, hudRegistry);
        } else {
            this.gameManager = new GameManager(this, config, menuBoard, hudRegistry);
        }

        TabExecutor autoBingoCommand = new AutoBingoCommand(gameManager);

        menuBoard.setPlayerOpenPredicate(player -> player instanceof Player p && this.gameManager.canPlayerOpenMenus(p));

        registerCommand("bingo", new BingoCommand(config, gameManager, menuBoard));
        registerCommand("autobingo", autoBingoCommand);
        registerCommand("bingotest", new BingoTestCommand(this, menuBoard));
//        registerCommand("bingobot", new BotCommand(gameManager));
        if (config.enableTeamChat) {
            TeamChatCommand command = new TeamChatCommand(player -> gameManager.getSessionFromWorld(player.getWorld()));
            registerCommand("btc", command);
            Bukkit.getPluginManager().registerEvents(command, this);
        }

        ConsoleMessenger.log(Component.text("Enabled " + getName()).color(NamedTextColor.GREEN));

        Bukkit.getPluginManager().registerEvents(menuBoard, this);
        Bukkit.getPluginManager().registerEvents(hudRegistry, this);

        Metrics bStatsMetrics = new Metrics(this, 22586);
        bStatsMetrics.addCustomChart(new Metrics.SimplePie("selected_language",
                () -> config.language.replace(".yml", "").replace("languages/", "")));
        bStatsMetrics.addCustomChart(new Metrics.SimplePie("plugin_configuration",
                () -> config.configuration == ConfigData.PluginConfiguration.SINGULAR ? "Singular" : "Multiple"));
    }

    public void registerCommand(String commandName, TabExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }

    public static YmlDataManager createYmlDataManager(String filepath) {
        return new YmlDataManager(INSTANCE, filepath);
    }

    public void onDisable() {
        if (gameManager != null) {
            gameManager.onPluginDisable();
        }

        HandlerList.unregisterAll(menuBoard);
    }

    public ConfigData config() {
        return config;
    }

    public static void incrementPlayerStat(Player player, BingoStatType stat) {
        boolean savePlayerStatistics = INSTANCE.config.savePlayerStatistics;
        if (savePlayerStatistics) {
            BingoStatData statsData = new BingoStatData();
            statsData.incrementPlayerStat(player, stat);
        }
    }

    public static void setPlayerStat(Player player, BingoStatType stat, int value) {
        boolean savePlayerStatistics = INSTANCE.config.savePlayerStatistics;
        if (savePlayerStatistics) {
            BingoStatData statsData = new BingoStatData();
            statsData.setPlayerStat(player.getUniqueId(), stat, value);
        }
    }

    public static int getPlayerStat(Player player, BingoStatType stat) {
        boolean savePlayerStatistics = INSTANCE.config.savePlayerStatistics;
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
        String version = Bukkit.getMinecraftVersion();
        if (version.contains("1.20")) {
            return CARD_1_20_6;
        } else if (version.contains("1.21")) {
            return CARD_1_21;
        }
        return CARD_1_20_6;
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
}
