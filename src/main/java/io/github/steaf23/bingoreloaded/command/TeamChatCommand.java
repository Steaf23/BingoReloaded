package io.github.steaf23.bingoreloaded.command;

import io.github.steaf23.bingoreloaded.*;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TeamChatCommand implements Listener, CommandExecutor
{
    private final List<BingoPlayer> enabledPlayers;

    public TeamChatCommand()
    {
        this.enabledPlayers = new ArrayList<>();

        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        if (plugin == null) return;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerSendMessage(final AsyncPlayerChatEvent event)
    {
        BingoGame game = GameWorldManager.get().getGame(GameWorldManager.getWorldName(event.getPlayer().getWorld()));
        if (game == null)
            return;

        BingoPlayer player = game.getTeamManager().getBingoPlayer(event.getPlayer());
        if (!enabledPlayers.contains(player)) return;

        TeamManager teamManager = game.getTeamManager();
        BingoTeam team = teamManager.getTeamOfPlayer(game.getTeamManager().getBingoPlayer(event.getPlayer()));
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
            BingoGame game = GameWorldManager.get().getGame(GameWorldManager.getWorldName(p.getWorld()));
            if (game == null)
                return false;

            TeamManager teamManager = game.getTeamManager();
            BingoPlayer player = teamManager.getBingoPlayer(p);
            if (!teamManager.getParticipants().contains(player))
            {
                new Message("game.team.no_chat").color(ChatColor.RED).send(p);
                return false;
            }

            if (enabledPlayers.contains(player))
            {
                enabledPlayers.remove(player);
                new Message("game.team.chat_off").color(ChatColor.GREEN).arg("/btc").send(p);
            }
            else
            {
                enabledPlayers.add(player);
                new Message("game.team.chat_on").color(ChatColor.GREEN).arg("/btc").send(p);
            }
        }
        return false;
    }
}