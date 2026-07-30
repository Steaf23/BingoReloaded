package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import net.kyori.adventure.key.Key;

import java.util.Collection;

public interface WorldHandle {

	Key key();
	Collection<? extends PlayerHandle> players();
	GlobalPosition spawnPoint();
	DimensionType dimensionType();

	void spawnEntity(EntityType type, GlobalPosition pos);

	void setStorming(boolean storm);
	void setTimeOfDay(long time);
	BiomeType biomeAtPos(GlobalPosition pos);

	ItemType typeAtPos(GlobalPosition pos);
	void setTypeAtPos(GlobalPosition pos, ItemType type);
	GlobalPosition highestBlockAt(GlobalPosition pos);

	void dropItem(StackHandle item, GlobalPosition location);
}
