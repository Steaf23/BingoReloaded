package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import org.bukkit.Material;

public class CardBuilder
{
    public static BingoCard fromMode(BingoGamemode mode, CardSize size, int teamCount)
    {
        return switch (mode)
                {
                    case LOCKOUT -> new LockoutBingoCard(size, teamCount);
                    case COMPLETE -> new CompleteBingoCard(size);
                    case COUNTDOWN -> new CountdownBingoCard(size);
                    default -> new BingoCard(size);
                };
    }

    public static Material completeColor(BingoTeam team)
    {
        FlexibleColor color = FlexibleColor.fromChatColor(team.getColor());
        if (color == null) return Material.BLACK_STAINED_GLASS_PANE;
        return color.glassPane;
    }
}
