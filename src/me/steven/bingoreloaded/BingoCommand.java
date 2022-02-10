package me.steven.bingoreloaded;

import me.steven.bingoreloaded.GUIInventories.BingoOptionsUI;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BingoCommand implements CommandExecutor
{
    public BingoGame gameInstance;

    public BingoCommand(BingoGame game)
    {
        gameInstance = game;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
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
                default:
                    if (commandSender instanceof Player p)
                        BingoReloaded.print(ChatColor.RED + "Usage: /bingo [start|end|leave]", p);
                    else
                    {
                        BingoReloaded.print(ChatColor.RED + "Usage: /bingo [start|end|leave]");
                    }
            }
        }
        else
        {
            if (commandSender instanceof Player player)
            {
                if (gameInstance.isGameInProgress())
                {
                    TextComponent[] message = BingoReloaded.createHoverCommandMessage(
                            "Cannot open the settings menu while a game is still in progress, end it using ",
                            "/bingo end",
                            "/bingo end",
                            "Or click here to end the game ;)");

                    player.spigot().sendMessage(message);
                    return false;
                }

                BingoOptionsUI.open(player, gameInstance);
            }
        }

        return false;
    }
}
