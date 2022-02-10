package me.steven.bingoreloaded.GUIInventories.cards;

import org.bukkit.scoreboard.Team;

public class CompleteBingoCard extends BingoCard
{

    public CompleteBingoCard(CardSize size)
    {
        super(size);
    }

    @Override
    public boolean hasBingo(Team team)
    {
        return getCompleteCount(team) == size.fullCardSize;
    }
}
