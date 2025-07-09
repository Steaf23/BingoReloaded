package io.github.steaf23.bingoreloaded.lib.api;

import java.util.Collection;
import java.util.UUID;

public interface WorldHandle {

	String name();
	UUID uniqueId();
	Collection<? extends PlayerHandle> players();
	WorldPosition spawnPoint();
	DimensionType dimension();

	void setStorming(boolean storm);
	void setTimeOfDay(long time);
	BiomeType biomeAtPos(WorldPosition pos);

	ItemType typeAtPos(WorldPosition pos);
	void setTypeAtPos(WorldPosition pos, ItemType type);
	WorldPosition highestBlockAt(WorldPosition pos);

	void dropItem(StackHandle item, WorldPosition location);
}
