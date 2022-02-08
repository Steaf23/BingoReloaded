package me.steven.bingoreloaded;

import me.steven.bingoreloaded.cards.BingoCard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamManager
{
    private final Map<Team, BingoCard> activeTeams = new HashMap<>();
    private final Scoreboard scoreboard;

    public TeamManager()
    {
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
    }

    public Team getPlayerTeam(Player player)
    {
        for (Team t : scoreboard.getTeams())
        {
            for (String entry : t.getEntries())
            {
                if (entry.equals(player.getName()))
                {
                    return t;
                }
            }
        }
        return null;
    }

    public void openTeamSelector(Player player)
    {
        TeamSelectorInventory teamSelect = new TeamSelectorInventory(this);
        teamSelect.showInventory(player);
    }

    public Team addTeam(String teamName)
    {
        Team existingTeam = scoreboard.getTeam(teamName);

        if (existingTeam == null)
        {
            Team newTeam = scoreboard.registerNewTeam(teamName);
            BingoReloaded.broadcast("Adding Team " + newTeam.getDisplayName() + ChatColor.RESET + "!");
            activeTeams.put(newTeam, null);
            return newTeam;
        }

        return existingTeam;
    }

    public void addPlayerToTeam(Player player, String teamName)
    {
        removePlayerFromAllTeams(player);

        Team team = scoreboard.getTeam(teamName);

        if (team == null)
        {
            team = addTeam(teamName);
        }

        team.addEntry(player.getName());
        BingoReloaded.print("You successfully joined team " + team.getDisplayName(), player);
    }

    public void removePlayerFromAllTeams(Player player)
    {
        for (Team team : scoreboard.getTeams())
        {
            team.removeEntry(player.getName());
            if (team.getEntries().size() == 0)
            {
                removeTeam(team);
            }
        }
    }

    public void removeTeam(Team team)
    {
        activeTeams.remove(team);
        team.unregister();
    }

    public BingoCard getCardForTeam(Team team)
    {
        return activeTeams.get(team);
    }

    public void initializeCards(BingoCard masterCard)
    {
        for (Team t : activeTeams.keySet())
        {
            activeTeams.put(t, masterCard);
        }
    }

    public Set<Player> getParticipants()
    {
        Set<Player> players = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers())
        {
            boolean found = false;
            for (Team team : activeTeams.keySet())
            {
                for (String entry : team.getEntries())
                {
                    if (entry.equals(p.getName()))
                    {
                        players.add(p);
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    break;
                }
            }
        }

        return players;
    }
}
