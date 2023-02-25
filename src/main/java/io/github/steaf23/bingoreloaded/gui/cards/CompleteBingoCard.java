package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.item.tasks.BingoTask;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CompleteBingoCard extends BingoCard
{
    public CompleteBingoCard(CardSize size)
    {
        super(size);
        menu.setInfo(TranslationData.itemName("menu.card.info_complete"),
                TranslationData.itemDescription("menu.card.info_complete"));
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
