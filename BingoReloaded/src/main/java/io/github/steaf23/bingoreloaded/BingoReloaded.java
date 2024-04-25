package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.command.BingoTestCommand;
import io.github.steaf23.bingoreloaded.command.TeamChatCommand;
import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.data.helper.YmlDataManager;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.multiple.MultiAutoBingoCommand;
import io.github.steaf23.bingoreloaded.gameloop.singular.SimpleAutoBingoCommand;
import io.github.steaf23.bingoreloaded.gui.base.BasicMenu;
import io.github.steaf23.bingoreloaded.gui.base.BingoMenuManager;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.hologram.HologramManager;
import io.github.steaf23.bingoreloaded.hologram.HologramPlacer;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.statistics.BingoStatistic;
import io.github.steaf23.bingoreloaded.util.Message;
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

    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;
    public static boolean usesPlaceholderAPI = false;

    private static BingoReloaded instance;

    private ConfigData config;
    private HologramManager hologramManager;
    private HologramPlacer hologramPlacer;
    private GameManager gameManager;
    private BingoMenuManager menuManager;

    @Override
    public void onEnable() {
        reloadConfig();
        saveDefaultConfig();
        // Kinda ugly, but we can assume there will only be one instance of this class anyway.
        instance = this;
        usesPlaceholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        ConfigurationSerialization.registerClass(BingoSettings.class);
        ConfigurationSerialization.registerClass(ItemTask.class);
        ConfigurationSerialization.registerClass(AdvancementTask.class);
        ConfigurationSerialization.registerClass(StatisticTask.class);
        ConfigurationSerialization.registerClass(BingoStatistic.class);
        ConfigurationSerialization.registerClass(CustomKit.class);
        ConfigurationSerialization.registerClass(MenuItem.class);
        ConfigurationSerialization.registerClass(SerializablePlayer.class);
        ConfigurationSerialization.registerClass(TeamData.TeamTemplate.class);

        this.config = new ConfigData(getConfig());

        BingoTranslation.setLanguage(createYmlDataManager(config.language).getConfig(), createYmlDataManager("languages/en_us.yml").getConfig());
        BasicMenu.pluginTitlePrefix = BingoTranslation.MENU_PREFIX.translate();
        Message.log("" + ChatColor.GREEN + BingoTranslation.CHANGED_LANGUAGE.translate());

        this.hologramManager = new HologramManager();
        this.hologramPlacer = new HologramPlacer(hologramManager);
        WorldData.clearWorlds(this);

        TabExecutor autoBingoCommand;

        this.menuManager = new BingoMenuManager();

        this.gameManager = new GameManager(this, config, menuManager);
        if (config.configuration == ConfigData.PluginConfiguration.SINGULAR) {
            autoBingoCommand = new SimpleAutoBingoCommand(gameManager);
        } else {
            autoBingoCommand = new MultiAutoBingoCommand(gameManager);
        }

        menuManager.setPlayerOpenPredicate(player -> player instanceof Player p && this.gameManager.canPlayerOpenMenu(p, null));

        registerCommand("bingo", new BingoCommand(config, gameManager, menuManager));
        registerCommand("autobingo", autoBingoCommand);
        registerCommand("bingotest", new BingoTestCommand(this));
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

        Bukkit.getPluginManager().registerEvents(menuManager, this);
    }

    public void registerCommand(String commandName, TabExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }

    public static YmlDataManager createYmlDataManager(String filepath) {
        return new YmlDataManager(instance, filepath);
    }

    public void onDisable() {
        if (gameManager != null) {
            gameManager.onPluginDisable();
        }

        HandlerList.unregisterAll(menuManager);
    }

    public ConfigData config() {
        return config;
    }

    public HologramManager holograms() {
        return hologramManager;
    }

    public static void incrementPlayerStat(Player player, BingoStatType stat) {
        boolean savePlayerStatistics = instance.config.savePlayerStatistics;
        if (savePlayerStatistics) {
            BingoStatData statsData = new BingoStatData();
            statsData.incrementPlayerStat(player, stat);
        }
    }

    public static boolean areAdvancementsDisabled() {
        return !Bukkit.advancementIterator().hasNext() || Bukkit.advancementIterator().next() == null;
    }

    public static BingoReloaded getInstance() {
        return instance;
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task) {
        BingoReloaded.scheduleTask(task, 0);
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task, long delay) {
        if (delay <= 0)
            Bukkit.getScheduler().runTask(instance, task);
        else
            Bukkit.getScheduler().runTaskLater(instance, task, delay);
    }

    public static String getDefaultTasksVersion() {
        String version = Bukkit.getVersion();
        return CARD_1_20_4;
    }
}
