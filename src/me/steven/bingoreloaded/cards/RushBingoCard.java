package me.steven.bingoreloaded.cards;

public class RushBingoCard extends RegularBingoCard
{
    public RushBingoCard()
    {
        CARD_SIZE = 3;
    }

    @Override
    public int getCardInventorySlot(int itemIndex)
    {
        return getCardInventorySlot(itemIndex, 3, 3);
    }
}
