package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gui.inventory.CardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.GenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.SmallTexturedCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.TexturedCardMenu;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import org.jetbrains.annotations.NotNull;

public class CardBuilder
{
    public static TaskCard fromGame(MenuBoard menuBoard, BingoGame game, boolean texturedMenu) {
        BingoSettings settings = game.getSettings();

        CardMenu menu = createMenu(menuBoard, texturedMenu, settings);

        return switch (settings.mode()) {
            case LOCKOUT -> {
                yield new LockoutTaskCard(menu, settings.size(), game.getSession(), game.getTeamManager().getActiveTeams());
            }
            case COMPLETE -> {
                yield new CompleteTaskCard(menu, settings.size());
            }
            case HOTSWAP -> {
                //FIXME: return textured hot swap menu
                yield new HotswapTaskCard(new HotswapCardMenu(menuBoard, settings.size()), settings.size(), game, game.getProgressTracker(), settings.hotswapGoal(), game.getConfig().hotswapMode);
            }
            default -> {
                yield new BingoTaskCard(menu, settings.size());
            }
        };
    }

    private static @NotNull CardMenu createMenu(MenuBoard menuBoard, boolean texturedMenu, BingoSettings settings) {
        CardMenu menu = null;
        if (texturedMenu) {
            if (settings.size() == CardSize.X3) {
                menu = new SmallTexturedCardMenu(menuBoard, settings.mode());
            } else if (settings.size() == CardSize.X5) {
                menu = new TexturedCardMenu(menuBoard, settings.mode(), settings.size());
            } else {
                menu = new GenericCardMenu(menuBoard, settings.size());
            }
        } else {
            menu = new GenericCardMenu(menuBoard, settings.size());
        }
        return menu;
    }
}
