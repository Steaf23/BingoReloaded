package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.*;
import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.gui.UIManager;
import io.github.steaf23.bingoreloaded.item.tasks.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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
        ConfigurationSerialization.registerClass(ItemTask.class, "Bingo.ItemTask");
        ConfigurationSerialization.registerClass(AdvancementTask.class, "Bingo.AdvancementTask");
        ConfigurationSerialization.registerClass(StatisticTask.class, "Bingo.StatisticTask");
        ConfigurationSerialization.registerClass(BingoTask.class, "Bingo.Task");
        ConfigurationSerialization.registerClass(BingoStatistic.class, "Bingo.Statistic");

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
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "autobingo world create 3");

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
}
