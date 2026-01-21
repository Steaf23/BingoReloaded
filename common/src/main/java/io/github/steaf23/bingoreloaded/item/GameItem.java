package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public abstract class GameItem implements Keyed {

	private final Key id;

	public GameItem(Key id) {
		this.id = id;
	}

	@Override
	public @NotNull Key key() {
		return id;
	}

	public boolean canLeaveInventory() {
		return false;
	}

	public abstract EventResult<?> use(StackHandle stack, BingoParticipant participant, BingoConfigurationData config);
}
