package me.steven.bingoreloaded.GUIInventories.cards;

import me.steven.bingoreloaded.BingoGameMode;
import me.steven.bingoreloaded.util.FlexibleColor;
import org.bukkit.Material;
import org.bukkit.scoreboard.Team;

public class CardBuilder
{
    public static BingoCard fromMode(BingoGameMode mode)
    {
        return switch (mode)
                {
                    case LOCKOUT -> lockout();
                    case RUSH -> rush();
                    case COMPLETE -> complete();
                    default -> regular();
                };
    }

    private static BingoCard regular()
    {
        return new BingoCard(CardSize.X5);
    }

    private static BingoCard lockout()
    {
        return new LockoutBingoCard(CardSize.X5);
    }

    private static BingoCard rush()
    {
        return new BingoCard(CardSize.X3);
    }

    private static BingoCard complete()
    {
        return new CompleteBingoCard(CardSize.X4);
    }

    public static Material completeColor(Team team)
    {
        FlexibleColor color = FlexibleColor.fromChatColor(team.getColor());
        if (color == null) return Material.BLACK_STAINED_GLASS_PANE;
        return color.glassPane;
    }
}
