package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface PlayerHandle extends Audience {

	String playerName();
	Component displayName();
	UUID uniqueId();
	WorldHandle world();
	WorldPosition position();
	WorldPosition respawnPoint();

	void teleport(WorldPosition pos);

	void clearInventory();
	void openInventory(InventoryHandle handle);

	/**
	 * @param newSpawn new position.
	 * @param force true if setting the spawn point should ignore valid bed/respawn positions too.
	 */
	void setRespawnPoint(WorldPosition newSpawn, boolean force);
}
