package io.github.steaf23.bingoreloaded.lib.event;

import io.github.steaf23.bingoreloaded.lib.api.WorldPosition;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import org.jetbrains.annotations.Nullable;

public class EventResults {

	private EventResults() {
	}

	public static EventResult<PlayerDeathResult> playerDeathResult(boolean cancel, boolean keepInventory) {
		return new EventResult<>(cancel, new PlayerDeathResult(keepInventory));
	}

	public record PlayerDeathResult(boolean keepInventory) {

	}

	public static EventResult<PlayerRespawnResult> playerRespawnResult(boolean cancel, boolean overwriteSpawnPoint, @Nullable WorldPosition newSpawnPoint) {
		return new EventResult<>(cancel, new PlayerRespawnResult(overwriteSpawnPoint, newSpawnPoint));
	}

	public record PlayerRespawnResult(boolean overwriteSpawnPoint, @Nullable WorldPosition newSpawnPoint) {

	}

	public static EventResult<PlayerMoveResult> playerMoveResult(boolean cancel, boolean overwritePosition, @Nullable WorldPosition newPosition) {
		return new EventResult<>(cancel, new PlayerMoveResult(overwritePosition, newPosition));
	}

	public record PlayerMoveResult(boolean overwritePosition, @Nullable WorldPosition newPosition) {

	}

	public static EventResult<PlayerPickupResult> playerPickupResult(boolean cancel, boolean removeItem, boolean overwriteItem, @Nullable StackHandle newItem) {
		return new EventResult<>(cancel, new PlayerPickupResult(removeItem, overwriteItem, newItem));
	}

	public record PlayerPickupResult(boolean removeItem, boolean overwriteItem, @Nullable StackHandle newItem) {

	}
}


