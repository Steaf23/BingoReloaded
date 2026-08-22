package io.github.steaf23.bingoreloaded.lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.InteractionResult;

public interface PlayerStatIncrement {
	Event<PlayerStatIncrement> EVENT = EventFactory.createArrayBacked(PlayerStatIncrement.class,
			(listeners) -> (player, stat, count, old, newV) -> {
				for (PlayerStatIncrement listener : listeners) {
					InteractionResult result = listener.incrementStat(player, stat, count, old, newV);

					if (result != InteractionResult.PASS) {
						return result;
					}
				}

				return InteractionResult.PASS;
			});

	InteractionResult incrementStat(ServerPlayer player, Stat<?> stat, int increment, int oldValue, int newValue);
}
