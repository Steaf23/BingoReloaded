package io.github.steaf23.bingoreloadedcompanion.client;

import io.github.steaf23.bingoreloadedcompanion.BingoReloadedCompanion;
import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.client.hud.BingoCardHudElement;
import io.github.steaf23.bingoreloadedcompanion.client.hud.ConfigurableHudRegistry;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudConfigManager;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudInfo;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudPlacement;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudTimer;
import io.github.steaf23.bingoreloadedcompanion.network.ClientHelloPayload;
import io.github.steaf23.bingoreloadedcompanion.network.EditTaskListPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ServerHotswapPayload;
import io.github.steaf23.bingoreloadedcompanion.network.ServerUpdateCardPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class BingoReloadedCompanionClient implements ClientModInitializer {

	public static final Identifier BINGO_CARD_TASKS = ConfigurableHudRegistry.registerSubElement("bingocard", "tasks",
			new HudInfo(false, 5 * 22, 5 * 22),
			new HudPlacement(0.014, 0.1, true, 3.0f, 3.0f, 0.6));

	public static final Identifier BINGO_CARD_GAMEMODE = ConfigurableHudRegistry.registerSubElement("bingocard", "gamemode",
			new HudInfo(false, 128, 32),
			new HudPlacement(0, 0, true, 3.0f, 3.0f, 1.0));

	private static final HudConfigManager HUD_CONFIG = new HudConfigManager();

	public BingoReloadedCompanionClient() {
	}

	@Override
	public void onInitializeClient() {

		PayloadTypeRegistry.playC2S().register(ClientHelloPayload.ID, ClientHelloPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(EditTaskListPayload.ID, EditTaskListPayload.CODEC);

		PayloadTypeRegistry.playS2C().register(ServerUpdateCardPayload.ID, ServerUpdateCardPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(ServerHotswapPayload.ID, ServerHotswapPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(EditTaskListPayload.ID, EditTaskListPayload.CODEC);

		HUD_CONFIG.load();

		ClientTickEvents.END_CLIENT_TICK.register(new HudTimer());

		BingoCardHudElement cardElement = new BingoCardHudElement(HUD_CONFIG);

		HudElementRegistry.addLast(Identifier.of(BingoReloadedCompanion.ADDON_ID, "card_display"), cardElement);

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof HandledScreen<?> handledScreen) {
				cardElement.setRenderingInScreen(true);
				ScreenEvents.afterRender(screen).register((renderedScreen, drawContext, mouseX, mouseY, tickDelta) -> {

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

			ClientHelloPayload payload = new ClientHelloPayload();
			if (ClientPlayNetworking.canSend(payload.getId())) {
				ClientPlayNetworking.send(payload);
			}

		}));

		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			if (client == null) {
				return;
			}

			cardElement.setCard(null);
		}));

		ClientPlayNetworking.registerGlobalReceiver(ServerUpdateCardPayload.ID,
				(payload, context) -> {
					BingoCard card = payload.getCard();
					cardElement.setCard(card);
				});
		ClientPlayNetworking.registerGlobalReceiver(ServerHotswapPayload.ID,
				(payload, context) -> {
					cardElement.setHotswapHolders(payload.holders);
				});

		KeyBinding toggleCardVisibility = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.bingoreloadedcompanion.toggle_card_visibility",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				"category.bingoreloadedcompanion"));

//		ClientPlayNetworking.registerGlobalReceiver(EditTaskListPayload.ID,
//				(payload, context) -> {
//					context.client().setScreen(new BingoCardTaskListScreen(Text.empty(), payload.tasks()));
//				});
//
//
//		KeyBinding binding2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
//				"key.bingoreloadedcompanion.test2",
//				InputUtil.Type.KEYSYM,
//				GLFW.GLFW_KEY_Y,
//				"category.bingoreloadedcompanion.test"));

//		List<Task> testTasks = new ArrayList<>();
//		for (int i = 0; i < 25; i++) {
//			testTasks.add(new Task(Task.TaskCompletion.INCOMPLETE, Identifier.of("bingoreloaded:item"), Items.PAPER, 1));
//		}
//		BingoCard testCard5x = new BingoCard(BingoGamemode.HOTSWAP, 5, testTasks);
//		BingoCard testCard3x = new BingoCard(BingoGamemode.REGULAR, 3, testTasks.subList(0, 9));
//
//		ClientTickEvents.END_CLIENT_TICK.register(c -> {
//			if (binding.wasPressed()) {
//				cardElement.setCard(testCard5x);
////				c.setScreen(new BingoCardTaskListScreen(Text.empty(), List.of(new ItemTask(Identifier.of("minecraft:polished_granite"), 5), new ItemTask(Identifier.of("minecraft:budding_amethyst"), 1))));
//			} else if (binding2.wasPressed()) {
//				cardElement.setCard(testCard3x);
//			}
//		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (toggleCardVisibility.wasPressed()) {
				cardElement.setVisible(cardElement.isHidden());
			}
		});
	}

	public static HudConfigManager getHudConfig() {
		return HUD_CONFIG;
	}
}
