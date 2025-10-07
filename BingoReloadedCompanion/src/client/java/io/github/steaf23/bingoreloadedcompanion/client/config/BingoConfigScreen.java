package io.github.steaf23.bingoreloadedcompanion.client.config;

import io.github.steaf23.bingoreloadedcompanion.card.BingoCard;
import io.github.steaf23.bingoreloadedcompanion.card.BingoGamemode;
import io.github.steaf23.bingoreloadedcompanion.card.Task;
import io.github.steaf23.bingoreloadedcompanion.client.BingoReloadedCompanionClient;
import io.github.steaf23.bingoreloadedcompanion.client.hud.BingoCardHudElement;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudConfigManager;
import io.github.steaf23.bingoreloadedcompanion.client.hud.HudPlacement;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BingoConfigScreen extends Screen {

	private final Screen modMenuScreen;
	private final HudConfigManager configManager;
	private final BingoCardHudElement previewCard;

	private static final Identifier HIDE_BUTTON = Identifier.of("bingoreloadedcompanion:hide_button");
	private static final Identifier HIDE_BUTTON_HIGHLIGHT = Identifier.of("bingoreloadedcompanion:hide_button_highlighted");
	private static final Identifier SHOW_BUTTON = Identifier.of("bingoreloadedcompanion:show_button");
	private static final Identifier SHOW_BUTTON_HIGHLIGHT = Identifier.of("bingoreloadedcompanion:show_button_highlighted");
	private static final Identifier RESET_BUTTON = Identifier.of("bingoreloadedcompanion:reset_button");
	private static final Identifier RESET_BUTTON_HIGHLIGHT = Identifier.of("bingoreloadedcompanion:reset_button_highlighted");
	private static final Identifier SCALE_BUTTON = Identifier.of("bingoreloadedcompanion:scale_button");
	private static final Identifier SCALE_BUTTON_HIGHLIGHT = Identifier.of("bingoreloadedcompanion:scale_button_highlighted");
	private static final Identifier SLIDER_BUTTON_SLIDER = Identifier.of("bingoreloadedcompanion:slider_button_slider");
	private static final Identifier SLIDER_BUTTON_SLIDER_HIGHLIGHT = Identifier.of("bingoreloadedcompanion:slider_button_slider_highlighted");
	private static final Identifier SLIDER_BUTTON_BACKGROUND = Identifier.of("bingoreloadedcompanion:slider_button_background");
	private static final Identifier SLIDER_BUTTON_PROGRESS = Identifier.of("bingoreloadedcompanion:slider_button_progress");
	private static final int BUTTON_WIDTH = 14;
	private static final int SLIDER_WIDTH = 10;
	private static final int SLIDER_BACKGROUND_WIDTH = BUTTON_WIDTH * 2 + 2;
	private static final int BUTTON_HEIGHT = 14;


	private static final long MOVE_CURSOR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR);

	private Identifier selectedElement;
	private Identifier hoveringElement;

	private boolean dragging = false;
	private boolean draggingSlider = false;
	private double clickOffsetX;
	private double clickOffsetY;

	private final List<Identifier> elements = List.of(BingoReloadedCompanionClient.BINGO_CARD_TASKS, BingoReloadedCompanionClient.BINGO_CARD_GAMEMODE);

	protected BingoConfigScreen(Screen modMenuScreen, HudConfigManager hudConfig) {
		super(Text.of("Bingo Reloaded Options"));
		this.modMenuScreen = modMenuScreen;

		this.configManager = hudConfig;
		this.previewCard = new BingoCardHudElement(this.configManager);

		List<Task> testTasks = new ArrayList<>();
		for (int i = 0; i < 25; i++) {
			testTasks.add(new Task(Task.TaskCompletion.INCOMPLETE, Identifier.of("bingoreloaded:item"), Items.PAPER, 1));
		}
		BingoCard testCard5x = new BingoCard(BingoGamemode.REGULAR, 5, testTasks);
		previewCard.setCard(testCard5x);
	}

	@Override
	protected void init() {

		ButtonWidget backButton = ButtonWidget.builder(Text.of("Save & Exit"), (btn) -> {
			if (client == null) return;
			configManager.save();
			client.setScreen(modMenuScreen);
		})
				.position(5, height - 20 - 5)
				.build();

		addDrawableChild(backButton);

		int buttonWidth = Math.max(150, textRenderer.getWidth(Text.of("Reset all elements")) + 10);

		ButtonWidget resetButton = ButtonWidget.builder(Text.of("Reset all elements"), (btn) -> {
					configManager.resetAllElements();
				})
				.position(width - buttonWidth - 5, height - 20 - 5)
				.build();

		addDrawableChild(resetButton);
	}

	@Override
	public void removed() {
		configManager.load();
		GLFW.glfwSetCursor(getGameWindowId(), 0);
		super.removed();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);

		previewCard.renderElement(context, deltaTicks);

		{ // Draw the info tooltip at the top
			double infoAlpha = mouseY < 40 ? 0.5 : 1.0;

			List<Text> text = List.of(
					Text.of("Click on an element to select it."),
					Text.of("You can move it by dragging the mouse or using the movement keys.")
			);
			int recordWidth = 0;
			for (Text t : text) {
				int textWidth = textRenderer.getWidth(t);
				if (textWidth > recordWidth) {
					recordWidth = textWidth;
				}
			}

			int textHeight = text.size() * 15 + 5;
			int backgroundWidth = recordWidth + 10;
			int backgroundStartX = width / 2 - (backgroundWidth / 2);
			context.fill(backgroundStartX, 0, backgroundStartX + backgroundWidth, textHeight, ScreenHelper.addAlphaToColor(0x000000, (int)(128 * infoAlpha)));

			int y = 5;
			for (Text t : text) {
				int textWidth = textRenderer.getWidth(t);
				int textX = width / 2 - (textWidth / 2);
				context.drawText(textRenderer, t, textX, y, ScreenHelper.addAlphaToColor(Formatting.WHITE.getColorValue(), (int)(255 * infoAlpha)), true);
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
				context.drawBorder(rect.x() - 3, rect.y() - 3, rect.width() + 6, rect.height() + 6, ScreenHelper.addAlphaToColor(Formatting.YELLOW.getColorValue(), 200));

				int showButtonX = rect.endX() - (BUTTON_WIDTH * 2 + 2);
				int scaleButtonX = rect.endX() - (BUTTON_WIDTH * 3 + 4);
				int resetButtonX = rect.endX() - BUTTON_WIDTH;
				int buttonY = height - rect.endY() > BUTTON_HEIGHT + 4 ? rect.endY() + 4 : rect.y() - BUTTON_HEIGHT - 4;

				if (isMouseOverShowButton(mouseX, mouseY) && configManager.getHudPlacement(element).visible()) {
					context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of("Hide"), mouseX, mouseY);
					context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HIDE_BUTTON_HIGHLIGHT, showButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				} else if (isMouseOverShowButton(mouseX, mouseY)) {
					context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of("Show"), mouseX, mouseY);
					context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SHOW_BUTTON_HIGHLIGHT, showButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				} else if (configManager.getHudPlacement(element).visible()) {
					context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HIDE_BUTTON, showButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				} else {
					context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SHOW_BUTTON, showButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				}

				if (isMouseOverResetButton(mouseX, mouseY)) {
					context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of("Reset"), mouseX, mouseY);
					context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, RESET_BUTTON_HIGHLIGHT, resetButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				} else {
					context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, RESET_BUTTON, resetButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				}

				if (isMouseOverScaleButton(mouseX, mouseY)) {
					context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of("Change size"), mouseX, mouseY);
					context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCALE_BUTTON_HIGHLIGHT, scaleButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				} else {
					context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCALE_BUTTON, scaleButtonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
				}

				int sliderButtonX = rect.endX() - (BUTTON_WIDTH * 5 + 8);
				drawSlider(context, sliderButtonX, buttonY, placement.transparency(), mouseX, mouseY);

			} else if (hoveringElement == element) {
				context.fill(rect.x() - 2, rect.y() - 2, rect.endX() + 2, rect.endY() + 2, 0x44DADADA);
			}
		}

		if (dragging && selectedElement != null) {
			HudConfigManager.Rect dragRect = configManager.getUsedRectOfElement(selectedElement);
			int offsetX = (int)(mouseX - clickOffsetX);
			int offsetY = (int)(mouseY - clickOffsetY);
			context.fill(offsetX - 2, offsetY - 2, offsetX + dragRect.width() + 2, offsetY + dragRect.height() + 2, 0x44DADADA);
		}
	}

	private void drawSlider(DrawContext context, int x, int y, double value, int mouseX, int mouseY) {
		Identifier sliderTexture = SLIDER_BUTTON_SLIDER;
		if (isMouseOverTransparencySlider(mouseX, mouseY)) {
			context.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.of("Transparency"), mouseX, mouseY);
			sliderTexture = SLIDER_BUTTON_SLIDER_HIGHLIGHT;
		}

		int range = SLIDER_BACKGROUND_WIDTH - SLIDER_WIDTH;

		int progressStartX = (int)(range * (1.0 - value));
		int progressSizeX = range - (progressStartX + SLIDER_WIDTH / 2);

		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLIDER_BUTTON_BACKGROUND, x, y, SLIDER_BACKGROUND_WIDTH, BUTTON_HEIGHT);
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLIDER_BUTTON_PROGRESS, SLIDER_BACKGROUND_WIDTH, BUTTON_HEIGHT, progressStartX + SLIDER_WIDTH / 2, 0, x + progressStartX + SLIDER_WIDTH / 2, y, progressSizeX + SLIDER_WIDTH, BUTTON_HEIGHT);
		context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sliderTexture, x + progressStartX, y, SLIDER_WIDTH, BUTTON_HEIGHT);
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

	private long getGameWindowId() {
		return client.getWindow().getHandle();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			return super.mouseClicked(mouseX, mouseY, button);
		}

		if (isMouseOverShowButton(mouseX, mouseY)) {
			configManager.toggleElementVisible(selectedElement);
			return true;
		} else if (isMouseOverResetButton(mouseX, mouseY)) {
			configManager.resetElement(selectedElement);
			return true;
		} else if (isMouseOverScaleButton(mouseX, mouseY)) {
			int scale = (int) configManager.getHudPlacement(selectedElement).scaleX();
			scale = 1 + ((scale - 1) + 1) % 4;
			configManager.setElementScale(selectedElement, scale, scale);
			return true;
		} else if (isMouseOverTransparencySlider(mouseX, mouseY)) {
			draggingSlider = true;
			int range = SLIDER_BACKGROUND_WIDTH - SLIDER_WIDTH;
			HudConfigManager.Rect usedRect = configManager.getUsedRectOfElement(selectedElement);

			int buttonX = usedRect.endX() - (BUTTON_WIDTH * 5 + 8);

			int targetX = (int) mouseX - buttonX;

			int pixelValue = Math.clamp(targetX, 5, range + 5);
			configManager.setElementTransparency(selectedElement, 1.0 - (double) (pixelValue - 5) / (double) range);
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
				else if (selectedElement != element) {
					selectedElement = element;
					GLFW.glfwSetCursor(getGameWindowId(), MOVE_CURSOR);
				}
				return true;
			}
		}

		selectedElement = null;
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
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

		return super.mouseReleased(mouseX, mouseY, button);
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
				if (element == selectedElement) {
					GLFW.glfwSetCursor(getGameWindowId(), MOVE_CURSOR);
				}

				hoveringElement = element;

				if (hoveringElement != selectedElement) {
					GLFW.glfwSetCursor(getGameWindowId(), 0);
				}
				return;
			}
		}

		if (hoveringElement != null) {
			hoveringElement = null;
			GLFW.glfwSetCursor(getGameWindowId(), 0);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (selectedElement == null) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		if (keyCode == GLFW.GLFW_KEY_W || keyCode == GLFW.GLFW_KEY_UP) {
			HudConfigManager.Rect rect = configManager.getUsedRectOfElement(selectedElement);
			configManager.moveElement(selectedElement, rect.x(), rect.y() - 1, width, height);
		} else if (keyCode == GLFW.GLFW_KEY_S || keyCode == GLFW.GLFW_KEY_DOWN) {
			HudConfigManager.Rect rect = configManager.getUsedRectOfElement(selectedElement);
			configManager.moveElement(selectedElement, rect.x(), rect.y() + 1, width, height);
		} else if (keyCode == GLFW.GLFW_KEY_A || keyCode == GLFW.GLFW_KEY_LEFT) {
			HudConfigManager.Rect rect = configManager.getUsedRectOfElement(selectedElement);
			configManager.moveElement(selectedElement, rect.x() - 1, rect.y(), width, height);
		} else if (keyCode == GLFW.GLFW_KEY_D || keyCode == GLFW.GLFW_KEY_RIGHT) {
			HudConfigManager.Rect rect = configManager.getUsedRectOfElement(selectedElement);
			configManager.moveElement(selectedElement, rect.x() + 1, rect.y(), width, height);
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
