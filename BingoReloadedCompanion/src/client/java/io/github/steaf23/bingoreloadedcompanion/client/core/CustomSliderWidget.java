package io.github.steaf23.bingoreloadedcompanion.client.core;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CustomSliderWidget extends AbstractWidget {

	private static final Identifier SLIDER_BUTTON_SLIDER = Identifier.parse("bingoreloadedcompanion:slider_button_slider");
	private static final Identifier SLIDER_BUTTON_SLIDER_HIGHLIGHT = Identifier.parse("bingoreloadedcompanion:slider_button_slider_highlighted");
	private static final Identifier SLIDER_BUTTON_BACKGROUND = Identifier.parse("bingoreloadedcompanion:slider_button_background");
	private static final Identifier SLIDER_BUTTON_PROGRESS = Identifier.parse("bingoreloadedcompanion:slider_button_progress");

	private static final int SLIDER_WIDTH = 10;
	private static final int SLIDER_HEIGHT = 14;

	private final boolean highlightOnHover;

	private int leftX = 0;

	public CustomSliderWidget(int x, int y, int width, boolean highlightOnHover) {
		super(x, y, Math.max(width, SLIDER_WIDTH), SLIDER_HEIGHT, Component.empty());
		this.highlightOnHover = highlightOnHover;

		visible = false;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		double valueRaw = 1.0 - (double) (mouseX - leftX) / getWidth();
		double percentage = Math.clamp(valueRaw, 0.0, 1.0);
		extractSlider(graphics, getX(), getY(), percentage, mouseX, mouseY);

		if (isMouseOver(mouseX, mouseY)) {
			graphics.requestCursor(CursorTypes.RESIZE_EW);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}

	private void extractSlider(GuiGraphicsExtractor graphics, int x, int y, double value, int mouseX, int mouseY) {
		Identifier sliderTexture = SLIDER_BUTTON_SLIDER;
		if (isMouseOver(mouseX, mouseY) && highlightOnHover) {
			graphics.setTooltipForNextFrame(Minecraft.getInstance().font, Component.nullToEmpty("Transparency"), mouseX, mouseY);
			sliderTexture = SLIDER_BUTTON_SLIDER_HIGHLIGHT;
		}

		int range = getWidth() - SLIDER_WIDTH;

		int progressStartX = (int) (range * (1.0 - value));
		int progressSizeX = range - (progressStartX + SLIDER_WIDTH / 2);

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLIDER_BUTTON_BACKGROUND, x, y, getWidth(), SLIDER_HEIGHT);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLIDER_BUTTON_PROGRESS, getWidth(), SLIDER_HEIGHT, progressStartX + SLIDER_WIDTH / 2, 0, x + progressStartX + SLIDER_WIDTH / 2, y, progressSizeX + SLIDER_WIDTH, SLIDER_HEIGHT);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sliderTexture, x + progressStartX, y, SLIDER_WIDTH, SLIDER_HEIGHT);
	}

	public void show(int centerX, int centerY) {
		leftX = centerX - getWidth() / 2;

		setY(centerY);
		setX(leftX);
		visible = true;
	}

	@Override
	public void onRelease(MouseButtonEvent event) {
		leftX = 0;
		visible = false;
	}

	public double currentValue() {
		if (!visible) {
			return 0;
		} else {
			return pixelValue() / (double)getWidth();
		}
	}

	public int pixelValue() {
		if (!visible) {
			return 0;
		} else {
			return leftX;
		}
	}
}
