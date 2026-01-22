package io.github.steaf23.bingoreloaded.lib.event;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.steaf23.bingoreloaded.lib.api.PlayerInput;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleHytale;

public class HytaleEventListener {

	final EventRegistry registry;
	final PlatformEventDispatcher dispatcher;

	public HytaleEventListener(EventRegistry registry, PlatformEventDispatcher dispatcher) {
		this.registry = registry;
		this.dispatcher = dispatcher;

		registry.registerGlobal(PlayerReadyEvent.class, this::handlePlayerReady);
		registry.registerGlobal(PlayerDisconnectEvent.class, this::handlePlayerDisconnect);
	}

	private void handlePlayerReady(final PlayerReadyEvent event) {
		dispatcher.sendPlayerJoinsServer(new PlayerHandleHytale(event.getPlayerRef().getStore().getComponent(event.getPlayerRef(), PlayerRef.getComponentType())));
	}

	private void handlePlayerDisconnect(final PlayerDisconnectEvent event) {
		Universe.get().getWorld(event.getPlayerRef().getWorldUuid()).execute( () -> {
			dispatcher.sendPlayerQuitsServer(new PlayerHandleHytale(event.getPlayerRef()));
		});
	}

	public void playerSecondaryItemInteraction(World world, PlayerHandle player, StackHandle stack) {
		world.execute(() -> {
			dispatcher.sendPlayerInteracted(player, stack, new PlayerInput(false, true, true));
		});
	}
}
