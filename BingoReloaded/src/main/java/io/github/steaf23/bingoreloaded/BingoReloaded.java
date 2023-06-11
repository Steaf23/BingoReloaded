package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.command.BingoCommand;
import io.github.steaf23.bingoreloaded.command.BingoTabCompleter;
import io.github.steaf23.bingoreloaded.command.TeamChatCommand;
import io.github.steaf23.bingoreloaded.data.*;
import io.github.steaf23.bingoreloaded.data.helper.SerializablePlayer;
import io.github.steaf23.bingoreloaded.gameloop.BingoGameManager;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.multiple.MultiAutoBingoCommand;
import io.github.steaf23.bingoreloaded.gameloop.multiple.MultiGameManager;
import io.github.steaf23.bingoreloaded.gameloop.singular.SimpleAutoBingoCommand;
import io.github.steaf23.bingoreloaded.gameloop.singular.SingularGameManager;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.hologram.HologramManager;
import io.github.steaf23.bingoreloaded.settings.CustomKit;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.tasks.AdvancementTask;
import io.github.steaf23.bingoreloaded.tasks.ItemTask;
import io.github.steaf23.bingoreloaded.tasks.StatisticTask;
import io.github.steaf23.bingoreloaded.tasks.statistics.BingoStatistic;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class BingoReloaded extends JavaPlugin
{
    public static final String NAME = "BingoReloaded";
    // Amount of ticks per second.
    public static final int ONE_SECOND = 20;
    public static boolean usesPlaceholderAPI = false;

    private ConfigData config;
    private HologramManager hologramManager;
    private BingoGameManager gameManager;

    public BingoReloaded()
    {
        reloadConfig();
        saveDefaultConfig();
    }

    @Override
    public void onEnable()
    {
        usesPlaceholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        ConfigurationSerialization.registerClass(BingoSettings.class);
        ConfigurationSerialization.registerClass(ItemTask.class);
        ConfigurationSerialization.registerClass(AdvancementTask.class);
        ConfigurationSerialization.registerClass(StatisticTask.class);
        ConfigurationSerialization.registerClass(BingoStatistic.class);
        ConfigurationSerialization.registerClass(CustomKit.class);
        ConfigurationSerialization.registerClass(MenuItem.class);
        ConfigurationSerialization.registerClass(SerializablePlayer.class);

        this.config = new ConfigData(getConfig());

        BingoTranslation.setLanguage(createYmlDataManager(config.language).getConfig(), createYmlDataManager("languages/en_us.yml").getConfig());
        Message.log("" + ChatColor.GREEN + BingoTranslation.CHANGED_LANGUAGE.translate());

        this.hologramManager = new HologramManager();

        CommandExecutor autoBingoCommand;

        if (config.configuration == ConfigData.PluginConfiguration.SINGULAR)
        {
            this.gameManager = new SingularGameManager(this);
            autoBingoCommand = new SimpleAutoBingoCommand(gameManager);
        }
        else
        {
            this.gameManager = new MultiGameManager(this);
            autoBingoCommand = new MultiAutoBingoCommand((MultiGameManager)gameManager);
        }

        registerCommand("bingo", new BingoCommand(config, gameManager), new BingoTabCompleter());
        registerCommand("autobingo", autoBingoCommand, null);

        Message.log(ChatColor.GREEN + "Enabled " + getName());

//        if (RecoveryCardData.loadCards(game))
//        {
//            game.resume();
//        }
    }

    public void registerTeamChatCommand(String commandName, Function<Player, BingoSession> bingoSessionResolver)
    {
        registerCommand(commandName, new TeamChatCommand(bingoSessionResolver), null);
    }

    public void registerCommand(String commandName, CommandExecutor executor, @Nullable TabCompleter tabCompleter)
    {
        PluginCommand command = getCommand(commandName);
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
        return new YmlDataManager(getPlugin(BingoReloaded.class), filepath);
    }

    public void onDisable()
    {
        gameManager.onDisable();
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
        boolean savePlayerStatistics = getPlugin(BingoReloaded.class).config.savePlayerStatistics;
        if (savePlayerStatistics)
        {
            BingoStatsData statsData = new BingoStatsData();
            statsData.incrementPlayerStat(player, stat);
        }
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task)
    {
        BingoReloaded.scheduleTask(task, 0);
    }

    public static void scheduleTask(@NotNull Consumer<BukkitTask> task, long delay)
    {
        if (delay <= 0)
            Bukkit.getScheduler().runTask(getPlugin(BingoReloaded.class), task);
        else
            Bukkit.getScheduler().runTaskLater(getPlugin(BingoReloaded.class), task, delay);
    }
}
