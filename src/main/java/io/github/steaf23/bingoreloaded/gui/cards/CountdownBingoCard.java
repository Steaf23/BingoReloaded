package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.Message;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.event.BingoGameEvent;
import io.github.steaf23.bingoreloaded.event.CountdownTimerFinishedEvent;
import io.github.steaf23.bingoreloaded.event.ReceiveBingoGameEvent;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.AbstractBingoTask;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.player.TeamManager;
import io.github.steaf23.bingoreloaded.util.CountdownTimer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountdownBingoCard extends CompleteBingoCard
{
    public CountdownBingoCard(CardSize size, BingoGame game)
    {
        super(size, game);
        InventoryItem cardInfoItem = new InventoryItem(0, Material.MAP, TranslationData.itemName("menu.card.info_countdown"), TranslationData.itemDescription("menu.card.info_countdown"));
        addOption(cardInfoItem);
    }

    @EventHandler
    public void onBingoGameEventReceived(final ReceiveBingoGameEvent event)
    {
        if (event.eventType.equals(BingoGameEvent.ENDED))
        {

        }
    }

    @Override
    public CountdownBingoCard copy()
    {
        CountdownBingoCard card = new CountdownBingoCard(this.size, game);
        List<AbstractBingoTask> newTasks = new ArrayList<>();
        for (AbstractBingoTask item : tasks)
        {
            newTasks.add(item.copy());
        }
        card.tasks = newTasks;
        return card;
    }
}
