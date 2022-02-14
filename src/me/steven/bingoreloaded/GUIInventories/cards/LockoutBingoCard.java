package me.steven.bingoreloaded.GUIInventories.cards;

import org.bukkit.scoreboard.Team;

public class LockoutBingoCard extends BingoCard
{
    public int teamCount;

    public LockoutBingoCard(CardSize size)
    {
        super(size);
    }

    @Override
    public LockoutBingoCard copy()
    {
        return this;
    }

    @Override
    public boolean hasBingo(Team team)
    {
        int completeCount = getCompleteCount(team);
        if (teamCount > 1)
        {
            return completeCount >= Math.floor(size.fullCardSize / (double) teamCount) + 1;
        }
        return true;
    }
}
