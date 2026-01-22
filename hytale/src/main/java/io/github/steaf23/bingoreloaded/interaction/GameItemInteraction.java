package io.github.steaf23.bingoreloaded.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.steaf23.bingoreloaded.BingoReloadedHytale;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandleHytale;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleHytale;
import org.jetbrains.annotations.NotNull;

public class GameItemInteraction extends SimpleInstantInteraction {

	public static final BuilderCodec<GameItemInteraction> CODEC = BuilderCodec.builder(
			GameItemInteraction.class, GameItemInteraction::new, SimpleInstantInteraction.CODEC
	).build();

	@Override
	protected void firstRun(@NotNull InteractionType interactionType, @NotNull InteractionContext interactionContext, @NotNull CooldownHandler cooldownHandler) {
		StackHandleHytale stack = new StackHandleHytale(interactionContext.getHeldItem());

		CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
		if (commandBuffer == null) {
			interactionContext.getState().state = InteractionState.Failed;
			return;
		}

		PlayerHandleHytale player = new PlayerHandleHytale(commandBuffer.getComponent(interactionContext.getEntity(), PlayerRef.getComponentType()));

		BingoReloadedHytale.EVENT_LISTENER.playerSecondaryItemInteraction(commandBuffer.getExternalData().getWorld(), player, stack);
	}
}
