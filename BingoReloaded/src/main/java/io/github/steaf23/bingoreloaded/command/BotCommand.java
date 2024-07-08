package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.cards.TaskCard;
import io.github.steaf23.bingoreloaded.event.BingoTaskProgressCompletedEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.player.VirtualBingoPlayer;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.playerdisplay.util.ConsoleMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    private BingoSession session;
    private TeamManager teamManager;

    public BotCommand(BingoSession session) {
        this.teamManager = session.teamManager;
        BingoReloaded.getInstance().registerCommand("bingobot", this);
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
                        virtualPlayer = new VirtualBingoPlayer(UUID.randomUUID(), playerName, session);
                    }
                    teamManager.addMemberToTeam(virtualPlayer, teamName);
                }
            }
            case "add" -> {
                String playerName = args[1];
                String teamName = args[2];
                BingoParticipant virtualPlayer = getVirtualPlayerFromName(playerName);
                if (virtualPlayer == null) {
                    virtualPlayer = new VirtualBingoPlayer(UUID.randomUUID(), playerName, session);
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
                    ConsoleMessenger.error("Cannot complete task " + args[2] + " for non existing virtual player: " + args[1]);
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

        TaskCard card = player.getTeam().getCard();

        if (card == null || taskIndex >= card.getTasks().size()) {
            ConsoleMessenger.log(Component.text("index out of bounds for task list!").color(NamedTextColor.RED));
            return;
        }

        GameTask task = card.getTasks().get(taskIndex);
        task.complete(player, ((BingoGame) player.getSession().phase()).getGameTime());
        var slotEvent = new BingoTaskProgressCompletedEvent(player.getSession(), task);
        Bukkit.getPluginManager().callEvent(slotEvent);
    }

    @Nullable
    private BingoParticipant getVirtualPlayerFromName(String name) {
        return teamManager.getParticipants().stream()
                .filter(p -> p.getName().equals(name))
                .findAny().orElse(null);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
