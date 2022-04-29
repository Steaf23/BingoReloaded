package me.steven.bingoreloaded.gui.cards;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.item.BingoItem;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.player.TeamManager;
import org.bukkit.Material;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;
    public final Map<Team, Boolean> teams;
    public int currentMaxItems;

    public LockoutBingoCard(CardSize size)
    {
        super(size);
        this.teams = new HashMap<>();

        InventoryItem cardInfo = new InventoryItem(0, Material.PAPER, "Lockout Bingo Card", "Complete the most items to win.", "When an item has been completed", "it cannot be complete by any other team.");
        addOption(cardInfo);
    }

    @Override
    public LockoutBingoCard copy()
    {
        return this;
    }

    @Override
    public boolean hasBingo(Team team)
    {
        if (teamCount == 2)
        {
            int completeCount = getCompleteCount(team);
            return completeCount >= Math.floor(size.fullCardSize / (double) teamCount) + 1;
        }

        if (teamCount < 2)
        {
            return true;
        }

        // get the completeCount of the team with the most items.
        int leadingTeamCompleteCount = 0;
        Team leadingTeam = team;
        for (Team otherTeam : teams.keySet())
        {
            int teamCC = getCompleteCount(otherTeam);
            if (teamCC > leadingTeamCompleteCount)
            {
                leadingTeamCompleteCount = teamCC;
                leadingTeam = otherTeam;
            }
        }

        if (team.equals(leadingTeam) && (getCompleteCount(team) * 2 + 1) >= getRemainingItemCount())

        for (Team t : teams.keySet())
        {
            if (!t.equals(leadingTeam))
            {
                int completeCount = getCompleteCount(t);
                int otherTeamsTotal = getTotalCompleteCount() - completeCount;
                BingoReloaded.print("other teams got " + otherTeamsTotal + " items");

                // if amount on items cannot get up to amount of items of the team with the most items, this team cannot win anymore.
                int itemsLeft = size.fullCardSize - getTotalCompleteCount();
                if (itemsLeft + completeCount < leadingTeamCompleteCount)
                {
                    dropTeam(t);
                    return false;
                }
            }
        }
        return true;
    }

    public int getRemainingItemCount()
    {
        int totalVoidSpaces = 0;
        for (Team team : teams.keySet())
        {
            if (!teams.get(team))
            {
                totalVoidSpaces += getCompleteCount(team);
            }
        }
        return size.fullCardSize - totalVoidSpaces;
    }

    public void dropTeam(Team team)
    {
        BingoReloaded.broadcast("Team " + team.getColor() + team.getDisplayName() + " cannot win anymore, they are out of the game!");
        teams.put(team, false);
        for (BingoItem item : items)
        {
            if (item.getWhoCompleted().equals(team))
            {
                item.voidItem();
            }
        }
        teamCount -= 1;
    }

    public int getTotalCompleteCount()
    {
        int total = 0;
        for (Team t : teams.keySet())
        {
            total += getCompleteCount(t);
        }
        return total;
    }
}
