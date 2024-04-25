package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.gui.base.MenuBoard;
import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;

public class CardBuilder
{
    public static BingoCard fromMode(MenuBoard menuBoard, BingoGamemode mode, CardSize size, TeamManager teamManager) {
        return switch (mode) {
            case LOCKOUT -> new LockoutBingoCard(menuBoard, size, teamManager.getSession(), teamManager.getActiveTeams());
            case COMPLETE -> new CompleteBingoCard(menuBoard, size);
            case HOTSWAP -> new HotswapBingoCard(menuBoard, size, 10);
            default -> new BingoCard(menuBoard, size);
        };
    }
}
