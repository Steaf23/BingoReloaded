package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;

public class CardBuilder
{
    public static BingoCard fromGame(MenuBoard menuBoard, BingoGame game) {
        BingoSettings settings = game.getSettings();
        return switch (settings.mode()) {
            case LOCKOUT -> new LockoutBingoCard(menuBoard, settings.size(), game.getSession(), game.getTeamManager().getActiveTeams(), game.getProgressTracker());
            case COMPLETE -> new CompleteBingoCard(menuBoard, settings.size(), game.getProgressTracker());
            case HOTSWAP -> new HotswapBingoCard(menuBoard, settings.size(), game, game.getProgressTracker(), settings.hotswapGoal(), game.getConfig().hotswapMode);
            default -> new BingoCard(menuBoard, settings.size(), game.getProgressTracker());
        };
    }
}
