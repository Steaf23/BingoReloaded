package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.core.BingoGameManager;
import io.github.steaf23.bingoreloaded.core.command.*;
import io.github.steaf23.bingoreloaded.core.data.ConfigData;
import io.github.steaf23.bingoreloaded.core.data.DataStorage;
import io.github.steaf23.bingoreloaded.core.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.core.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.core.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.gui.base.MenuEventListener;
import io.github.steaf23.bingoreloaded.gui.base.InventoryItem;
import io.github.steaf23.bingoreloaded.hologram.HologramManager;
import io.github.steaf23.bingoreloaded.core.tasks.statistics.BingoStatistic;
import io.github.steaf23.bingoreloaded.core.player.CustomKit;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BingoReloaded extends JavaPlugin
{
    public static final String NAME = "BingoReloaded";

    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;

    public static boolean usesPlaceholderAPI = false;

    private static BingoReloaded instance;
    private BingoGameManager gameManager;
    private MenuEventListener menuManager;
    private HologramManager hologramManager;
    private ConfigData config;
    private DataStorage dataStorage;


    public BingoReloaded()
    {
        reloadConfig();
        saveDefaultConfig();
    }
    @Override
    public void onEnable()
    {
        ConfigurationSerialization.registerClass(ItemTask.class);
        ConfigurationSerialization.registerClass(AdvancementTask.class);
        ConfigurationSerialization.registerClass(StatisticTask.class);
        ConfigurationSerialization.registerClass(BingoStatistic.class);
        ConfigurationSerialization.registerClass(CustomKit.class);
        ConfigurationSerialization.registerClass(InventoryItem.class);

        this.config = new ConfigData();
        config.loadConfig(this.getConfig());

        this.usesPlaceholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        this.dataStorage = new DataStorage();
        this.gameManager = new BingoGameManager();
        this.menuManager = new MenuEventListener((view) -> {
            return gameManager.doesGameWorldExist(view.getPlayer().getWorld());
        });
        this.hologramManager = new HologramManager();

        PluginCommand bingoCommand = getCommand("bingo");
        if (bingoCommand != null)
        {
            bingoCommand.setExecutor(new BingoCommand(gameManager));
            bingoCommand.setTabCompleter( new BingoTabCompleter());
        }

        PluginCommand autoBingoCommand = getCommand("autobingo");
        if (autoBingoCommand != null)
        {
            autoBingoCommand.setExecutor(new AutoBingoCommand(gameManager));
            autoBingoCommand.setTabCompleter(new AutoBingoTabCompleter(dataStorage.cardsData));
        }

        if (config.enableTeamChat)
        {
            PluginCommand teamChatCommand = getCommand("btc");
            if (teamChatCommand != null)
                teamChatCommand.setExecutor(new TeamChatCommand(gameManager));
        }

//        if (RecoveryCardData.loadCards(game))
//        {
//            game.resume();
//        }

        registerListener(gameManager.getListener());
        registerListener(menuManager);

        Message.log(BingoReloaded.data().translationData.translate("changed"));
        Message.log(ChatColor.GREEN + "Enabled " + this.getName());

//        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "autobingo world create " + ConfigData.instance.defaultTeamSize);

    }

    @Override
    public void onDisable()
    {
        unregisterListener(gameManager.getListener());
        unregisterListener(menuManager);

        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + this.getName());
    }

    private static void registerListener(Listener listener)
    {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    private static void unregisterListener(Listener listener)
    {
        HandlerList.unregisterAll(listener);
    }

    public static BingoReloaded get()
    {
        if (instance == null)
            instance = getPlugin(BingoReloaded.class);
        return instance;
    }

    public static ConfigData config()
    {
        return get().config;
    }

    public static HologramManager holograms()
    {
        return get().hologramManager;
    }

    public static DataStorage data()
    {
        return get().dataStorage;
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task)
    {
        BingoReloaded.scheduleTask(task, 0);
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task, long delay)
    {
        if (delay <= 0)
            Bukkit.getScheduler().runTask(BingoReloaded.get(), task);
        else
            Bukkit.getScheduler().runTaskLater(BingoReloaded.get(), task, delay);
    }
}
