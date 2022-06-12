package me.steven.bingoreloaded.gui.cards;

import me.steven.bingoreloaded.BingoReloaded;
import me.steven.bingoreloaded.item.BingoItem;
import me.steven.bingoreloaded.item.InventoryItem;
import me.steven.bingoreloaded.player.BingoTeam;
import me.steven.bingoreloaded.player.TeamManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;
    public TeamManager teamManager;
    public int currentMaxItems;

    public LockoutBingoCard(CardSize size, TeamManager manager)
    {
        super(size);
        this.teamManager = manager;
        this.currentMaxItems = size.fullCardSize;
        this.teamCount = teamManager.getActiveTeams().size();

        InventoryItem cardInfo = new InventoryItem(0, Material.PAPER, "Lockout Bingo Card", "Complete the most items to win.", "When an item has been completed", "it cannot be complete by any other team.");
        addOption(cardInfo);
    }

    @Override
    public LockoutBingoCard copy()
    {
        return this;
    }

    @Override
    public boolean hasBingo(BingoTeam team)
    {
        int completeCount = getCompleteCount(team);
        return completeCount >= Math.floor(currentMaxItems / (double) teamCount) + 1;
    }

    @Override
    public boolean completeItem(Material item, BingoTeam team, int time)
    {
        boolean result = super.completeItem(item, team, time);

        if (!result)
        {
            return false;
        }

        // get the completeCount of the team with the most items.
        BingoTeam leadingTeam = teamManager.getLeadingTeam();
        BingoTeam losingTeam = teamManager.getLosingTeam();

        int itemsLeft = size.fullCardSize - getTotalCompleteCount();

        // if amount on items cannot get up to amount of items of the team with the most items, this team cannot win anymore.
        if (itemsLeft + getCompleteCount(losingTeam) < getCompleteCount(leadingTeam))
        {
            dropTeam(losingTeam);
        }
        return true;
    }

    public void dropTeam(BingoTeam team)
    {
        BingoReloaded.broadcast("Team " + team.getColor() + team.getName() + " cannot win anymore, they are out of the game!");
        team.outOfTheGame = true;
        for (BingoItem item : items)
        {
            if (item.getWhoCompleted().equals(team))
            {
                item.voidItem();
                currentMaxItems--;
            }
        }
        for (Player p : teamManager.getPlayersOfTeam(team))
        {
            p.setGameMode(GameMode.SPECTATOR);
        }
        teamCount--;
    }

    public int getTotalCompleteCount()
    {
        int total = 0;
        for (BingoTeam t : teamManager.getActiveTeams())
        {
            total += getCompleteCount(t);
        }
        return total;
    }
}
