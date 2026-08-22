package io.github.steaf23.bingoreloaded.lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerAdvancementCompleted {

	Event<PlayerAdvancementCompleted> EVENT = EventFactory.createArrayBacked(PlayerAdvancementCompleted.class,
			(listeners) -> (player, advancement, lastCriterion) -> {
				for (PlayerAdvancementCompleted listener : listeners) {
					listener.advancementCompleted(player, advancement, lastCriterion);
				}
			});

	void advancementCompleted(ServerPlayer player, AdvancementHolder advancement, String lastCriterion);
}
