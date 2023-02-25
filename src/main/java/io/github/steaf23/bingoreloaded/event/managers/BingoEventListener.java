package io.github.steaf23.bingoreloaded.event.managers;

import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.BingoGameManager;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.util.GameTimer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class BingoEventListener implements Listener
{
    private final BingoGameManager gameManager;

    public BingoEventListener(BingoGameManager gameManager)
    {
        this.gameManager = gameManager;
    }

    private BingoGame getGame(String worldName)
    {
        return gameManager.getGame(worldName);
    }

    private BingoGame getGame(World world)
    {
        return gameManager.getGame(BingoGameManager.getWorldName(world));
    }

    @EventHandler
    public void handleBingoTaskComplete(final BingoCardTaskCompleteEvent event)
    {
        BingoGame game = getGame(event.worldName);
        if (game != null)
        {
            game.handleBingoTaskComplete(event);
            game.getCardEventManager().handleTaskCompleted(event);
        }
    }

    @EventHandler
    public void handlePlayerDropItem(final PlayerDropItemEvent event)
    {
        BingoGame game = getGame(event.getPlayer().getWorld());
        if (game != null)
        {
            game.handlePlayerDropItem(event);
            game.getCardEventManager().handlePlayerDroppedItem(event);
        }
    }

    @EventHandler
    public void handlePlayerInteract(final PlayerInteractEvent event)
    {
        BingoGame game = getGame(event.getPlayer().getWorld());
        if (game != null)
        {
            game.handlePlayerInteract(event);
        }
    }

    @EventHandler
    public void handleEntityDamage(final EntityDamageEvent event)
    {
        BingoGame game = getGame(event.getEntity().getWorld());
        if (game != null)
        {
            game.handleEntityDamage(event);
        }
    }

    @EventHandler
    public void handlePlayerDeath(final PlayerDeathEvent event)
    {
        BingoGame game = getGame(event.getEntity().getWorld());
        if (game != null)
        {
            game.handlePlayerDeath(event);
        }
    }

    @EventHandler
    public void handlePlayerRespawn(final PlayerRespawnEvent event)
    {
        BingoGame game = getGame(event.getPlayer().getWorld());
        if (game != null)
        {
            game.handlePlayerRespawn(event);
        }
    }

    @EventHandler
    public void handleCountdownFinished(final CountdownTimerFinishedEvent event)
    {
        BingoGame game = getGame(event.worldName);
        if (game != null)
        {
            game.handleCountdownFinished(event);
        }
    }

    @EventHandler
    public void handlePlayerJoin(final BingoPlayerJoinEvent event)
    {
        BingoGame game = getGame(event.worldName);
        if (game != null)
        {
            game.getScoreboard().handlePlayerJoin(event);
        }
    }

    @EventHandler
    public void handlePlayerLeave(final BingoPlayerLeaveEvent event)
    {
        BingoGame game = getGame(event.worldName);
        if (game != null)
        {
            game.getScoreboard().handlePlayerLeave(event);
        }
    }

    @EventHandler
    public void handlePlayerAdvancementCompleted(final PlayerAdvancementDoneEvent event)
    {
        BingoGame game = getGame(event.getPlayer().getWorld());
        if (game != null)
        {
            game.getCardEventManager().handlePlayerAdvancementCompleted(event);
        }
    }

    @EventHandler
    public void handlePlayerPickupItem(final EntityPickupItemEvent event)
    {
        BingoGame game = getGame(event.getEntity().getWorld());
        if (game != null)
        {
            game.getCardEventManager().handlePlayerPickupItem(event);
        }
    }

    @EventHandler
    public void handleInventoryClicked(final InventoryClickEvent event)
    {
        BingoGame game = getGame(event.getWhoClicked().getWorld());
        if (game != null)
        {
            game.getCardEventManager().handleInventoryClicked(event);
        }
    }

    @EventHandler
    public void handlePlayerJoinsServer(final PlayerJoinEvent event)
    {
        BingoGame game = getGame(event.getPlayer().getWorld());
        if (game != null)
        {
            game.getTeamManager().handlePlayerJoinsServer(event);
        }
    }

    @EventHandler
    public void handlePlayerChangedWorld(final PlayerChangedWorldEvent event)
    {
        BingoGame game = getGame(event.getPlayer().getWorld());
        if (game != null)
        {
            game.getTeamManager().handlePlayerChangedWorld(event);
        }
    }
}
