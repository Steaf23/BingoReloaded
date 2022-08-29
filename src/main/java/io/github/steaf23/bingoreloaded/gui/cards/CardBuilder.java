package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.FlexibleColor;
import org.bukkit.Material;

public class CardBuilder
{
    public static BingoCard fromMode(BingoGamemode mode, CardSize size, BingoGame game)
    {
        return switch (mode)
                {
                    case LOCKOUT -> new LockoutBingoCard(size, game, game.getTeamManager());
                    case COMPLETE -> new CompleteBingoCard(size, game);
                    default -> new BingoCard(size, game);
                };
    }

    public static Material completeColor(BingoTeam team)
    {
        FlexibleColor color = FlexibleColor.fromChatColor(team.team.getColor());
        if (color == null) return Material.BLACK_STAINED_GLASS_PANE;
        return color.glassPane;
    }
}
