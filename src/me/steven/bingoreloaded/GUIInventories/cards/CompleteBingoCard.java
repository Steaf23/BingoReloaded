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

    @Override
    public CompleteBingoCard copy()
    {
        CompleteBingoCard card = new CompleteBingoCard(this.size);
        card.items = this.items;
        return card;
    }
}
