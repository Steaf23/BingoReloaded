package io.github.steaf23.bingoreloaded.event.core;

import io.github.steaf23.bingoreloaded.event.BingoDeathmatchTaskCompletedEvent;
import io.github.steaf23.bingoreloaded.event.BingoEndedEvent;
import io.github.steaf23.bingoreloaded.event.BingoPlaySoundEvent;
import io.github.steaf23.bingoreloaded.event.BingoSettingsUpdatedEvent;
import io.github.steaf23.bingoreloaded.event.BingoStatisticCompletedEvent;
import io.github.steaf23.bingoreloaded.event.BingoTaskProgressCompletedEvent;
import io.github.steaf23.bingoreloaded.event.CountdownTimerFinishedEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantJoinedTeamEvent;
import io.github.steaf23.bingoreloaded.event.ParticipantLeftTeamEvent;
import io.github.steaf23.bingoreloaded.event.PlayerJoinedSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PlayerLeftSessionWorldEvent;
import io.github.steaf23.bingoreloaded.event.PrepareNextBingoGameEvent;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BingoEventListener implements Listener
{
    private final boolean disableAdvancements;
    private final boolean disableStatistics;
    private final GameManager gameManager;

    public BingoEventListener(GameManager gameManager, boolean disableAdvancements, boolean disableStatistics)
    {
        this.gameManager = gameManager;
        this.disableAdvancements = disableAdvancements;
        this.disableStatistics = disableStatistics;
    }

    @Nullable
    private BingoSession getSession(@NotNull World world)
    {
        return gameManager.getSessionFromWorld(world);
    }

    @EventHandler
    public void handleBingoGameEnded(final BingoEndedEvent event)
    {
        if (event.getSession() != null)
        {
            event.getSession().handleGameEnded(event);
        }
    }

    @EventHandler
    public void handleTaskProgressCompleted(final BingoTaskProgressCompletedEvent event) {
        BingoSession session = event.getSession();
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.handleBingoTaskComplete(event);
        }
    }

    @EventHandler
    public void handleDeathmatchTaskCompleted(final BingoDeathmatchTaskCompletedEvent event) {
        BingoSession session = event.getSession();
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.handleDeathmatchTaskComplete(event);
        }
    }

    @EventHandler
    public void handlePlayerDropItem(final PlayerDropItemEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
            return;

        session.handlePlayerDropItem(event);

        BingoGame game = session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null && game.hasStarted())
        {
            game.getProgressTracker().handlePlayerDroppedItem(event);
        }
    }

    @EventHandler
    public void handlePlayerInteract(final PlayerInteractEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
        {
            return;
        }

        // Determine if the event is fired for the correct hand, to avoid duplicate events
        if (!(event.getHand() == EquipmentSlot.HAND && event.getPlayer().getInventory().getItemInMainHand().equals(event.getItem()) ||
                event.getHand() == EquipmentSlot.OFF_HAND && event.getPlayer().getInventory().getItemInOffHand().equals(event.getItem())))
        {
            return;
        }

        session.phase().handlePlayerInteract(event);
    }

    @EventHandler
    public void handleEntityDamage(final EntityDamageEvent event)
    {
        BingoSession session = getSession(event.getEntity().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.handleEntityDamage(event);
        }
    }

    @EventHandler
    public void handlePlayerDeath(final PlayerDeathEvent event)
    {
        BingoSession session = getSession(event.getEntity().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.handlePlayerDeath(event);
        }
    }

    @EventHandler
    public void handlePlayerRespawn(final PlayerRespawnEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session != null && session.getPhase() instanceof PregameLobby lobby) {
            lobby.handlePlayerRespawn(event);
            return;
        }


        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.handlePlayerRespawn(event);
        }
    }

    @EventHandler
    public void handleCountdownFinished(final CountdownTimerFinishedEvent event)
    {
        BingoGame game = event.getSession() != null && event.getSession().isRunning() ? (BingoGame)event.getSession().phase() : null;
        if (game != null)
        {
            game.handleCountdownFinished(event);
        }
    }

    @EventHandler
    public void handlePlayerAdvancementCompleted(final PlayerAdvancementDoneEvent event)
    {
        if (disableAdvancements)
            return;

        BingoSession session = getSession(event.getPlayer().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.getProgressTracker().handlePlayerAdvancementDone(event);
        }
    }

    @EventHandler
    public void handlePlayerPickupItem(final EntityPickupItemEvent event)
    {
        BingoSession session = getSession(event.getEntity().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null && game.hasStarted())
        {
            game.getProgressTracker().handlePlayerPickupItem(event);
        }
    }

    @EventHandler
    public void handleInventoryClicked(final InventoryClickEvent event)
    {
        BingoSession session = getSession(event.getWhoClicked().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null && game.hasStarted())
        {
            game.getProgressTracker().handleInventoryClicked(event);
        }
    }

    @EventHandler
    public void handlePlayerJoinsServer(final PlayerJoinEvent event)
    {
        gameManager.handlePlayerJoinsServer(event);
    }

    @EventHandler
    public void handlePlayerQuitsServer(final PlayerQuitEvent event)
    {
        gameManager.handlePlayerQuitsServer(event);
    }

    // We need the game manager to handle teleports first to make sure no player information gets lost by accident.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerTeleport(final PlayerTeleportEvent event)
    {
        gameManager.handlePlayerTeleport(event);
    }

    @EventHandler
    public void onPlayerItemDamaged(PlayerItemDamageEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session != null && session.isRunning())
        {
            ((BingoGame)session.phase()).handlePlayerItemDamaged(event);
        }
    }

    @EventHandler
    public void handleStatisticIncrement(final PlayerStatisticIncrementEvent event)
    {
        if (disableStatistics)
            return;

        BingoSession session = getSession(event.getPlayer().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.getProgressTracker().handlePlayerStatIncrement(event);
        }
    }

    @EventHandler
    public void handleBingoStatisticCompleted(final BingoStatisticCompletedEvent event)
    {
        if (disableStatistics)
            return;

        BingoSession session = event.getSession();
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.getProgressTracker().handleBingoStatisticCompleted(event);
        }
    }

    @EventHandler
    public void handlePlayerMove(final PlayerMoveEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
            return;

        if (session.isRunning())
        {
            ((BingoGame)session.phase()).handlePlayerMove(event);
        }
    }

    @EventHandler
    public void handlePlayerPortal(final PlayerPortalEvent event) {
        BingoSession session = getSession(event.getFrom().getWorld());
        if (session == null)
            return;

        session.handlePlayerPortalEvent(event);
    }

    @EventHandler
    public void handlePlayerBlockBreak(final BlockBreakEvent event) {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
            return;

        session.handlePlayerBlockBreak(event);
    }

    @EventHandler
    public void handlePlayerBlockPlace(final BlockPlaceEvent event) {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
            return;

        session.handlePlayerBlockPlace(event);
    }

    @EventHandler
    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
        event.getSession().handleSettingsUpdated(event);
    }

    @EventHandler
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event)
    {
        event.getSession().handlePlayerJoinedSessionWorld(event);
    }

    @EventHandler
    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event)
    {
        event.getSession().handlePlayerLeftSessionWorld(event);
    }

    @EventHandler
    public void handleParticipantJoinedTeam(final ParticipantJoinedTeamEvent event) {
        event.getSession().handleParticipantJoinedTeam(event);
    }

    @EventHandler
    public void handleParticipantLeftTeam(final ParticipantLeftTeamEvent event) {
        event.getSession().handleParticipantLeftTeam(event);
    }

    @EventHandler
    public void handleBingoPlaySoundEvent(final BingoPlaySoundEvent event) {
        event.getSession().handlePlaySoundEvent(event);
    }

    @EventHandler
    public void handlePrepareNextBingoGameEvent(final PrepareNextBingoGameEvent event) {
        gameManager.handlePrepareNextBingoGame(event);
    }
}
