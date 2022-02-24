package me.steven.bingoreloaded.player;

import me.steven.bingoreloaded.BingoReloaded;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TeamChat implements Listener, CommandExecutor
{
    private final List<Player> enabledPlayers;

    private final TeamManager teamManager;

    public TeamChat(TeamManager teamManager)
    {
        this.teamManager = teamManager;
        this.enabledPlayers = new ArrayList<>();

        Plugin plugin = Bukkit.getPluginManager().getPlugin(BingoReloaded.NAME);
        if (plugin == null) return;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerSendMessage(final AsyncPlayerChatEvent event)
    {
        if (!enabledPlayers.contains(event.getPlayer())) return;

        Team team = teamManager.getTeamOfPlayer(event.getPlayer());
        if (team == null) return;

        String message = event.getMessage();
        sendMessage(team, event.getPlayer(), message);

        event.setCancelled(true);

    }

    public void sendMessage(Team team, Player player, String message)
    {
        for (String entry : team.getEntries())
        {
            Player member = Bukkit.getPlayer(entry);
            if (member == null) continue;

            if (!member.isOnline()) continue;

            member.sendMessage(ChatColor.DARK_RED + "[" + team.getColor() + ChatColor.BOLD + team.getDisplayName() + ChatColor.DARK_RED + "]" +
                    ChatColor.RESET  + "<" + player.getName() + "> " + message);
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args)
    {
        if (commandSender instanceof Player p)
        {
            if (!teamManager.getParticipants().contains(p))
            {
                BingoReloaded.print(ChatColor.RED + "Can't toggle team chat since you aren't in a team!");
                return false;
            }

            if (enabledPlayers.contains(p))
            {
                enabledPlayers.remove(p);
                BingoReloaded.print(ChatColor.RED + "Disabled team chat, use /btc to enable it again.", p);
            }
            else
            {
                enabledPlayers.add(p);
                BingoReloaded.print(ChatColor.GREEN + "Enabled team chat, use /btc to disable it.", p);
            }
        }
        return false;
    }
}