package io.github.steaf23.bingoreloadedcompanion.client.config;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import io.github.steaf23.bingoreloaded.protocol.data.BingoCard;
import io.github.steaf23.bingoreloaded.protocol.data.BingoGamemode;
import io.github.steaf23.bingoreloaded.protocol.data.ClientSettings;
import io.github.steaf23.bingoreloaded.protocol.data.task.Task;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloadedcompanion.client.BingoReloadedCompanionClient;
import io.github.steaf23.bingoreloadedcompanion.client.hud.BingoCardHudElement;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudConfigManager;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudPlacement;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import io.github.steaf23.bingoreloadedcompanion.network.ClientHelloPayload;
import net.kyori.adventure.key.Key;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.CommonColors;

import java.util.ArrayList;
import java.util.List;

public class BingoConfigScreen extends Screen {

	private final Screen modMenuScreen;
	private final HudConfigManager configManager;
	private final BingoCardHudElement previewCard;

	private static final Identifier HIDE_BUTTON = Identifier.parse("bingoreloadedcompanion:hide_button");
	private static final Identifier HIDE_BUTTON_HIGHLIGHT = Identifier.parse("bingoreloadedcompanion:hide_button_highlighted");
	private static final Identifier SHOW_BUTTON = Identifier.parse("bingoreloadedcompanion:show_button");
	private static final Identifier SHOW_BUTTON_HIGHLIGHT = Identifier.parse("bingoreloadedcompanion:show_button_highlighted");
	private static final Identifier RESET_BUTTON = Identifier.parse("bingoreloadedcompanion:reset_button");
	private static final Identifier RESET_BUTTON_HIGHLIGHT = Identifier.parse("bingoreloadedcompanion:reset_button_highlighted");
	private static final Identifier SCALE_BUTTON = Identifier.parse("bingoreloadedcompanion:scale_button");
	private static final Identifier SCALE_BUTTON_HIGHLIGHT = Identifier.parse("bingoreloadedcompanion:scale_button_highlighted");
	private static final Identifier SLIDER_BUTTON_SLIDER = Identifier.parse("bingoreloadedcompanion:slider_button_slider");
	private static final Identifier SLIDER_BUTTON_SLIDER_HIGHLIGHT = Identifier.parse("bingoreloadedcompanion:slider_button_slider_highlighted");
	private static final Identifier SLIDER_BUTTON_BACKGROUND = Identifier.parse("bingoreloadedcompanion:slider_button_background");
	private static final Identifier SLIDER_BUTTON_PROGRESS = Identifier.parse("bingoreloadedcompanion:slider_button_progress");
	private static final int BUTTON_WIDTH = 14;
	private static final int SLIDER_WIDTH = 10;
	private static final int SLIDER_BACKGROUND_WIDTH = BUTTON_WIDTH * 2 + 2;
	private static final int BUTTON_HEIGHT = 14;

	private Identifier selectedElement;
	private Identifier hoveringElement;

	private boolean dragging = false;
	private boolean draggingSlider = false;
	private double clickOffsetX;
	private double clickOffsetY;

	private boolean removed = false;

	private final List<Identifier> elements = List.of(BingoReloadedCompanionClient.BINGO_CARD_TASKS, BingoReloadedCompanionClient.BINGO_CARD_GAMEMODE);

	private Checkbox clientSideCreator;

	protected BingoConfigScreen(Screen modMenuScreen, HudConfigManager hudConfig) {
		super(Component.nullToEmpty("Bingo Reloaded Options"));
		this.modMenuScreen = modMenuScreen;

		this.configManager = hudConfig;
		this.previewCard = new BingoCardHudElement(this.configManager, true);

		List<Task> testTasks = new ArrayList<>();
		for (int i = 0; i < 25; i++) {
			testTasks.add(new Task(
					new TaskDefinition(TaskId.DUMMY, "", Key.key("paper"),"", 64),
					Task.TaskCompletion.INCOMPLETE, 1));
		}
		BingoCard testCard5x = new BingoCard(BingoGamemode.REGULAR, 5, testTasks);
		previewCard.setCard(testCard5x);
	}

