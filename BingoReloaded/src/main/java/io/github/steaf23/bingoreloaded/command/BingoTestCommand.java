package io.github.steaf23.bingoreloaded.command;


import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.data.world.WorldData;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BingoTestCommand implements TabExecutor
{
    private final JavaPlugin plugin;

    public BingoTestCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args) {
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin")) {
            return false;
        }

        if (args.length < 3) {
            return false;
        }

        switch (args[0]) {
            case "world" -> {
                if (args.length < 3)
                {
                    break;
                }


                String worldName = args[1];
                boolean doesWorldExist = WorldData.doesWorldExist(plugin, worldName);
                switch (args[2])
                {
                    case "tp":
                        if (!(commandSender instanceof Player p)) {
                            return false;
                        }
                        if (!doesWorldExist)
                        {
                            Message.log("Cannot TP player to non-existing world " + worldName);
                            return false;
                        }
                        WorldData.getOrCreateWorldGroup(plugin, worldName).teleportPlayer(p);
                        break;
                    case "create":
                        if (doesWorldExist)
                        {
                            Message.log("Cannot create world " + worldName + ", because it already exists!");
                        }
                        WorldData.getOrCreateWorldGroup(plugin, worldName);
                        break;
                    case "destroy":
                        if (!doesWorldExist) {
                            Message.log("Cannot remove non-existing world " + worldName);
                            return false;
                        }
                        WorldData.destroyWorldGroup(plugin, WorldData.getOrCreateWorldGroup(plugin, worldName));
                        break;
                }
            }
            case "complete" -> {
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null)
                    return false;

                // Grossly beautiful line of code...
                BingoParticipant virtualPlayer = BingoReloaded.getInstance().getGameManager().getSession("world").teamManager.getPlayerAsParticipant(player);
                int taskIndex = Integer.parseInt(args[2]);
                if (virtualPlayer == null) {
                    Message.error("Cannot complete task " + args[2] + " for non existing player: " + args[1]);
                    break;
                }
                completeTaskByPlayer(virtualPlayer, taskIndex);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }

    void completeTaskByPlayer(BingoParticipant player, int taskIndex) {
        if (!player.getSession().isRunning())
            return;

        BingoCard card = player.getTeam().card;

        if (taskIndex >= card.tasks.size()) {
            Message.log(ChatColor.RED + "index out of bounds for task list!");
            return;
        }

        card.tasks.get(taskIndex).complete(player, ((BingoGame) player.getSession().phase()).getGameTime());
        var slotEvent = new BingoCardTaskCompleteEvent(card.tasks.get(taskIndex), player, card.hasBingo(player.getTeam()));
        Bukkit.getPluginManager().callEvent(slotEvent);
    }
}
