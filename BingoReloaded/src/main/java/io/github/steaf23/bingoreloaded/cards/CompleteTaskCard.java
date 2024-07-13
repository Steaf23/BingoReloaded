package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoMessage;
import io.github.steaf23.bingoreloaded.gui.inventory.card.CardMenu;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.GameTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CompleteTaskCard extends TaskCard
{
    public CompleteTaskCard(@NotNull CardMenu menu, CardSize size)
    {
        super(menu, size);
        menu.setInfo(BingoMessage.INFO_COMPLETE_NAME.asPhrase(),
                BingoMessage.INFO_COMPLETE_DESC.asMultiline());
    }

    @Override
    public boolean hasTeamWon(BingoTeam team) {
        return getCompleteCount(team) == size.fullCardSize;
    }

    @Override
    public CompleteTaskCard copy()
    {
        CompleteTaskCard card = new CompleteTaskCard(menu.copy(), this.size);
        List<GameTask> newTasks = new ArrayList<>();
        for (var t : getTasks())
        {
            newTasks.add(t.copy());
        }
        card.setTasks(newTasks);
        return card;
    }
}
