package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.api.CardMenu;
import io.github.steaf23.bingoreloaded.api.HotswapCardMenu;
import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.data.config.BingoOptions;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.settings.BingoSettings;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import io.github.steaf23.bingoreloaded.tasks.TaskGenerator;

import java.util.HashSet;
import java.util.Set;

public class CardFactory
{
    public static TaskCard fromGame(BingoReloadedRuntime runtime, BingoGame game, boolean texturedMenu) {
        BingoSettings settings = game.getSettings();
        CardSize size = settings.size();

        boolean allowViewingAllCards = game.getConfig().getOptionValue(BingoOptions.ALLOW_VIEWING_ALL_CARDS);
        CardMenu menu = runtime.createMenu(texturedMenu, settings.mode(), size, allowViewingAllCards);

        return switch (settings.mode()) {
            case LOCKOUT ->
                    new LockoutTaskCard(menu, size, game.getSession(), game.getTeamManager().getActiveTeams());
            case COMPLETE ->
                    new CompleteTaskCard(menu, size, game.getSettings().completeGoal());
            case HOTSWAP ->
                    new HotswapTaskCard((HotswapCardMenu) menu, size, game, game.getProgressTracker(), settings.hotswapGoal(),
                        game.getConfig().getOptionValue(BingoOptions.HOTSWAP_CONFIG));
            default -> new BingoTaskCard(menu, size);
        };
    }

    public static Set<TaskCard> generateCardsForGame(BingoGame game, boolean includeAdvancements, boolean includeStatistics, GameTask.TaskDisplayMode advancementDisplayMode, GameTask.TaskDisplayMode statisticDisplayMode) {
        BingoSettings settings = game.getSettings();
        TaskGenerator.GeneratorSettings generatorSettings = new TaskGenerator.GeneratorSettings(settings.card(), settings.seed(), includeAdvancements, includeStatistics, settings.size(), advancementDisplayMode, statisticDisplayMode);

        Set<TaskCard> uniqueCards = new HashSet<>();

        TaskCard masterCard = CardFactory.fromGame(BingoReloaded.runtime(), game, BingoReloaded.useResourcePack());
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
}
