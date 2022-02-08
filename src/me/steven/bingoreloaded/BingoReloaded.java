package me.steven.bingoreloaded;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Objects;

public class BingoReloaded extends JavaPlugin
{
    public static final String NAME = "BingoReloaded";
    public BingoGame game = new BingoGame();

    @Override
    public void onEnable()
    {
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
        Bukkit.getLogger().info(message);

        if (player != null)
        {
            player.sendMessage(message);
        }
    }

    public static void print(String message)
    {
        print(message, null);
    }

    public static void broadcast(String message)
    {
        for (Player p : Bukkit.getServer().getOnlinePlayers())
        {
            print(message, p);
        }
    }
}
