package io.github.steaf23.bingoreloaded.lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public interface PlayerDroppedItem {

	Event<PlayerDroppedItem> EVENT = EventFactory.createArrayBacked(PlayerDroppedItem.class,
			(listeners) -> (player, stack) -> {
				for (PlayerDroppedItem listener : listeners) {
					InteractionResult result = listener.droppedItem(player, stack);

					if (result != InteractionResult.PASS) {
						return result;
					}
				}

			return InteractionResult.PASS;
			});

	InteractionResult droppedItem(ServerPlayer player, ItemStack stack);
}
