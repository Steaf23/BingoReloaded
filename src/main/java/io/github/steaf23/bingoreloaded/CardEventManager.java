package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

    public void handlePlayerAdvancementCompleted(final PlayerAdvancementDoneEvent event)
    {
        BingoGame game = BingoGameManager.get().getActiveGame(worldName);
        if (game == null)
            return;

        BingoPlayer player = game.getTeamManager().getBingoPlayer(event.getPlayer());
        if (player == null || !game.isInProgress())
            return;

        BingoTeam team = player.getTeam();
        if (team == null)
            return;

        for (BingoCard card : cards)
        {
            if (team.card.equals(card))
                card.onPlayerAdvancementDone(event, player, game);
        }
    }

    public void handlePlayerDroppedItem(final PlayerDropItemEvent event)
    {
        BingoGame game = BingoGameManager.get().getActiveGame(worldName);
        if (game == null)
            return;

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

    public void handlePlayerPickupItem(final EntityPickupItemEvent event)
    {
        BingoGame game = BingoGameManager.get().getActiveGame(worldName);
        if (game == null || !(event.getEntity() instanceof Player p))
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

    public void handleInventoryClicked(final InventoryClickEvent event)
    {
        BingoGame game = BingoGameManager.get().getActiveGame(worldName);
        if (game == null || !(event.getWhoClicked() instanceof Player p))
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
}
