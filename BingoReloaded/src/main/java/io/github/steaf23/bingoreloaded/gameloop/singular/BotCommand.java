package io.github.steaf23.bingoreloaded.gameloop.singular;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.player.VirtualBingoPlayer;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class BotCommand implements CommandExecutor
{
    private TeamManager teamManager;

    public BotCommand(TeamManager teamManager)
    {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args)
    {
        if (!(commandSender instanceof Player p) || !p.hasPermission("bingo.admin"))
        {
            return false;
        }

        if (args.length == 0)
        {
            return false;
        }

        switch (args[0])
        {
            case "add10" ->
            {
                for (int i = 0; i < 10; i++)
                {
                    String playerName = "testPlayer_" + i;
                    String teamName = args[1];
                    teamManager.addVirtualPlayerToTeam(playerName, teamName);
                }
            }
            case "add" ->
            {
                String playerName = args[1];
                String teamName = args[2];
                teamManager.addVirtualPlayerToTeam(playerName, teamName);
            }
            case "remove" ->
            {
                String playerName = args[1];
                teamManager.removeMemberFromTeam(teamManager.getVirtualPlayerFromName(playerName));
            }
            case "complete" ->
            {
                VirtualBingoPlayer virtualPlayer = teamManager.getVirtualPlayerFromName(args[1]);
                int taskIndex = Integer.parseInt(args[2]);
                completeTaskByPlayer(virtualPlayer, taskIndex);
            }
        }
        Message.log("BEEP BOOP");
        return true;
    }

    void completeTaskByPlayer(VirtualBingoPlayer player, int taskIndex)
    {
        if (!player.getSession().isRunning())
            return;

        BingoCard card = player.getTeam().card;

        if (taskIndex >= card.tasks.size())
        {
            Message.log(ChatColor.RED + "index out of bounds for task list!");
            return;
        }

        card.tasks.get(taskIndex).complete(player, ((BingoGame)player.getSession().phase()).getGameTime());
        var slotEvent = new BingoCardTaskCompleteEvent(card.tasks.get(taskIndex), player, card.hasBingo(player.getTeam()));
        Bukkit.getPluginManager().callEvent(slotEvent);
    }
}
