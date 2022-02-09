package me.steven.bingoreloaded;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public class BingoReloaded extends JavaPlugin
{
    public static final String PRINT_PREFIX = "" + ChatColor.DARK_RED + "[" + ChatColor.GOLD + ChatColor.BOLD + "Bingo-" + ChatColor.YELLOW + ChatColor.ITALIC + "Reloaded" + ChatColor.DARK_RED + "] " + ChatColor.RESET + "";
    public static final String NAME = "BingoReloaded";
    public BingoGame game;

    @Override
    public void onEnable()
    {
        game = new BingoGame();
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());
        this.getCommand("bingo").setExecutor(new BingoCommand(game));
        getServer().getPluginManager().registerEvents(game, this);
    }

    @Override
    public void onDisable()
    {
        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + this.getName());
    }

    public static void print(String message, @Nullable Player player)
    {
        message = PRINT_PREFIX + message;
        if (player != null)
        {
            player.sendMessage(message);
        }
    }

    public static void print(String message)
    {
        message = PRINT_PREFIX + message;
        Bukkit.getLogger().info(message);
    }

    public static void broadcast(String message)
    {
        for (Player p : Bukkit.getServer().getOnlinePlayers())
        {
            print(message, p);
        }
    }
}
