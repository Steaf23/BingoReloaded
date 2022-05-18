package me.steven.bingoreloaded.command;

import com.sun.security.auth.login.ConfigFile;
import me.steven.bingoreloaded.BingoGame;
import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.data.ConfigData;
import me.steven.bingoreloaded.gui.BingoOptionsUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BingoCommand implements CommandExecutor
{
    private final BingoGame gameInstance;

    public BingoCommand(BingoGame game)
    {
        gameInstance = game;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args)
    {
        if (args.length > 0)
        {
            switch (args[0])
            {
                case "leave":
                    if (!(commandSender instanceof Player player)) return false;

                    gameInstance.playerQuit(player);
                    break;

                case "start":
                    gameInstance.start();
                    break;

                case "end":
                    gameInstance.end();
                    break;

                case "getcard":
                    if (commandSender instanceof Player p)
                    {
                        gameInstance.returnCardToPlayer(p);
                    }
                    break;

                case "back":
                    if (commandSender instanceof Player p)
                    {
                        if (ConfigData.getConfig().teleportAfterDeath)
                        {
                            gameInstance.teleportPlayerAfterDeath(p);
                        }
                    }
                    break;
                case "deathmatch":
                    if (gameInstance.isGameInProgress())
                    {
                        gameInstance.startDeathMatch(3);
                    }
                    else
                    {
                        if (commandSender instanceof Player p)
                            BingoReloaded.print(ChatColor.RED + "Cannot start a death match when there is no game active!", p);
                        else
                            BingoReloaded.print(ChatColor.RED + "Cannot start a death match when there is no game active!");
                    }
                    break;

                default:
                    if (commandSender instanceof Player p)
                        BingoReloaded.print(ChatColor.RED + "Usage: /bingo [start|end|leave|getcard|deathmatch]", p);
                    else
                        BingoReloaded.print(ChatColor.RED + "Usage: /bingo [start|end|leave|getcard|deathmatch]");
                    break;
            }
        }
        else
        {
            if (commandSender instanceof Player player)
            {
                BingoOptionsUI.open(player, gameInstance);
            }
        }
        return false;
    }
}