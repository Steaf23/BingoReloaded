package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gui.inventory.card.CardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.GenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapGenericCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.HotswapTexturedCardMenu;
import io.github.steaf23.bingoreloaded.gui.inventory.card.TexturedCardMenu;
import io.github.steaf23.bingoreloaded.settings.BingoGamemode;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.TaskGenerator;
import io.github.steaf23.playerdisplay.PlayerDisplay;
import io.github.steaf23.playerdisplay.inventory.MenuBoard;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CardFactory
{
    public static TaskCard fromGame(MenuBoard menuBoard, BingoGame game, boolean texturedMenu) {
        BingoSettings settings = game.getSettings();
        CardSize size = settings.size();

        boolean allowViewingAllCards = game.getConfig().getOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS);
        CardMenu menu = createMenu(menuBoard, texturedMenu, settings.mode(), size, allowViewingAllCards);

        return switch (settings.mode()) {
            case LOCKOUT ->
                    new LockoutTaskCard(menu, size, game.getSession(), game.getTeamManager().getActiveTeams());
            case COMPLETE ->
                    new CompleteTaskCard(menu, size, game.getSettings().completeGoal());
            case HOTSWAP -> {
                if (!(menu instanceof HotswapCardMenu)) {
                    menu = new HotswapGenericCardMenu(menuBoard, size, allowViewingAllCards, null);
                }
                yield new HotswapTaskCard((HotswapCardMenu) menu, size, game, game.getProgressTracker(), settings.hotswapGoal(),
                        game.getConfig().getOptionValue(BingoOptions.HOTSWAP_CONFIG));
            }
            default -> new BingoTaskCard(menu, size);
        };
    }

    public static Set<TaskCard> generateCardsForGame(BingoGame game, MenuBoard menuBoard, boolean includeAdvancements, boolean includeStatistics, GameTask.TaskDisplayMode advancementDisplayMode, GameTask.TaskDisplayMode statisticDisplayMode) {
        BingoSettings settings = game.getSettings();
        TaskGenerator.GeneratorSettings generatorSettings = new TaskGenerator.GeneratorSettings(settings.card(), settings.seed(), includeAdvancements, includeStatistics, settings.size(), advancementDisplayMode, statisticDisplayMode);

        Set<TaskCard> uniqueCards = new HashSet<>();

        TaskCard masterCard = CardFactory.fromGame(menuBoard, game, PlayerDisplay.useCustomTextures());
        if (settings.differentCardPerTeam() && masterCard.canGenerateSeparateCards()) {
            // Generate a new card for each team.
            game.getTeamManager().getActiveTeams().forEach(t -> {
                t.outOfTheGame = false;
                TaskCard card = masterCard.copy(BingoMessage.SHOW_TEAM_CARD_NAME.asPhrase(t.getColoredName()));
                card.generateCard(generatorSettings);
                t.setCard(card);
                uniqueCards.add(card);
            });
        } else {
            // Otherwise generate the card only once and copy it for all teams
            masterCard.generateCard(generatorSettings);
            game.getTeamManager().getActiveTeams().forEach(t -> {
                t.outOfTheGame = false;
                TaskCard card = masterCard.copy(BingoMessage.SHOW_TEAM_CARD_NAME.asPhrase(t.getColoredName()));
                t.setCard(card);
                uniqueCards.add(card);
            });
        }
        return uniqueCards;
    }

    private static @NotNull CardMenu createMenu(MenuBoard menuBoard, boolean texturedMenu, BingoGamemode mode, CardSize size, boolean allowViewingAllCards) {
        if (texturedMenu) {
            if (mode == BingoGamemode.HOTSWAP) {
                return new HotswapTexturedCardMenu(menuBoard, size, allowViewingAllCards);
            }
            return new TexturedCardMenu(menuBoard, mode, size, allowViewingAllCards);
        }

        if (mode == BingoGamemode.HOTSWAP) {
            return new HotswapGenericCardMenu(menuBoard, size, allowViewingAllCards, null);
        }

        return new GenericCardMenu(menuBoard, mode, size, allowViewingAllCards, null);
    }
}
