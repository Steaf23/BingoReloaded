package io.github.steaf23.bingoreloadedcompanion.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.steaf23.bingoreloaded.protocol.data.ClientSettings;
import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.client.creator.CreatorSuite;
import io.github.steaf23.bingoreloadedcompanion.client.hud.BingoCardHudElement;
import io.github.steaf23.bingoreloadedcompanion.client.hud.ConfigOption;
import io.github.steaf23.bingoreloadedcompanion.client.hud.ConfigurableHudRegistry;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudConfigManager;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudInfo;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudPlacement;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudTimer;
import io.github.steaf23.bingoreloadedcompanion.network.ClientGetCreatorListPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ClientHelloPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ClientUpsertCreatorCardPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ClientUpsertCreatorListPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ServerCreatorListPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ServerHotswapPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ServerOpenCreatorPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ServerUpdateCardPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class BingoReloadedCompanionClient implements ClientModInitializer {

	public static final Identifier BINGO_CARD_TASKS = ConfigurableHudRegistry.registerSubElement("bingocard", "tasks",
			new HudInfo(false, 5 * 22, 5 * 22),
			new HudPlacement(0.014, 0.1, true, 3.0f, 3.0f, 0.6));

	public static final Identifier BINGO_CARD_GAMEMODE = ConfigurableHudRegistry.registerSubElement("bingocard", "gamemode",
			new HudInfo(false, 128, 32),
			new HudPlacement(0, 0, true, 3.0f, 3.0f, 1.0));

	public static final Identifier CREATOR_USE_CLIENT_CREATOR = ConfigurableHudRegistry.createSimpleOption("creator", "use_client_creator",
			new ConfigOption(true));

	private static final HudConfigManager HUD_CONFIG = new HudConfigManager();

	private final CreatorSuite suite = new CreatorSuite();

	@Override
	public void onInitializeClient() {
		PayloadTypeRegistry.serverboundPlay().register(ClientHelloPayload.ID, ClientHelloPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ClientGetCreatorListPayload.ID, ClientGetCreatorListPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ClientUpsertCreatorListPayload.ID, ClientUpsertCreatorListPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ClientUpsertCreatorCardPayload.ID, ClientUpsertCreatorCardPayload.CODEC);

		PayloadTypeRegistry.clientboundPlay().register(ServerUpdateCardPayload.ID, ServerUpdateCardPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ServerHotswapPayload.ID, ServerHotswapPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ServerOpenCreatorPayload.ID, ServerOpenCreatorPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(ServerCreatorListPayload.ID, ServerCreatorListPayload.CODEC);

		HUD_CONFIG.load();

		ClientTickEvents.END_CLIENT_TICK.register(new HudTimer());

		BingoCardHudElement cardElement = new BingoCardHudElement(HUD_CONFIG, false);

		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(BingoReloadedCompanion.ADDON_ID, "card_display"), cardElement);

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof AbstractContainerScreen<?> containerScreen) {
				cardElement.setRenderingInScreen(true);
				ScreenEvents.afterExtract(screen).register((renderedScreen, drawContext, mouseX, mouseY, tickDelta) -> {

					cardElement.renderFromScreen(drawContext, tickDelta);
				});
				ScreenEvents.remove(screen).register((renderedScreen) -> {
					cardElement.setRenderingInScreen(false);
				});
			}
		});

		ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
			if (client.player == null) {
				return;
			}

			ClientSettings settings = new ClientSettings(
					HUD_CONFIG.getBooleanOption(BingoReloadedCompanionClient.CREATOR_USE_CLIENT_CREATOR)
			);

			BingoReloadedCompanionClient.sendPayloadToServer(new ClientHelloPayload(settings));
		}));

		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			cardElement.setCard(null);
		}));

		ClientPlayNetworking.registerGlobalReceiver(ServerUpdateCardPayload.ID,
				(payload, context) -> {
					cardElement.setCard(payload.card().orElse(null));
				});
		ClientPlayNetworking.registerGlobalReceiver(ServerHotswapPayload.ID,
				(payload, context) -> {
					cardElement.setHotswapHolders(payload.holders());
				});
		ClientPlayNetworking.registerGlobalReceiver(ServerOpenCreatorPayload.ID,
				(payload, context) -> {
					suite.openListEditor(payload.creatorContext());
				});
		ClientPlayNetworking.registerGlobalReceiver(ServerCreatorListPayload.ID,
				(payload, context) -> {
					suite.listReceived(payload.list());
				});

		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("bingoreloadedcompanion", "main"));
		KeyMapping toggleCardVisibility = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.bingoreloadedcompanion.toggle_card_visibility",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (toggleCardVisibility.consumeClick()) {
				cardElement.setVisible(cardElement.isHidden());
			}
		});
	}

	public static HudConfigManager getHudConfig() {
		return HUD_CONFIG;
	}

	public static void sendPayloadToServer(CustomPacketPayload payload) {
		if (ClientPlayNetworking.canSend(payload.type())) {
			ClientPlayNetworking.send(payload);
		}
	}
}
