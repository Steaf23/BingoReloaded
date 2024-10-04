package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gui.inventory.card.CardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.GenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapGenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapTexturedCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.TexturedCardMenu;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import org.jetbrains.annotations.NotNull;

public class CardFactory
{
    public static TaskCard fromGame(MenuBoard menuBoard, BingoGame game, boolean texturedMenu) {
        BingoSettings settings = game.getSettings();

        CardMenu menu = createMenu(menuBoard, texturedMenu, settings);

        return switch (settings.mode()) {
            case LOCKOUT ->
                    new LockoutTaskCard(menu, settings.size(), game.getSession(), game.getTeamManager().getActiveTeams());
            case COMPLETE ->
                    new CompleteTaskCard(menu, settings.size(), game.getSettings().completeGoal());
            case HOTSWAP -> {
                if (!(menu instanceof HotswapCardMenu)) {
                    menu = new HotswapGenericCardMenu(menuBoard, settings.size());
                }
                yield new HotswapTaskCard((HotswapCardMenu) menu, settings.size(), game, game.getProgressTracker(), settings.hotswapGoal(), game.getConfig().hotswapMode);
            }
            default -> new BingoTaskCard(menu, settings.size());
        };
    }

    private static @NotNull CardMenu createMenu(MenuBoard menuBoard, boolean texturedMenu, BingoSettings settings) {
        if (texturedMenu) {
            if (settings.mode() == BingoGamemode.HOTSWAP) {
                return new HotswapTexturedCardMenu(menuBoard, settings.size());
            }
            return new TexturedCardMenu(menuBoard, settings.mode(), settings.size());
        }

        if (settings.mode() == BingoGamemode.HOTSWAP) {
            return new HotswapGenericCardMenu(menuBoard, settings.size());
        }

        return new GenericCardMenu(menuBoard, settings.mode(), settings.size());
    }
}
