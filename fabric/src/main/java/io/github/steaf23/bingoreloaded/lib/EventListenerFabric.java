package io.github.steaf23.bingoreloaded.lib;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.platform.FabricServer;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandleFabric;
import io.github.steaf23.bingoreloaded.lib.event.EventResult;
import io.github.steaf23.bingoreloaded.lib.event.PlatformEventDispatcher;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EventListenerFabric {

	private final FabricServer server;
	private final PlatformEventDispatcher dispatcher;

	public EventListenerFabric(FabricServer server, PlatformEventDispatcher dispatcher) {
		this.server = server;
		this.dispatcher = dispatcher;

		ServerPlayerEvents.JOIN.register(this::playerJoin);
		ServerPlayerEvents.LEAVE.register(this::playerLeave);
		UseItemCallback.EVENT.register(this::playerUseItem);
	}

	public void playerJoin(ServerPlayer player) {
		dispatcher.sendPlayerJoinsServer(new PlayerHandleFabric(server, player));
	}

	public void playerLeave(ServerPlayer player) {
		dispatcher.sendPlayerQuitsServer(new PlayerHandleFabric(server, player));
	}

	public InteractionResult playerUseItem(Player player, Level level, InteractionHand hand) {
		EventResult<?> result = dispatcher.sendPlayerUseItem(
				new PlayerHandleFabric(server, (ServerPlayer)player),
				new StackHandleFabric(player.getItemInHand(hand)));
		return result.consume() ? InteractionResult.FAIL : InteractionResult.PASS;
	}

}
