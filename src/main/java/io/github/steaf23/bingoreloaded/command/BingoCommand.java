package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.GameWorldManager;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.BingoStatsData;
import io.github.steaf23.bingoreloaded.data.TaskListsData;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.gui.BingoOptionsUI;
import io.github.steaf23.bingoreloaded.gui.creator.BingoCreatorUI;
import io.github.steaf23.bingoreloaded.item.tasks.*;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

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
        
        if (args.length == 0)
        {
            if (!GameWorldManager.get().doesGameWorldExist(worldName))
                return false;

            BingoOptionsUI.openOptions(player);
            return true;
        }

        switch (args[0])
        {
            case "join":
                if (activeGame != null)
                    activeGame.getTeamManager().openTeamSelector(player, null);
                break;
            case "leave":
                if (activeGame != null)
                {
                    BingoPlayer participant = activeGame.getTeamManager().getBingoPlayer(player);
                    activeGame.playerQuit(participant);
                }
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
                    BingoPlayer participant = activeGame.getTeamManager().getBingoPlayer(player);
                    if (participant != null);
                        activeGame.returnCardToPlayer(participant);
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
                if (GameWorldManager.get().doesGameWorldExist(worldName) && player.hasPermission("bingo.manager"))
                {
                    BingoCreatorUI creatorUI = new BingoCreatorUI(null);
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

            case "task":
                if (commandSender instanceof Player p)
                {
                    TaskListsData.saveTasks("CHEESE",
                            new BingoTask(new ItemTask(Material.BEACON)),
                            new BingoTask(new AdvancementTask(Bukkit.getAdvancement(NamespacedKey.fromString("minecraft:nether/obtain_crying_obsidian")))),
                            new BingoTask(new StatisticTask(new BingoStatistic(Statistic.BELL_RING))),
                            new BingoTask(new StatisticTask(new BingoStatistic(Statistic.KILL_ENTITY, EntityType.MUSHROOM_COW), 3))
                    );

                    BingoPlayer dummy = new BingoPlayer(p.getUniqueId(),
                            new BingoTeam(
                                    Bukkit.getScoreboardManager().getNewScoreboard().registerNewTeam("orange"),
                                    null,
                                    FlexColor.ORANGE)
                            , "world");

                    var tasks = TaskListsData.getTasks("CHEESE");
                    p.getInventory().addItem(tasks.get(0).asStack());
                    p.getInventory().addItem(tasks.get(1).asStack());
                    p.getInventory().addItem(tasks.get(2).asStack());
                    p.getInventory().addItem(tasks.get(3).asStack());
                    tasks.forEach(t -> t.completedBy = Optional.ofNullable(dummy));
                    p.getInventory().addItem(tasks.get(0).asStack());
                    p.getInventory().addItem(tasks.get(1).asStack());
                    p.getInventory().addItem(tasks.get(2).asStack());
                    p.getInventory().addItem(tasks.get(3).asStack());
                    tasks.forEach(t -> t.setVoided(true));
                    p.getInventory().addItem(tasks.get(0).asStack());
                    p.getInventory().addItem(tasks.get(1).asStack());
                    p.getInventory().addItem(tasks.get(2).asStack());
                    p.getInventory().addItem(tasks.get(3).asStack());
                }
                break;

            default:
                if (commandSender instanceof Player p)
                    new Message("command.use").color(ChatColor.RED).arg("/bingo [getcard | stats | start | end | join | back | leave | deathmatch | creator]").send(p);
                else
                    Message.log(ChatColor.RED + "Usage: /bingo [start | end | deathmatch]");
                break;
        }

        return false;
    }
}
