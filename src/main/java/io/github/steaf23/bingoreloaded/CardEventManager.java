package io.github.steaf23.bingoreloaded;

import io.github.steaf23.bingoreloaded.event.BingoCardTaskCompleteEvent;
import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.gui.cards.LockoutBingoCard;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import io.github.steaf23.bingoreloaded.util.Message;
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


public class CardEventManager implements Listener
{
    private final List<BingoCard> cards;

    private String worldName;

    public CardEventManager(String worldName)
    {
        this.cards = new ArrayList<>();
        this.worldName = worldName;
        Bukkit.getPluginManager().registerEvents(this, BingoReloaded.get());
    }

    public void setCards(List<BingoCard> newCards)
    {
        this.cards.clear();
        this.cards.addAll(newCards);
    }

    @EventHandler
    public void onPlayerAdvancementDone(final PlayerAdvancementDoneEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(GameWorldManager.getWorldName(event.getPlayer().getWorld()));
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

    @EventHandler
    public void onPlayerDroppedItem(final PlayerDropItemEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(GameWorldManager.getWorldName(event.getPlayer().getWorld()));
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

    @EventHandler
    public void onPlayerCollectItem(final EntityPickupItemEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(GameWorldManager.getWorldName(event.getEntity().getWorld()));
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

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        BingoGame game = GameWorldManager.get().getActiveGame(GameWorldManager.getWorldName(event.getWhoClicked().getWorld()));
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

    @EventHandler
    public void onTaskCompleted(final BingoCardTaskCompleteEvent event)
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
