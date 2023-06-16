package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;

import java.util.ArrayList;
import java.util.List;

public class CompleteBingoCard extends BingoCard
{
    public CompleteBingoCard(CardSize size)
    {
        super(size);
        menu.setInfo(BingoTranslation.INFO_COMPLETE_NAME.translate(),
                BingoTranslation.INFO_COMPLETE_DESC.translate().split("\\n"));
    }

    public CompleteBingoCard(CardSize size, List<BingoTask> tasks) {
        super(size, tasks);
        menu.setInfo(BingoTranslation.INFO_COMPLETE_NAME.translate(),
                BingoTranslation.INFO_COMPLETE_DESC.translate().split("\\n"));
    }

    @Override
    public boolean hasBingo(BingoTeam team)
    {
        return getCompleteCount(team) == size.fullCardSize;
    }

    @Override
    public CompleteBingoCard copy()
    {
        CompleteBingoCard card = new CompleteBingoCard(this.size);
        List<BingoTask> newTasks = new ArrayList<>();
        for (var t : tasks)
        {
            newTasks.add(t.copy());
        }
        card.tasks = newTasks;
        return card;
    }
}
