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

	private int dragStartX = 0;
	private double dragStartValue = 0.0;

	private double currentValue = 0.0;

	public CustomSliderWidget(int x, int y, int width, boolean highlightOnHover) {
		super(x, y, Math.max(width, SLIDER_WIDTH), SLIDER_HEIGHT, Component.empty());
		this.highlightOnHover = highlightOnHover;

		visible = false;
	}

	public int sliderX() {
		int range = getWidth() - SLIDER_WIDTH / 2;

		int progressStartX = (int) (range * currentValue);

		return getX() + progressStartX;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		int mouseDelta = mouseX - dragStartX;
		double valueDiff = mouseDelta / (double)getWidth();

		double percentage = Math.clamp(dragStartValue + valueDiff, 0.0, 1.0);
		currentValue = percentage;
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

		int progressStartX = (int) (range * value);

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLIDER_BUTTON_BACKGROUND, x, y, getWidth(), SLIDER_HEIGHT);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLIDER_BUTTON_PROGRESS, getWidth(), SLIDER_HEIGHT,
				0, 0,
				x, y, progressStartX + SLIDER_WIDTH / 2, SLIDER_HEIGHT);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sliderTexture, sliderX() - SLIDER_WIDTH / 2, y, SLIDER_WIDTH, SLIDER_HEIGHT);
	}

	public void show(double valuePercent, int mouseX) {
		dragStartX = mouseX;
		dragStartValue = valuePercent;
		currentValue = dragStartValue;
		visible = true;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		return false;
	}

	public double currentValue() {
		return currentValue;
	}
}
