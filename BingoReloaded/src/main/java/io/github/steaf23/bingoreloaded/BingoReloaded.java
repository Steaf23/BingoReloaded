package io.github.steaf23.bingoreloaded;


import io.github.steaf23.bingoreloaded.command.*;
import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.SingularGameManager;
import io.github.steaf23.bingoreloaded.gui.inventory.BingoMenuBoard;
import io.github.steaf23.bingoreloaded.gui.inventory.item.SerializableItem;
import io.github.steaf23.bingoreloaded.hologram.HologramManager;
import io.github.steaf23.bingoreloaded.hologram.HologramPlacer;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.BingoStatistic;
import io.github.steaf23.bingoreloaded.placeholder.BingoReloadedPlaceholderExpansion;
import io.github.steaf23.bingoreloaded.util.Message;
import io.github.steaf23.bingoreloaded.util.bstats.Metrics;
import io.github.steaf23.easymenulib.EasyMenuLibrary;
import io.github.steaf23.easymenulib.inventory.BasicMenu;
import io.github.steaf23.easymenulib.scoreboard.HUDRegistry;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BingoReloaded extends JavaPlugin
{
    public static final String CARD_1_20_4 = "lists_1_20.yml";
    public static final String CARD_1_21 = "lists_1_21.yml";
    public static final String CARD_1_19_4 = "lists_1_19.yml";

    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;
    public static boolean PLACEHOLDER_API_ENABLED = false;

    private static BingoReloaded INSTANCE;

    private ConfigData config;
    private HologramManager hologramManager;
    private HologramPlacer hologramPlacer;
    private GameManager gameManager;
    private BingoMenuBoard menuBoard;
    private HUDRegistry hudRegistry;

    private Metrics bstatsMetrics;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(true)
                .bStats(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        reloadConfig();
        saveDefaultConfig();
        // Kinda ugly, but we can assume there will only be one instance of this class anyway.
        INSTANCE = this;
        PLACEHOLDER_API_ENABLED = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (PLACEHOLDER_API_ENABLED) {
            new BingoReloadedPlaceholderExpansion(this).register();
            Message.log(ChatColor.GREEN + "Enabled Bingo Reloaded Placeholder expansion");
        }

        EasyMenuLibrary.setPlugin(this);
        EasyMenuLibrary.setItemTranslation(key -> {
            return switch (key) {
                case MENU_PREVIOUS -> BingoTranslation.MENU_PREV.translate();
                case MENU_NEXT -> BingoTranslation.MENU_NEXT.translate();
                case MENU_ACCEPT -> BingoTranslation.MENU_ACCEPT.translate();
                case MENU_SAVE_EXIT -> BingoTranslation.MENU_SAVE_EXIT.translate();
                case MENU_FILTER -> BingoTranslation.MENU_FILTER.translate();
                case MENU_CLEAR_FILTER -> BingoTranslation.MENU_CLEAR_FILTER.translate();
            };
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

        BingoTranslation.setLanguage(createYmlDataManager(config.language).getConfig(), createYmlDataManager("languages/en_us.yml").getConfig());
        BasicMenu.pluginTitlePrefix = BingoTranslation.MENU_PREFIX.translate();
        Message.log("" + ChatColor.GREEN + BingoTranslation.CHANGED_LANGUAGE.translate());

        this.hologramManager = new HologramManager();
        this.hologramPlacer = new HologramPlacer(hologramManager);
        WorldData.clearWorlds(this);

        this.menuBoard = new BingoMenuBoard();
        this.hudRegistry = new HUDRegistry();

        if (config.configuration == ConfigData.PluginConfiguration.SINGULAR) {
            this.gameManager = new SingularGameManager(this, config, menuBoard, hudRegistry);
        }
        else {
            this.gameManager = new GameManager(this, config, menuBoard, hudRegistry);
        }

        TabExecutor autoBingoCommand = new AutoBingoCommand(gameManager);

        menuBoard.setPlayerOpenPredicate(player -> player instanceof Player p && this.gameManager.canPlayerOpenMenu(p, null));

        registerCommand("bingo", new BingoCommand(config, gameManager, menuBoard));
        registerCommand("autobingo", autoBingoCommand);
        registerCommand("bingotest", new BingoTestCommand(this, menuBoard));
        if (config.enableTeamChat) {
            TeamChatCommand command = new TeamChatCommand(player -> gameManager.getSessionFromWorld(player.getWorld()));
            registerCommand("btc", command);
            Bukkit.getPluginManager().registerEvents(command, this);
        }

        Message.log(ChatColor.GREEN + "Enabled " + getName());

//        if (RecoveryCardData.loadCards(game))
//        {
//            game.resume();
//        }

        Bukkit.getPluginManager().registerEvents(menuBoard, this);
        Bukkit.getPluginManager().registerEvents(hudRegistry, this);

        bstatsMetrics = new Metrics(this, 22586);
        bstatsMetrics.addCustomChart(new Metrics.SimplePie("selected_language", () -> {
            return config.language.replace(".yml", "");
        }));
        bstatsMetrics.addCustomChart(new Metrics.SimplePie("plugin_configuration", () -> {
            return config.configuration == ConfigData.PluginConfiguration.SINGULAR ? "Singular" : "Multiple";
        }));
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

    public HologramManager holograms() {
        return hologramManager;
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
        String version = Bukkit.getVersion();
        if (version.contains("(MC: 1.19")) {
            return CARD_1_19_4;
        }
        else if (version.contains("(MC: 1.20")) {
            return CARD_1_20_4;
        }
        else if (version.contains("(MC: 1.21")) {
            return CARD_1_21;
        }
        return CARD_1_19_4;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public static void sendPacket(Player player, PacketWrapper packet) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
}
