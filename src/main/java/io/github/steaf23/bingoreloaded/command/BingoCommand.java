package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.BingoOptionsUI;
import io.github.steaf23.bingoreloaded.gui.creator.CardCreatorUI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
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
                case "join":
                    if (!(commandSender instanceof Player player && player.hasPermission("bingo.player"))) return false;

                    gameInstance.getTeamManager().openTeamSelector(player, null);
                    break;
                case "leave":
                    if (!(commandSender instanceof Player player && player.hasPermission("bingo.player"))) return false;

                    gameInstance.playerQuit(player);
                    break;

                case "start":
                    if (commandSender instanceof Player p && p.hasPermission("bingo.settings"))
                    {
                        gameInstance.start();
                        return true;
                    }
                    break;

                case "end":
                    if (!(commandSender instanceof Player p) || p.hasPermission("bingo.settings"))
                    gameInstance.end();
                    break;

                case "getcard":
                    if (commandSender instanceof Player p && p.hasPermission("bingo.player"))
                    {
                        gameInstance.returnCardToPlayer(p);
                        return true;
                    }
                    break;

                case "back":
                    if (commandSender instanceof Player p && p.hasPermission("bingo.player"))
                    {
                        if (ConfigData.getConfig().teleportAfterDeath)
                        {
                            gameInstance.teleportPlayerAfterDeath(p);
                            return true;
                        }
                    }
                    break;

                case "deathmatch":
                    if (commandSender instanceof Player p && !p.hasPermission("bingo.settings"))
                    {
                        return false;
                    }

                    if (gameInstance.inProgress)
                    {
                        gameInstance.startDeathMatch(3);
                        return true;
                    }
                    else
                    {
                        if (commandSender instanceof Player p)
                            new Message("command.bingo.no_deathmatch").color(ChatColor.RED).send(p);
                        else
                            Message.log("command.bingo.no_deathmatch");
                    }
                    break;

                case "creator":
                    if (commandSender instanceof Player p && p.hasPermission("bingo.manager"))
                    {
                        CardCreatorUI creatorUI = new CardCreatorUI(null);
                        creatorUI.open(p);
                    }
                    break;

                default:
                    if (commandSender instanceof Player p)
                        new Message("command.use").color(ChatColor.RED).arg("/bingo [getcard | start | end | join | back | leave | deathmatch | creator]").send(p);
                    else
                        Message.log(ChatColor.RED + "Usage: /bingo [start | end | deathmatch]");
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