	@Override
	protected void init() {

		Button backButton = Button.builder(Component.nullToEmpty("Save & Exit"), (btn) -> {
			if (minecraft == null) return;
			configManager.save();
			closeScreen();
		})
				.pos(5, height - 20 - 5)
				.build();

		addRenderableWidget(backButton);

		int buttonWidth = Math.max(150, font.width(Component.nullToEmpty("Reset all elements")) + 10);

		Button resetButton = Button.builder(Component.nullToEmpty("Reset all elements"), (btn) -> {
					configManager.resetAllElements();
				})
				.pos(width - buttonWidth - 5, height - 20 - 5)
				.build();

		addRenderableWidget(resetButton);

		FrameLayout frame = new FrameLayout(200, 100);
		LinearLayout layout = LinearLayout.vertical();
		frame.addChild(layout);

		clientSideCreator = Checkbox.builder(Component.literal("Use client side card creator"), font)
				.selected(configManager.getBooleanOption(BingoReloadedCompanionClient.CREATOR_USE_CLIENT_CREATOR))
				.onValueChange((box, val) -> configManager.setBooleanOption(BingoReloadedCompanionClient.CREATOR_USE_CLIENT_CREATOR, val))
				.build();
		layout.addChild(clientSideCreator);

		frame.arrangeElements();
		frame.setX(0);
		frame.setY(height - frame.getHeight());
		frame.arrangeElements();

		frame.visitWidgets(this::addRenderableWidget);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
		super.extractRenderState(context, mouseX, mouseY, deltaTicks);

		previewCard.renderElement(context, deltaTicks);

		{ // Draw the info tooltip at the top
			double infoAlpha = mouseY < 40 ? 0.5 : 1.0;

			List<Component> text = List.of(
					Component.nullToEmpty("Click on an element to select it."),
					Component.nullToEmpty("You can move it by dragging the mouse or using the movement keys.")
			);
			int recordWidth = 0;
			for (Component t : text) {
				int textWidth = font.width(t);
				if (textWidth > recordWidth) {
					recordWidth = textWidth;
				}
			}

			int textHeight = text.size() * 15 + 5;
			int backgroundWidth = recordWidth + 10;
			int backgroundStartX = width / 2 - (backgroundWidth / 2);
			context.fill(backgroundStartX, 0, backgroundStartX + backgroundWidth, textHeight, ScreenHelper.addAlphaToColor(0x000000, (int)(128 * infoAlpha)));

			int y = 5;
			for (Component t : text) {
				int textWidth = font.width(t);
				int textX = width / 2 - (textWidth / 2);
				context.text(font, t, textX, y, ScreenHelper.addAlphaToColor(0xFFFFFF, (int)(255 * infoAlpha)), true);
				y += 15;
			}
		}

		for (Identifier element : elements) {

			HudPlacement placement = configManager.getHudPlacement(element);
			HudConfigManager.Rect rect = configManager.getUsedRectOfElement(element);
			if (!placement.visible()) {
				context.fill(rect.x(), rect.y(), rect.endX(), rect.endY(), 0x44FF0000);
			} else if (placement.transparency() < 0.1) {
				context.fill(rect.x(), rect.y(), rect.endX(), rect.endY(), 0x1100FFFF);
			}

			if (selectedElement == element) {
				context.outline(rect.x() - 3, rect.y() - 3, rect.width() + 6, rect.height() + 6, ScreenHelper.addAlphaToColor(CommonColors.YELLOW, 200));

				int showButtonX = rect.endX() - (BUTTON_WIDTH * 2 + 2);
				int scaleButtonX = rect.endX() - (BUTTON_WIDTH * 3 + 4);
				int resetButtonX = rect.endX() - BUTTON_WIDTH;
				int buttonY = height - rect.endY() > BUTTON_HEIGHT + 4 ? rect.endY() + 4 : rect.y() - BUTTON_HEIGHT - 4;

				if (isMouseOverShowButton(mouseX, mouseY) && configManager.getHudPlacement(element).visible()) {
					context.setTooltipForNextFrame(Minecraft.getInstance().font, Component.nullToEmpty("Hide"), mouseX, mouseY);
					context.blitSprite(RenderPipelines.GUI_TEXTURED, HIDE_BUTTON_HIGHLIGHT, showButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
					context.requestCursor(CursorTypes.POINTING_HAND);
				} else if (isMouseOverShowButton(mouseX, mouseY)) {
					context.setTooltipForNextFrame(Minecraft.getInstance().font, Component.nullToEmpty("Show"), mouseX, mouseY);
					context.blitSprite(RenderPipelines.GUI_TEXTURED, SHOW_BUTTON_HIGHLIGHT, showButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
					context.requestCursor(CursorTypes.POINTING_HAND);
				} else if (configManager.getHudPlacement(element).visible()) {
					context.blitSprite(RenderPipelines.GUI_TEXTURED, HIDE_BUTTON, showButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				} else {
					context.blitSprite(RenderPipelines.GUI_TEXTURED, SHOW_BUTTON, showButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				}

				if (isMouseOverResetButton(mouseX, mouseY)) {
					context.setTooltipForNextFrame(Minecraft.getInstance().font, Component.nullToEmpty("Reset"), mouseX, mouseY);
					context.blitSprite(RenderPipelines.GUI_TEXTURED, RESET_BUTTON_HIGHLIGHT, resetButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
					context.requestCursor(CursorTypes.POINTING_HAND);
				} else {
					context.blitSprite(RenderPipelines.GUI_TEXTURED, RESET_BUTTON, resetButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				}

				if (isMouseOverScaleButton(mouseX, mouseY)) {
					context.setTooltipForNextFrame(Minecraft.getInstance().font, Component.nullToEmpty("Change size"), mouseX, mouseY);
					context.blitSprite(RenderPipelines.GUI_TEXTURED, SCALE_BUTTON_HIGHLIGHT, scaleButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
					context.requestCursor(CursorTypes.POINTING_HAND);
				} else {
					context.blitSprite(RenderPipelines.GUI_TEXTURED, SCALE_BUTTON, scaleButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				}

				int sliderButtonX = rect.endX() - (BUTTON_WIDTH * 5 + 8);
				drawSlider(context, sliderButtonX, buttonY, placement.transparency(), mouseX, mouseY);

				if (hoveringElement == element) {
					context.requestCursor(CursorTypes.RESIZE_ALL);
				}

			} else if (hoveringElement == element) {
				context.fill(rect.x() - 2, rect.y() - 2, rect.endX() + 2, rect.endY() + 2, 0x44DADADA);
			}
		}

		if (dragging && selectedElement != null) {
			HudConfigManager.Rect dragRect = configManager.getUsedRectOfElement(selectedElement);
			int offsetX = (int)(mouseX - clickOffsetX);
			int offsetY = (int)(mouseY - clickOffsetY);
			context.fill(offsetX - 2, offsetY - 2, offsetX + dragRect.width() + 2, offsetY + dragRect.height() + 2, 0x44DADADA);
			context.requestCursor(CursorTypes.RESIZE_ALL);
		}
	}

	private void drawSlider(GuiGraphicsExtractor context, int x, int y, double value, int mouseX, int mouseY) {
		Identifier sliderTexture = SLIDER_BUTTON_SLIDER;
		if (isMouseOverTransparencySlider(mouseX, mouseY)) {
			context.setTooltipForNextFrame(Minecraft.getInstance().font, Component.nullToEmpty("Transparency"), mouseX, mouseY);
			sliderTexture = SLIDER_BUTTON_SLIDER_HIGHLIGHT;
			if (draggingSlider) {
				context.requestCursor(CursorTypes.RESIZE_EW);
			} else {
				context.requestCursor(CursorTypes.POINTING_HAND);
			}
		}

		int range = SLIDER_BACKGROUND_WIDTH - SLIDER_WIDTH;

		int progressStartX = (int)(range * (1.0 - value));
		int progressSizeX = range - (progressStartX + SLIDER_WIDTH / 2);

		context.blitSprite(RenderPipelines.GUI_TEXTURED, SLIDER_BUTTON_BACKGROUND, x, y, SLIDER_BACKGROUND_WIDTH, BUTTON_HEIGHT);
		context.blitSprite(RenderPipelines.GUI_TEXTURED, SLIDER_BUTTON_PROGRESS, SLIDER_BACKGROUND_WIDTH, BUTTON_HEIGHT, progressStartX + SLIDER_WIDTH / 2, 0, x + progressStartX + SLIDER_WIDTH / 2, y, progressSizeX + SLIDER_WIDTH, BUTTON_HEIGHT);
		context.blitSprite(RenderPipelines.GUI_TEXTURED, sliderTexture, x + progressStartX, y, SLIDER_WIDTH, BUTTON_HEIGHT);
	}

	private boolean isMouseOverElement(Identifier element, double mouseX, double mouseY) {
		HudConfigManager.Rect rect = configManager.getUsedRectOfElement(element);
		return ScreenHelper.isPointWithinBounds(rect.x(), rect.y(), rect.width(), rect.height(), mouseX, mouseY);
	}

	private boolean isMouseOverScaleButton(double mouseX, double mouseY) {
		if (selectedElement == null) {
			return false;
		}
		HudConfigManager.Rect usedRect = configManager.getUsedRectOfElement(selectedElement);

		int buttonX = usedRect.endX() - (BUTTON_WIDTH * 3 + 4);
		int buttonY = height - usedRect.endY() > BUTTON_HEIGHT + 4 ? usedRect.endY() + 4 : usedRect.y() - BUTTON_HEIGHT - 4;
		return ScreenHelper.isPointWithinBounds(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY);
	}

	private boolean isMouseOverShowButton(double mouseX, double mouseY) {
		if (selectedElement == null) {
			return false;
		}
		HudConfigManager.Rect usedRect = configManager.getUsedRectOfElement(selectedElement);

		int buttonX = usedRect.endX() - (BUTTON_WIDTH * 2 + 2);
		int buttonY = height - usedRect.endY() > BUTTON_HEIGHT + 4 ? usedRect.endY() + 4 : usedRect.y() - BUTTON_HEIGHT - 4;
		return ScreenHelper.isPointWithinBounds(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY);
	}

	private boolean isMouseOverResetButton(double mouseX, double mouseY) {
		if (selectedElement == null) {
			return false;
		}
		HudConfigManager.Rect usedRect = configManager.getUsedRectOfElement(selectedElement);

		int buttonX = usedRect.endX() - BUTTON_WIDTH;
		int buttonY = height - usedRect.endY() > BUTTON_HEIGHT + 4 ? usedRect.endY() + 4 : usedRect.y() - BUTTON_HEIGHT - 4;
		return ScreenHelper.isPointWithinBounds(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, mouseX, mouseY);
	}

	private boolean isMouseOverTransparencySlider(double mouseX, double mouseY) {
		if (selectedElement == null) {
			return false;
		}
		HudConfigManager.Rect usedRect = configManager.getUsedRectOfElement(selectedElement);

		int buttonX = usedRect.endX() - (BUTTON_WIDTH * 5 + 8);
		int buttonY = height - usedRect.endY() > BUTTON_HEIGHT + 4 ? usedRect.endY() + 4 : usedRect.y() - BUTTON_HEIGHT - 4;
		return ScreenHelper.isPointWithinBounds(buttonX, buttonY, SLIDER_BACKGROUND_WIDTH, BUTTON_HEIGHT, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		if (doubled) return true;

		int button = click.button();
		double mouseX = click.x();
		double mouseY = click.y();

		if (button != InputConstants.MOUSE_BUTTON_LEFT) {
			return super.mouseClicked(click, doubled);
		}

		boolean success = false;

		if (isMouseOverShowButton(mouseX, mouseY)) {
			configManager.toggleElementVisible(selectedElement);
			success = true;
		} else if (isMouseOverResetButton(mouseX, mouseY)) {
			configManager.resetElement(selectedElement);
			success = true;
		} else if (isMouseOverScaleButton(mouseX, mouseY)) {
			int scale = (int) configManager.getHudPlacement(selectedElement).scaleX();
			scale = 1 + ((scale - 1) + 1) % 4;
			configManager.setElementScale(selectedElement, scale, scale);
			success = true;
		} else if (isMouseOverTransparencySlider(mouseX, mouseY)) {
			draggingSlider = true;
			int range = SLIDER_BACKGROUND_WIDTH - SLIDER_WIDTH;
			HudConfigManager.Rect usedRect = configManager.getUsedRectOfElement(selectedElement);

			int buttonX = usedRect.endX() - (BUTTON_WIDTH * 5 + 8);

			int targetX = (int) mouseX - buttonX;

			int pixelValue = Math.clamp(targetX, 5, range + 5);
			configManager.setElementTransparency(selectedElement, 1.0 - (double) (pixelValue - 5) / (double) range);
			success = true;
		}

		if (success) {
			playButtonSound();
			return true;
		}

		for (Identifier element : elements) {
			if (isMouseOverElement(element, mouseX, mouseY)) {
				if (selectedElement == element) {
					dragging = true;
					HudConfigManager.Rect selectedRect = configManager.getUsedRectOfElement(element);
					clickOffsetX = mouseX - selectedRect.x();
					clickOffsetY = mouseY - selectedRect.y();
				}
				else {
					selectedElement = element;
				}
				return true;
			}
		}

		selectedElement = null;
		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent click) {

		int button = click.button();
		double mouseX = click.x();
		double mouseY = click.y();

		if (selectedElement != null && dragging) {
			configManager.moveElement(selectedElement, (int)(mouseX - clickOffsetX), (int)(mouseY - clickOffsetY), width, height);

			dragging = false;
			clickOffsetX = 0;
			clickOffsetY = 0;
			return true;
		}
		if (selectedElement != null && draggingSlider) {
			draggingSlider = false;
		}

		return super.mouseReleased(click);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if (isMouseOverTransparencySlider(mouseX, mouseY) && draggingSlider) {
			int range = SLIDER_BACKGROUND_WIDTH - SLIDER_WIDTH;
			HudConfigManager.Rect usedRect = configManager.getUsedRectOfElement(selectedElement);

			int buttonX = usedRect.endX() - (BUTTON_WIDTH * 5 + 8);

			int targetX = (int) mouseX - buttonX;

			int pixelValue = Math.clamp(targetX, 5, range + 5);
			configManager.setElementTransparency(selectedElement, 1.0 - (double) (pixelValue - 5) / (double) range);
			return;
		}

		for (Identifier element : elements) {
			if (isMouseOverElement(element, mouseX, mouseY)) {
				hoveringElement = element;
				return;
			}
		}

		if (hoveringElement != null) {
			hoveringElement = null;
		}
	}


	@Override
	public boolean keyPressed(KeyEvent key) {
		int keyCode = key.key();

		if (keyCode == InputConstants.KEY_ESCAPE) {
			closeScreen();
			return true;
		}

		if (selectedElement == null) {
			return super.keyPressed(key);
		}

		if (keyCode == InputConstants.KEY_W || keyCode == InputConstants.KEY_UP) {
			HudConfigManager.Rect rect = configManager.getUsedRectOfElement(selectedElement);
			configManager.moveElement(selectedElement, rect.x(), rect.y() - 1, width, height);
		} else if (keyCode == InputConstants.KEY_S || keyCode == InputConstants.KEY_DOWN) {
			HudConfigManager.Rect rect = configManager.getUsedRectOfElement(selectedElement);
			configManager.moveElement(selectedElement, rect.x(), rect.y() + 1, width, height);
		} else if (keyCode == InputConstants.KEY_A || keyCode == InputConstants.KEY_LEFT) {
			HudConfigManager.Rect rect = configManager.getUsedRectOfElement(selectedElement);
			configManager.moveElement(selectedElement, rect.x() - 1, rect.y(), width, height);
		} else if (keyCode == InputConstants.KEY_D || keyCode == InputConstants.KEY_RIGHT) {
			HudConfigManager.Rect rect = configManager.getUsedRectOfElement(selectedElement);
			configManager.moveElement(selectedElement, rect.x() + 1, rect.y(), width, height);
		}

		return super.keyPressed(key);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	protected void closeScreen() {
		if (configManager.hasChanged()) {

			minecraft.setScreenAndShow(new DiscardConfirmScreen(new BingoConfigScreen(modMenuScreen, configManager), modMenuScreen, configManager::load));
			return;
		}

		configManager.load();
		minecraft.setScreenAndShow(modMenuScreen);

		BingoReloadedCompanionClient.sendPayloadToServer(new ClientHelloPayload(new ClientSettings(
				configManager.getBooleanOption(BingoReloadedCompanionClient.CREATOR_USE_CLIENT_CREATOR))
		));
	}

	private void playButtonSound() {
		minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
	}
}
