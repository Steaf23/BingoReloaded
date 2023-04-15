package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.TranslatedMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TeamChatCommand implements Listener, CommandExecutor
{
    private final List<BingoPlayer> enabledPlayers;
    private final Function<Player, BingoSession> sessionResolver;

    public TeamChatCommand(Function<Player, BingoSession> sessionResolver)
    {
        this.enabledPlayers = new ArrayList<>();
        this.sessionResolver = sessionResolver;
    }

    private BingoSession getSession(Player player)
    {
        return sessionResolver.apply(player);
    }

    @EventHandler
    public void onPlayerSendMessage(final AsyncPlayerChatEvent event)
    {
        BingoSession session = getSession(BingoGameManager.getWorldName(event.getPlayer().getWorld()));
        if (session == null)
            return;

        TeamManager teamManager = session.teamManager;

        BingoPlayer player = teamManager.getBingoPlayer(event.getPlayer());
        if (!enabledPlayers.contains(player)) return;

        BingoTeam team = teamManager.getTeamOfPlayer(teamManager.getBingoPlayer(event.getPlayer()));
        if (team == null) return;

        String message = event.getMessage();
        sendMessage(team, event.getPlayer(), message);

        event.setCancelled(true);
    }

    public void sendMessage(BingoTeam team, Player player, String message)
    {
        for (String entry : team.team.getEntries())
        {
            Player member = Bukkit.getPlayer(entry);
            if (member == null) continue;

            if (!member.isOnline()) continue;

            member.sendMessage(ChatColor.DARK_RED + "[" + team.getColoredName().asLegacyString() + ChatColor.DARK_RED + "]" +
                    ChatColor.RESET  + "<" + player.getName() + "> " + message);
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args)
    {
        if (commandSender instanceof Player p)
        {
            BingoSession session = manager.getSession(BingoGameManager.getWorldName(p.getWorld()));
            if (session == null)
                return false;

            TeamManager teamManager = session.teamManager;
            BingoPlayer player = teamManager.getBingoPlayer(p);
            if (!teamManager.getParticipants().contains(player))
            {
                new TranslatedMessage("game.team.no_chat").color(ChatColor.RED).send(p);
                return false;
            }

            if (enabledPlayers.contains(player))
            {
                enabledPlayers.remove(player);
                new TranslatedMessage("game.team.chat_off").color(ChatColor.GREEN).arg("/btc").send(p);
            }
            else
            {
                enabledPlayers.add(player);
                new TranslatedMessage("game.team.chat_on").color(ChatColor.GREEN).arg("/btc").send(p);
            }
        }
        return false;
    }
}