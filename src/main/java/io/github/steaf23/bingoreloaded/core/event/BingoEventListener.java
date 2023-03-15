package io.github.steaf23.bingoreloaded.core.event;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.core.BingoGame;
import io.github.steaf23.bingoreloaded.core.BingoGameManager;
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

    private BingoGame getGame(World world)
    {
        return gameManager.getGame(BingoGameManager.getWorldName(world));
    }

    @EventHandler
    public void handleBingoTaskComplete(final BingoCardTaskCompleteEvent event)
    {
        BingoGame game = event.game;
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
            game.getCardEventManager().handlePlayerDroppedItem(event, game);
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
            game.handlePlayerRespawn(event, gameManager);
        }
    }

    @EventHandler
    public void handleCountdownFinished(final CountdownTimerFinishedEvent event)
    {
        BingoGame game = event.game;
        if (game != null)
        {
            game.handleCountdownFinished(event);
        }
    }

    @EventHandler
    public void handlePlayerJoin(final BingoPlayerJoinEvent event)
    {
        BingoGame game = event.game;
        if (game != null)
        {
//            game.getScoreboard().handlePlayerJoin(event);
        }
    }

    @EventHandler
    public void handlePlayerLeave(final BingoPlayerLeaveEvent event)
    {
        BingoGame game = event.game;
        if (game != null)
        {
//            game.getScoreboard().handlePlayerLeave(event);
        }
    }

    @EventHandler
    public void handlePlayerAdvancementCompleted(final PlayerAdvancementDoneEvent event)
    {
        if (!BingoReloaded.config().useAdvancements)
            return;

        BingoGame game = getGame(event.getPlayer().getWorld());
        if (game != null)
        {
            game.getCardEventManager().handlePlayerAdvancementCompleted(event, game);
        }
    }

    @EventHandler
    public void handlePlayerPickupItem(final EntityPickupItemEvent event)
    {
        BingoGame game = getGame(event.getEntity().getWorld());
        if (game != null)
        {
            game.getCardEventManager().handlePlayerPickupItem(event, game);
        }
    }

    @EventHandler
    public void handleInventoryClicked(final InventoryClickEvent event)
    {
        BingoGame game = getGame(event.getWhoClicked().getWorld());
        if (game != null)
        {
            game.getCardEventManager().handleInventoryClicked(event, game);
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
        if (game == null)
        {
            game = getGame(event.getFrom());
            if (game == null)
                return;
        }
        game.getTeamManager().handlePlayerChangedWorld(event, gameManager);
    }

    @EventHandler
    public void handleStatisticIncrement(final PlayerStatisticIncrementEvent event)
    {
        if (!BingoReloaded.config().useStatistics)
            return;

        BingoGame game = getGame(event.getPlayer().getWorld());
        if (game != null)
        {
            game.getStatisticTracker().handleStatisticIncrement(event, game);
        }
    }

    @EventHandler
    public void handleStatisticCompleted(final BingoStatisticCompletedEvent event)
    {
        if (!BingoReloaded.config().useStatistics)
            return;

        BingoGame game = event.game;
        if (game != null)
        {
            game.getCardEventManager().handleStatisticCompleted(event, game);
        }
    }
}
