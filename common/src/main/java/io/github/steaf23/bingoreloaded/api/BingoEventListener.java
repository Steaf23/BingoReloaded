package io.github.steaf23.bingoreloaded.api;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.GameManager;
import io.github.steaf23.bingoreloaded.gameloop.phase.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.phase.PregameLobby;
import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.InteractAction;
import io.github.steaf23.bingoreloaded.lib.api.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.StatisticHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldHandle;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.event.PlatformEventDispatcher;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.event.EventResults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class BingoEventListener implements PlatformEventDispatcher {
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

	public void handleTaskProgressCompleted(final BingoEvents.TaskProgressCompleted event) {
//		BingoSession session = event.session();
//		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
//		if (game != null)
//		{
//			game.handleBingoTaskComplete(event);
//		}
	}

	public void handleCountdownFinished(final BingoEvents.CountdownTimerFinished event)
	{
//		BingoGame game = event.session() != null && event.session().isRunning() ? (BingoGame)event.session().phase() : null;
//		if (game != null)
//		{
//			game.handleCountdownFinished(event);
//		}
	}

	public void handleBingoStatisticCompleted(final BingoEvents.StatisticCompleted event)
	{
//		if (disableStatistics)
//			return;
//
//		BingoSession session = event.session();
//		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
//		if (game != null)
//		{
//			game.getProgressTracker().handleBingoStatisticCompleted(event);
//		}
	}

	public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
	{
		event.getSession().handleSettingsUpdated(event);
	}

	@Override
	public EventResult<?> sendPlayerMove(PlayerHandle player, WorldPosition from, WorldPosition to) {
		BingoGame game = getBingoGame(player.world());
		if (game == null) return EventResult.PASS;

		return game.handlePlayerMove(player, from, to);
	}

	@Override
	public EventResult<?> sendPlayerTeleport(PlayerHandle player, WorldPosition from, WorldPosition to) {
		return gameManager.handlePlayerTeleport(player, from, to);
	}


	@Override
	public EventResult<EventResults.PlayerMoveResult> sendPlayerPortal(PlayerHandle player, WorldPosition from, WorldPosition to) {
		// We only care about this event when it was sent from a bingo world.
		BingoSession session = getSession(from.world());
		if (session == null)
			return new EventResult<>(false, null);

		return session.handlePlayerPortalEvent(player, from, to);
	}

	@Override
	public EventResult<?> sendPlayerDroppedStack(PlayerHandle player, StackHandle item) {
		BingoSession session = getSession(player.world());
		if (session == null)
			return EventResult.PASS;

		EventResult<?> sessionResult = session.handlePlayerDroppedStack(player, item);

		BingoGame game = session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null && game.hasStarted())
		{
			game.getProgressTracker().handlePlayerDroppedItem(player, item);
		}

		return sessionResult;
	}

	@Override
	public EventResult<?> sendPlayerStackDamaged(PlayerHandle player, StackHandle item) {
		BingoGame game = getBingoGame(player.world());
		if (game == null) return EventResult.PASS;

		return game.handlePlayerStackDamaged(player, item);
	}

	@Override
	public EventResult<?> sendPlayerInteracted(PlayerHandle player, @Nullable StackHandle handItem, InteractAction action) {
		BingoSession session = getSession(player.world());
		if (session == null) return EventResult.PASS;

		// FIXME: REFACTOR determine if this is needed
//        // Determine if the event is fired for the correct hand, to avoid duplicate events
//        if (!(event.getHand() == EquipmentSlot.HAND && event.getPlayer().getInventory().getItemInMainHand().equals(event.getItem()) ||
//                event.getHand() == EquipmentSlot.OFF_HAND && event.getPlayer().getInventory().getItemInOffHand().equals(event.getItem())))
//        {
//            return;
//        }

		return session.phase().handlePlayerInteracted(player, handItem, action);
	}

	@Override
	public EventResult<?> sendPlayerFallDamage(PlayerHandle player) {
		BingoGame game = getBingoGame(player.world());
		if (game == null) return EventResult.PASS;

		return game.handlePlayerFallDamage(player);
	}

	@Override
	public EventResult<EventResults.PlayerDeathResult> sendPlayerDeath(PlayerHandle player, Collection<? extends StackHandle> drops) {
		BingoGame game = getBingoGame(player.world());
		if (game == null) return new EventResult<>(false, null);

		return game.handlePlayerDeath(player, drops);
	}

	@Override
	public EventResult<EventResults.PlayerRespawnResult> sendPlayerRespawn(PlayerHandle player, boolean isBedSpawn, boolean isAnchorSpawn) {
		BingoSession session = getSession(player.world());
		if (session != null && session.getPhase() instanceof PregameLobby lobby) {
			lobby.handlePlayerRespawn(player);
		}

		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null)
		{
			return game.handlePlayerRespawn(player, isBedSpawn, isAnchorSpawn);
		}

		return new EventResult<>(false, null);
	}

	@Override
	public EventResult<?> sendPlayerJoinsServer(PlayerHandle player)
	{
		return gameManager.handlePlayerJoinsServer(player);
	}

	@Override
	public EventResult<?> sendPlayerQuitsServer(final PlayerHandle player)
	{
		return gameManager.handlePlayerQuitsServer(player);
	}

	@Override
	public EventResult<?> sendPlayerBreaksBlock(PlayerHandle player, WorldPosition position, ItemType blockType) {
		BingoSession session = getSession(player.world());
		if (session == null)
			return EventResult.PASS;

		return session.handlePlayerBlockBreak(player, position, blockType);
	}

	@Override
	public EventResult<?> sendPlayerPlacesBlock(PlayerHandle player, WorldPosition position, ItemType blockType) {
		BingoSession session = getSession(player.world());
		if (session == null)
			return EventResult.PASS;

		return session.handlePlayerBlockPlace(player, position, blockType);
	}

	@Override
	public EventResult<?> sendPlayerStatisticIncrement(PlayerHandle player, StatisticHandle statistic, int newValue) {
		if (disableStatistics)
			return EventResult.PASS;

		BingoSession session = getSession(player.world());
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null)
		{
			game.getProgressTracker().handlePlayerStatIncrement(player, statistic, newValue);
		}

		return EventResult.PASS;
	}

	@Override
	public EventResult<?> sendPlayerAdvancementDone(PlayerHandle player, AdvancementHandle advancement) {
		if (disableAdvancements)
			return EventResult.PASS;

		BingoSession session = getSession(player.world());
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null)
		{
			game.getProgressTracker().handlePlayerAdvancementDone(player, advancement);
		}

		return EventResult.PASS;
	}

	@Override
	public EventResult<EventResults.PlayerPickupResult> sendPlayerPickupStack(PlayerHandle player, StackHandle stack, WorldPosition itemLocation) {
		BingoSession session = getSession(player.world());
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null && game.hasStarted())
		{
			return game.getProgressTracker().handlePlayerPickupItem(player, stack, itemLocation);
		}

		return new EventResult<>(false, null);
	}

	@Override
	public EventResult<?> sendPlayerInventoryClick(PlayerHandle player, StackHandle itemOnCursor) {
		BingoSession session = getSession(player.world());
		BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
		if (game != null && game.hasStarted())
		{
			game.getProgressTracker().handleInventoryClicked(player, itemOnCursor);
		}

		return EventResult.PASS;
	}


	private @Nullable BingoGame getBingoGame(WorldHandle world) {
		BingoSession session = getSession(world);
		if (session == null) return null;

		if (session.getPhase() instanceof BingoGame game) {
			return game;
		}

		return null;
	}
}
