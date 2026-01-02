package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;

public class TeamShulker extends GameItem {

	public TeamShulker() {
		super(BingoReloaded.resourceKey("team_shulker"));
	}

	@Override
	public ItemTemplate defaultTemplate() {
		return null;
	}

	@Override
	public EventResult<?> use(StackHandle stack, BingoParticipant participant, BingoConfigurationData config) {
		return null;
	}

	@Override
	public boolean canLeaveInventory() {
		return true;
	}
}
