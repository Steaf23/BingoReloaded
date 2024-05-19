package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.player.team.TeamManager;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.util.timer.GameTimer;
import io.github.steaf23.easymenulib.menu.MenuBoard;

public class CardBuilder
{
    public static BingoCard fromSettings(MenuBoard menuBoard, BingoSettings settings, TeamManager teamManager, GameTimer timer) {
        return switch (settings.mode()) {
            case LOCKOUT -> new LockoutBingoCard(menuBoard, settings.size(), teamManager.getSession(), teamManager.getActiveTeams());
            case COMPLETE -> new CompleteBingoCard(menuBoard, settings.size());
            case HOTSWAP -> new HotswapBingoCard(menuBoard, settings.size(), timer, teamManager.getSession(), settings.hotswapGoal());
            default -> new BingoCard(menuBoard, settings.size());
        };
    }
}
