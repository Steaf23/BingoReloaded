package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.cards.BingoCard;
import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.player.VirtualBingoPlayer;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BotCommand implements TabExecutor
{
    private TeamManager teamManager;

    public BotCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args) {
        if (!(commandSender instanceof Player p) || !p.hasPermission("bingo.admin")) {
            return false;
        }

        if (args.length == 0) {
            return false;
        }

        switch (args[0]) {
            case "add10" -> {
                for (int i = 0; i < 10; i++) {
                    String playerName = "testPlayer_" + i;
                    String teamName = args[1];
                    BingoParticipant virtualPlayer = getVirtualPlayerFromName(playerName);
                    if (virtualPlayer == null) {
                        virtualPlayer = new VirtualBingoPlayer(UUID.randomUUID(), playerName, teamManager.getSession());
                    }
                    teamManager.addMemberToTeam(virtualPlayer, teamName);
                }
            }
            case "add" -> {
                String playerName = args[1];
                String teamName = args[2];
                BingoParticipant virtualPlayer = getVirtualPlayerFromName(playerName);
                if (virtualPlayer == null) {
                    virtualPlayer = new VirtualBingoPlayer(UUID.randomUUID(), playerName, teamManager.getSession());
                }
                teamManager.addMemberToTeam(virtualPlayer, teamName);
            }
            case "remove" -> {
                String playerName = args[1];
                BingoParticipant player = getVirtualPlayerFromName(playerName);
                if (player != null) {
                    teamManager.removeMemberFromTeam(player);
                }
            }
            case "complete" -> {
                BingoParticipant virtualPlayer = getVirtualPlayerFromName(args[1]);
                int taskIndex = Integer.parseInt(args[2]);
                if (virtualPlayer == null) {
                    Message.error("Cannot complete task " + args[2] + " for non existing virtual player: " + args[1]);
                    break;
                }
                completeTaskByPlayer(virtualPlayer, taskIndex);
            }
        }
        return true;
    }

    void completeTaskByPlayer(BingoParticipant player, int taskIndex) {
        if (!player.getSession().isRunning())
            return;

        BingoCard card = player.getTeam().card;

        if (taskIndex >= card.getTasks().size()) {
            Message.log(ChatColor.RED + "index out of bounds for task list!");
            return;
        }

        BingoTask task = card.getTasks().get(taskIndex);
        task.complete(player, ((BingoGame) player.getSession().phase()).getGameTime());
        var slotEvent = new BingoCardTaskCompleteEvent(task, player, card.hasBingo(player.getTeam()));
        Bukkit.getPluginManager().callEvent(slotEvent);
    }

    @Nullable
    private BingoParticipant getVirtualPlayerFromName(String name) {
        for (BingoParticipant participant : teamManager.getParticipants()) {
            if (participant instanceof VirtualBingoPlayer virtualPlayer && virtualPlayer.getName().equalsIgnoreCase(name)) {
                return participant;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
