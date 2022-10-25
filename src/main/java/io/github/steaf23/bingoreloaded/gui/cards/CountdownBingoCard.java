package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.event.CountdownFinishedEvent;
import org.bukkit.event.EventHandler;

public class CountdownBingoCard extends CompleteBingoCard
{
    public CountdownBingoCard(CardSize size, BingoGame game)
    {
        super(size, game);
    }

    @EventHandler
    public void onCountdownFinished(final CountdownFinishedEvent event)
    {
        //TODO: implement BingoGame.getCountdown()
//        if (game.getCountdown() != event.getOwner())
//            return;

        game.startDeathMatch(3);
    }
}
