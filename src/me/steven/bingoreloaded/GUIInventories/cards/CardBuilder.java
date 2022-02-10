package me.steven.bingoreloaded.GUIInventories.cards;

import me.steven.bingoreloaded.BingoGameMode;

public class CardBuilder
{
    public static BingoCard fromMode(BingoGameMode mode)
    {
        return switch (mode)
                {
                    case LOCKOUT -> lockout();
                    case RUSH -> rush();
                    default -> regular();
                };
    }

    public static BingoCard regular()
    {
        return new BingoCard(CardSize.X5);
    }

    public static BingoCard lockout()
    {
        return new BingoCard(CardSize.X5);
    }

    public static BingoCard rush()
    {
        return new BingoCard(CardSize.X3);
    }
}
