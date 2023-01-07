package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoEventManager;
import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.GameWorldManager;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.BingoStatsData;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.BingoOptionsUI;
import io.github.steaf23.bingoreloaded.gui.creator.CardCreatorUI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public class BingoCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args)
    {
        if (!(commandSender instanceof Player player) || !player.hasPermission("bingo.player"))
        {
            return false;
        }

        String worldName = GameWorldManager.getWorldName(player.getWorld());
        BingoGame activeGame = GameWorldManager.get().getActiveGame(worldName);
        
        if (args.length > 0)
        {
            switch (args[0])
            {
                case "join":
                    if (GameWorldManager.get().doesGameWorldExist(worldName))
//                        activeGame.getTeamManager().openTeamSelector(player, null);
                    break;
                case "leave":
                    if (GameWorldManager.get().doesGameWorldExist(worldName))
//                        activeGame.playerQuit(player);
                    break;

                case "start":
                    if (player.hasPermission("bingo.settings"))
                    {
                        if (args.length > 1)
                        {
                            int seed = Integer.parseInt(args[1]);
                            GameWorldManager.get().getGameSettings(worldName).cardSeed = seed;
                        }

                        GameWorldManager.get().startGame(worldName);
                        return true;
                    }
                    break;

                case "end":
                    if (player.hasPermission("bingo.settings"))
                        GameWorldManager.get().endGame(worldName);
                    break;

                case "getcard":
                    if (activeGame != null)
                    {
                        activeGame.returnCardToPlayer(player);
                        return true;
                    }
                    break;

                case "back":
                    if (activeGame != null)
                    {
                        if (ConfigData.instance.teleportAfterDeath)
                        {
                            activeGame.teleportPlayerAfterDeath(player);
                            return true;
                        }
                    }
                    break;

                case "deathmatch":
                    if (!player.hasPermission("bingo.settings"))
                    {
                        return false;
                    }
                    else if (activeGame == null)
                    {
                        new Message("command.bingo.no_deathmatch").color(ChatColor.RED).send(player);
                        return false;
                    }

                    activeGame.startDeathMatch(3);
                    return true;

                case "creator":
                    if (player.hasPermission("bingo.manager"))
                    {
                        CardCreatorUI creatorUI = new CardCreatorUI(null);
                        creatorUI.open(player);
                    }
                    break;

                case "stats":
                    if (commandSender instanceof Player p && p.hasPermission("bingo.player"))
                    {
                        if (!ConfigData.instance.savePlayerStatistics)
                        {
                            TextComponent text = new TextComponent("Player statistics are not being tracked at this moment!");
                            text.setColor(ChatColor.RED);
                            Message.sendDebug(text, p);
                            return true;
                        }
                        Message msg;
                        if (args.length > 1 && p.hasPermission("bingo.admin"))
                        {
                            msg = BingoStatsData.getPlayerStatsFormatted(args[1]);
                        }
                        else
                        {
                            msg = BingoStatsData.getPlayerStatsFormatted(p.getUniqueId());
                        }
                        msg.send(p);
                        return true;
                    }
                    break;

                default:
                    if (commandSender instanceof Player p)
                        new Message("command.use").color(ChatColor.RED).arg("/bingo [getcard | stats | start | end | join | back | leave | deathmatch | creator]").send(p);
                    else
                        Message.log(ChatColor.RED + "Usage: /bingo [start | end | deathmatch]");
                    break;
            }
        }
        else
        {
            if (GameWorldManager.get().doesGameWorldExist(worldName))
            {
                BingoOptionsUI.openOptions(player);
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }
}
