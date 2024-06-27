package io.github.steaf23.bingoreloaded.cards;

import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.player.team.BingoTeam;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.tasks.tracker.TaskProgressTracker;
import io.github.steaf23.easymenulib.inventory.MenuBoard;
import io.github.steaf23.easymenulib.util.ChatComponentUtils;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public class CompleteBingoCard extends BingoCard
{
    public CompleteBingoCard(MenuBoard menuBoard, CardSize size, TaskProgressTracker progressTracker)
    {
        super(menuBoard, size, progressTracker);
        menu.setInfo(Component.text().append(BingoTranslation.INFO_COMPLETE_NAME.asComponent()).build(),
                ChatComponentUtils.createComponentsFromString(BingoTranslation.INFO_COMPLETE_DESC.translate().split("\\n")));
    }

    @Override
    public boolean hasBingo(BingoTeam team)
    {
        return getCompleteCount(team) == size.fullCardSize;
    }

    @Override
    public CompleteBingoCard copy()
    {
        CompleteBingoCard card = new CompleteBingoCard(menu.getMenuBoard(), this.size, this.progressTracker);
        List<BingoTask> newTasks = new ArrayList<>();
        for (var t : getTasks())
        {
            newTasks.add(t.copy());
        }
        card.setTasks(newTasks);
        return card;
    }
}
