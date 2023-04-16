package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.AutoBingoTabCompleter;
import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.command.TeamChatCommand;
import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuEventListener;
import io.github.steaf23.bingoreloaded.hologram.HologramManager;
import io.github.steaf23.bingoreloaded.player.CustomKit;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.statistics.BingoStatistic;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class BingoReloadedCore extends JavaPlugin
{
    public static final String NAME = "BingoReloadedCore";
    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;
    public static boolean usesPlaceholderAPI = false;

    private MenuEventListener menuManager;
    private HologramManager hologramManager;
    private ConfigData config;
    private BingoReloadedExtension extensionPlugin;
    private final Function<Player, BingoSession> bingoSessionResolver;


    public BingoReloadedCore(BingoReloadedExtension plugin, Function<Player, BingoSession> sessionResolver)
    {
        this.extensionPlugin = plugin;
        this.bingoSessionResolver = sessionResolver;
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
    }

    @Override
    public void onEnable()
    {
        ConfigurationSerialization.registerClass(BingoSettings.class);
        ConfigurationSerialization.registerClass(ItemTask.class);
        ConfigurationSerialization.registerClass(AdvancementTask.class);
        ConfigurationSerialization.registerClass(StatisticTask.class);
        ConfigurationSerialization.registerClass(BingoStatistic.class);
        ConfigurationSerialization.registerClass(CustomKit.class);
        ConfigurationSerialization.registerClass(InventoryItem.class);

        this.config = new ConfigData();
        config.loadConfig(extensionPlugin.getConfig());

        usesPlaceholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        this.hologramManager = new HologramManager();

//        if (RecoveryCardData.loadCards(game))
//        {
//            game.resume();
//        }

        BingoTranslation.setLanguage(new YmlDataManager(extensionPlugin, config.language).getConfig(), new YmlDataManager(extensionPlugin, "language/en_us.yml").getConfig());
        Message.log("" + ChatColor.GREEN + BingoTranslation.CHANGED_LANGUAGE);
//        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "autobingo world create " + ConfigData.instance.defaultTeamSize);
    }

    protected void registerBingoCommand(String commandName)
    {
        registerCommand(commandName, new BingoCommand(config)
        {
            @Override
            public BingoSession getSession(Player player)
            {
                return bingoSessionResolver.apply(player);
            }
        }, new AutoBingoTabCompleter());
    }

    protected void registerTeamChatCommand(String commandName)
    {
        registerCommand(commandName, new TeamChatCommand(bingoSessionResolver), null);
        if (config.enableTeamChat)
        {
            PluginCommand teamChatCommand = extensionPlugin.getCommand("btc");
            if (teamChatCommand != null)
                teamChatCommand.setExecutor(new TeamChatCommand(bingoSessionResolver));
        }
    }

    protected void registerCommand(String commandName, CommandExecutor executor, @Nullable TabCompleter tabCompleter)
    {
        PluginCommand command = extensionPlugin.getCommand(commandName);
        if (command != null)
        {
            command.setExecutor(executor);
            command.setTabCompleter(tabCompleter);
        }
    }

    public static String getWorldNameOfDimension(World dimension)
    {
        return dimension.getName()
                .replace("_nether", "")
                .replace("_the_end", "");
    }

    public static YmlDataManager createYmlDataManager(String filepath)
    {
        return new YmlDataManager(Bukkit.getPluginManager().getPlugin(BingoReloadedCore.NAME), filepath);
    }

    @Override
    public void onDisable()
    {

    }

    public ConfigData config()
    {
        return config;
    }

    public HologramManager holograms()
    {
        return hologramManager;
    }

    public static void incrementPlayerStat(Player player, BingoStatType stat)
    {
        BingoStatsData statsData = new BingoStatsData(BingoReloadedCore.getPlugin(BingoReloadedCore.class).config.savePlayerStatistics);
        statsData.incrementPlayerStat(player, stat);
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task)
    {
        BingoReloadedCore.scheduleTask(task, 0);
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task, long delay)
    {
        if (delay <= 0)
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin(BingoReloadedCore.NAME), task);
        else
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin(BingoReloadedCore.NAME), task, delay);
    }
}
