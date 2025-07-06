package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BingoEventListener {
	private final boolean disableAdvancements;
	private final boolean disableStatistics;
	private final GameManager gameManager;

	public BingoEventListener(GameManager gameManager, boolean disableAdvancements, boolean disableStatistics)
	{
		this.gameManager = gameManager;
		this.disableAdvancements = disableAdvancements;
		this.disableStatistics = disableStatistics;

		BingoReloaded.eventBus()
				.registerMethod(BingoEvents.GameEnded.class, this::handleBingoGameEnded)
				.registerMethod(BingoEvents.GameEnded.class, this::handleBingoGameEnded)
				.registerMethod(BingoEvents.GameEnded.class, this::handleBingoGameEnded)
				.registerMethod(BingoEvents.GameEnded.class, this::handleBingoGameEnded);
	}

	@Nullable
	private BingoSession getSession(@NotNull WorldHandle world)
	{
		return gameManager.getSessionFromWorld(world);
	}

	public void handleBingoGameEnded(final BingoEvents.GameEnded event)
	{
		if (event.session() != null)
		{
			event.session().handleGameEnded(event);
		}
	}

	public void handleTaskProgressCompleted(final BingoEvents.TaskProgressCompletedEvent event) {
		BingoSession session = event.session();
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null)
		{
			game.handleBingoTaskComplete(event);
		}
	}

	public void handleDeathmatchTaskCompleted(final BingoEvents.TaskProgressCompletedEvent event) {
		BingoSession session = event.session();
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null)
		{
			game.handleDeathmatchTaskComplete(event);
		}
	}

	public void handlePlayerDropItem(final PlayerDropItemEvent event)
	{
		BingoSession session = getSession(event.player().world());
		if (session == null)
			return;

		session.handlePlayerDropItem(event);

		BingoGame game = session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null && game.hasStarted())
		{
			game.getProgressTracker().handlePlayerDroppedItem(event);
		}
	}

	public void handlePlayerInteract(final PlayerInteractEvent event)
	{
		BingoSession session = getSession(event.player().world());
		if (session == null)
		{
			return;
		}

		// FIXME: REFACTOR determine if this is needed
//        // Determine if the event is fired for the correct hand, to avoid duplicate events
//        if (!(event.getHand() == EquipmentSlot.HAND && event.getPlayer().getInventory().getItemInMainHand().equals(event.getItem()) ||
//                event.getHand() == EquipmentSlot.OFF_HAND && event.getPlayer().getInventory().getItemInOffHand().equals(event.getItem())))
//        {
//            return;
//        }

		session.phase().handlePlayerInteract(event);
	}

	public void handleEntityDamage(final EntityDamageEvent event)
	{
		BingoSession session = getSession(event.getEntity().getWorld());
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null)
		{
			game.handleEntityDamage(event);
		}
	}

	public void handlePlayerDeath(final PlayerDeathEvent event)
	{
		BingoSession session = getSession(event.getEntity().getWorld());
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null)
		{
			game.handlePlayerDeath(event);
		}
	}

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

	public void handleCountdownFinished(final BingoEvents.CountdownTimerFinished event)
	{
		BingoGame game = event.session() != null && event.session().isRunning() ? (BingoGame)event.session().phase() : null;
		if (game != null)
		{
			game.handleCountdownFinished(event);
		}
	}

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

	public void handlePlayerPickupItem(final EntityPickupItemEvent event)
	{
		BingoSession session = getSession(event.getEntity().getWorld());
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null && game.hasStarted())
		{
			game.getProgressTracker().handlePlayerPickupItem(event);
		}
	}

	public void handleInventoryClicked(final InventoryClickEvent event)
	{
		BingoSession session = getSession(event.getWhoClicked().getWorld());
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null && game.hasStarted())
		{
			game.getProgressTracker().handleInventoryClicked(event);
		}
	}

	public void handlePlayerJoinsServer(final PlayerJoinEvent event)
	{
		gameManager.handlePlayerJoinsServer(event);
	}

	public void handlePlayerQuitsServer(final PlayerQuitEvent event)
	{
		gameManager.handlePlayerQuitsServer(event);
	}

	// FIXME: REFACTOR add event priority?
//    // We need the game manager to handle teleports first to make sure no player information gets lost by accident.
//    @EventCallback(priority = EventPriority.HIGHEST)
//    public void handlePlayerTeleport(final PlayerTeleportEvent event)
//    {
//        gameManager.handlePlayerTeleport(event);
//    }

	public void onPlayerItemDamaged(PlayerItemDamageEvent event)
	{
		BingoSession session = getSession(event.player().world());
		if (session != null && session.isRunning())
		{
			((BingoGame)session.phase()).handlePlayerItemDamaged(event);
		}
	}

	public void handleStatisticIncrement(final PlayerStatisticIncrementEvent event)
	{
		if (disableStatistics)
			return;

		BingoSession session = getSession(event.player().world());
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null)
		{
			game.getProgressTracker().handlePlayerStatIncrement(event);
		}
	}

	public void handleBingoStatisticCompleted(final BingoEvents.StatisticCompleted event)
	{
		if (disableStatistics)
			return;

		BingoSession session = event.session();
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null)
		{
			game.getProgressTracker().handleBingoStatisticCompleted(event);
		}
	}

	public void handlePlayerMove(final PlayerMoveEvent event)
	{
		BingoSession session = getSession(event.player().world());
		if (session == null)
			return;

		if (session.isRunning())
		{
			((BingoGame)session.phase()).handlePlayerMove(event);
		}
	}

	public void handlePlayerPortal(final PlayerPortalEvent event) {
		BingoSession session = getSession(event.fromPosition().world());
		if (session == null)
			return;

		session.handlePlayerPortalEvent(event);
	}

	public void handlePlayerBlockBreak(final BlockBreakEvent event) {
		BingoSession session = getSession(event.player().world());
		if (session == null)
			return;

		session.handlePlayerBlockBreak(event);
	}

	public void handlePlayerBlockPlace(final BlockPlaceEvent event) {
		BingoSession session = getSession(event.player().world());
		if (session == null)
			return;

		session.handlePlayerBlockPlace(event);
	}

	public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
	{
		event.getSession().handleSettingsUpdated(event);
	}

	public void handleParticipantJoinedTeam(final ParticipantJoinedTeamEvent event) {
		event.getSession().handleParticipantJoinedTeam(event);
	}

	public void handleParticipantLeftTeam(final ParticipantLeftTeamEvent event) {
		event.getSession().handleParticipantLeftTeam(event);
	}

	public void handleBingoPlaySoundEvent(final BingoPlaySoundEvent event) {
		event.getSession().handlePlaySoundEvent(event);
	}
}
