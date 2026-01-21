package io.github.steaf23.bingoreloaded.lib.event;

import io.github.steaf23.bingoreloaded.lib.api.AdvancementHandle;
import io.github.steaf23.bingoreloaded.lib.api.PlayerInput;
import io.github.steaf23.bingoreloaded.lib.api.StatisticDefinition;
import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Represents the events that are expected to be sent by the target platform/software
 */
public interface PlatformEventDispatcher {

	EventResult<?> sendPlayerMove(PlayerHandle player, WorldPosition from, WorldPosition to);
	EventResult<?> sendPlayerTeleport(PlayerHandle player, WorldPosition from, WorldPosition to);
	EventResult<EventResults.PlayerMoveResult> sendPlayerPortal(PlayerHandle player, WorldPosition from, WorldPosition to);
	EventResult<?> sendPlayerDroppedStack(PlayerHandle player, StackHandle item);
	EventResult<?> sendPlayerStackDamaged(PlayerHandle player, StackHandle item);
	EventResult<?> sendPlayerInteracted(PlayerHandle player, @Nullable StackHandle handItem, PlayerInput action);
	EventResult<?> sendPlayerFallDamage(PlayerHandle player);
	EventResult<EventResults.PlayerDeathResult> sendPlayerDeath(PlayerHandle player, Collection<StackHandle> drops);
	EventResult<EventResults.PlayerRespawnResult> sendPlayerRespawn(PlayerHandle player, boolean isBedSpawn, boolean isAnchorSpawn);
	EventResult<?> sendPlayerJoinsServer(PlayerHandle player);
	EventResult<?> sendPlayerQuitsServer(PlayerHandle player);
	EventResult<?> sendPlayerBreaksBlock(PlayerHandle player, WorldPosition position, ItemType blockType);
	EventResult<?> sendPlayerPlacesBlock(PlayerHandle player, WorldPosition position, ItemType blockType);
	EventResult<?> sendPlayerStatisticIncrement(PlayerHandle player, StatisticDefinition statistic, int newValue);
	EventResult<?> sendPlayerAdvancementDone(PlayerHandle player, AdvancementHandle advancement);
	EventResult<EventResults.PlayerPickupResult> sendPlayerPickupStack(PlayerHandle player, StackHandle stack, WorldPosition itemLocation);
	EventResult<?> sendPlayerInventoryClick(PlayerHandle player, StackHandle itemOnCursor, boolean resultSlot, boolean shiftClick);
}
