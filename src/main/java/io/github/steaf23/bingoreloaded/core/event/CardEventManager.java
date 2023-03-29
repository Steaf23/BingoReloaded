package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.core.BingoGame;
import io.github.steaf23.bingoreloaded.core.BingoSession;
import io.github.steaf23.bingoreloaded.core.cards.BingoCard;
import io.github.steaf23.bingoreloaded.core.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.core.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.core.player.BingoTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.List;


public class CardEventManager
{
    private final List<BingoCard> cards;
    private String worldName;

    public CardEventManager(String worldName)
    {
        this.cards = new ArrayList<>();
        this.worldName = worldName;
    }

    public void setCards(List<BingoCard> newCards)
    {
        this.cards.clear();
        this.cards.addAll(newCards);
    }

    public void handlePlayerAdvancementCompleted(final PlayerAdvancementDoneEvent event, final BingoSession session)
    {
        BingoPlayer player = session.teamManager.getBingoPlayer(event.getPlayer());
        if (player == null || !session.isRunning())
            return;

        BingoTeam team = player.getTeam();
        if (team == null)
            return;

        for (BingoCard card : cards)
        {
            if (team.card.equals(card))
                card.onPlayerAdvancementDone(event, player, session.game());
        }
    }

    public void handlePlayerDroppedItem(final PlayerDropItemEvent event, final BingoGame game)
    {
        BingoPlayer player = game.getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || !player.gamePlayer().isPresent())
            return;

        BingoTeam team = player.getTeam();
        if (team == null)
            return;

        for (BingoCard card : cards)
        {
            if (team.card.equals(card))
                card.onPlayerDroppedItem(event, player, game);
        }
    }

    public void handlePlayerPickupItem(final EntityPickupItemEvent event, final BingoGame game)
    {
        if (!(event.getEntity() instanceof Player p))
            return;

        BingoPlayer player = game.getTeamManager().getBingoPlayer(p);
        if (player == null || !player.gamePlayer().isPresent())
            return;

        BingoTeam team = player.getTeam();
        if (team == null)
            return;

        for (BingoCard card : cards)
        {
            if (team.card.equals(card))
                card.onPlayerCollectItem(event, player, game);
        }
    }

    public void handleInventoryClicked(final InventoryClickEvent event, final BingoGame game)
    {
        if (!(event.getWhoClicked() instanceof Player p))
            return;

        BingoPlayer player = game.getTeamManager().getBingoPlayer(p);
        if (player == null || !player.gamePlayer().isPresent())
            return;

        BingoTeam team = player.getTeam();
        if (team == null)
            return;

        for (BingoCard card : cards)
        {
            if (team.card.equals(card))
                card.onInventoryClick(event, player, game);
        }
    }

    public void handleTaskCompleted(final BingoCardTaskCompleteEvent event)
    {
        for (BingoCard card : cards)
        {
            if (card instanceof LockoutBingoCard lockoutCard)
            {
                lockoutCard.onCardSlotCompleteEvent(event);
            }
        }
    }

    public void handleStatisticCompleted(final BingoStatisticCompletedEvent event, final BingoGame game)
    {
        if (!event.player.gamePlayer().isPresent())
            return;

        BingoTeam team = event.player.getTeam();
        if (team == null)
            return;

        for (BingoCard card : cards)
        {
            if (team.card.equals(card))
                card.onPlayerStatisticCompleted(event, event.player, game);
        }
    }
}
