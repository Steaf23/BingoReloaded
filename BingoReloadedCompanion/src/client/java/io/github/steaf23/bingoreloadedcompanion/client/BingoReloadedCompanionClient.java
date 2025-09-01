package io.github.steaf23.bingoreloadedcompanion.client;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.network.ClientHelloPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ServerUpdateCardPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;

public class BingoReloadedCompanionClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

		PayloadTypeRegistry.playC2S().register(ClientHelloPayload.ID, ClientHelloPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(ServerUpdateCardPayload.ID, ServerUpdateCardPayload.CODEC);

		BingoCardHudElement cardElement = new BingoCardHudElement();

		HudElementRegistry.addLast(Identifier.of(BingoReloadedCompanion.ADDON_ID, "dd"), cardElement);

		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			if (client.player == null) {
				return;
			}

			ClientHelloPayload payload = new ClientHelloPayload();
			if (ClientPlayNetworking.canSend(payload.getId())) {
				ClientPlayNetworking.send(payload);
			}

		}));

		ClientPlayNetworking.registerGlobalReceiver(ServerUpdateCardPayload.ID,
				(payload, context) -> {
					BingoCard card = payload.getCard();
					cardElement.setCard(card);
				});
    }
}
