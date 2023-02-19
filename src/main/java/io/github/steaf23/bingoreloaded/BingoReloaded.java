package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.*;
import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.gui.UIManager;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.*;
import io.github.steaf23.bingoreloaded.item.ItemCooldownManager;
import io.github.steaf23.bingoreloaded.player.CustomKit;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BingoReloaded extends JavaPlugin
{
    public static final String NAME = "BingoReloaded";

    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;

    public static boolean usesPlaceholder = false;

    private GameWorldManager gameManager;
    private BingoEventManager eventManager;

    @Override
    public void onEnable()
    {
        reloadConfig();
        saveDefaultConfig();
        ConfigData.instance.loadConfig(this.getConfig());
        ConfigurationSerialization.registerClass(ItemTask.class);
        ConfigurationSerialization.registerClass(AdvancementTask.class);
        ConfigurationSerialization.registerClass(StatisticTask.class);
        ConfigurationSerialization.registerClass(BingoStatistic.class);
        ConfigurationSerialization.registerClass(CustomKit.class);
        ConfigurationSerialization.registerClass(InventoryItem.class);

        usesPlaceholder = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        gameManager = GameWorldManager.get();
        eventManager = new BingoEventManager();
        // create singletons.
        UIManager.create();
        ItemCooldownManager.create();

        PluginCommand bingoCommand = getCommand("bingo");
        if (bingoCommand != null)
        {
            bingoCommand.setExecutor(new BingoCommand());
            bingoCommand.setTabCompleter( new BingoTabCompleter());
        }

        PluginCommand autoBingoCommand = getCommand("autobingo");
        if (autoBingoCommand != null)
        {
            autoBingoCommand.setExecutor(new AutoBingoCommand());
            autoBingoCommand.setTabCompleter(new AutoBingoTabCompleter());
        }

        if (ConfigData.instance.enableTeamChat)
        {
            PluginCommand teamChatCommand = getCommand("btc");
            if (teamChatCommand != null)
                teamChatCommand.setExecutor(new TeamChatCommand());
        }

//        if (RecoveryCardData.loadCards(game))
//        {
//            game.resume();
//        }

        Message.log(TranslationData.translate("changed"));
        Message.log(ChatColor.GREEN + "Enabled " + this.getName());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "autobingo world create " + ConfigData.instance.defaultTeamSize);

    }

    @Override
    public void onDisable()
    {
        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + this.getName());
    }

    public static void registerListener(Listener listener)
    {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public static BingoReloaded get()
    {
        return getPlugin(BingoReloaded.class);
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
