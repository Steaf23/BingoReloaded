package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.config.BingoConfigurationData;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public class TeamShulker extends GameItem {

	public static Key ID = BingoReloaded.resourceKey("team_shulker");

	public TeamShulker() {
		super(ID);
	}

	@Override
	public EventResult<?> use(StackHandle stack, BingoParticipant participant, BingoConfigurationData config) {
		participant.sendMessage(Component.text("You clicked on the team shulker :D"));
		return EventResult.CONSUME;
	}
}
