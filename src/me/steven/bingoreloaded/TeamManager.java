package me.steven.bingoreloaded;

import me.steven.bingoreloaded.GUIInventories.AbstractGUIInventory;
import me.steven.bingoreloaded.GUIInventories.TeamSelectorInventory;
import me.steven.bingoreloaded.GUIInventories.cards.BingoCard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class TeamManager
{
    private final Map<Team, BingoCard> activeTeams = new HashMap<>();
    private final Scoreboard scoreboard;
    private final BingoGame game;

    public TeamManager(BingoGame game)
    {
        this.game = game;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("item_count", "bingo_item_count", "Collected Items");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
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

    public void openTeamSelector(Player player, AbstractGUIInventory parentUI)
    {
        if (game.isGameInProgress())
        {
            BingoReloaded.print(ChatColor.RED + "You cannot join or switch teams in an ongoing game, please wait until it ends", player);
            return;
        }
        TeamSelectorInventory.open(this, parentUI, player);
    }

    public Team addTeam(String teamName)
    {
        Team existingTeam = scoreboard.getTeam(teamName);

        if (existingTeam == null)
        {
            Team newTeam = scoreboard.registerNewTeam(teamName);
            BingoReloaded.broadcast("Adding Team " + newTeam.getDisplayName() + ChatColor.RESET + "!");
            activeTeams.put(newTeam, null);
            ChatColor teamColor = ChatColor.getByChar((char)newTeam.getDisplayName().getBytes()[1]);
            if (teamColor != null)
                newTeam.setColor(teamColor);
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
        if (!getParticipants().contains(player)) return;
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
        activeTeams.replaceAll((t, v) -> masterCard);
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

    public void updateTeamDisplay()
    {
        Objective objective = scoreboard.getObjective("item_count");
        if (objective == null) return;

        for (Team t : activeTeams.keySet())
        {
            Score score = objective.getScore(t.getDisplayName() + ChatColor.RESET + ":");
            score.setScore(getCardForTeam(t).getCompleteCount(t));
        }

        for (Player p : getParticipants())
        {
            p.setScoreboard(scoreboard);
        }
    }

    public void clearTeamDisplay()
    {
        for (String entry : scoreboard.getEntries())
        {
            scoreboard.resetScores(entry);
        }
    }
}
