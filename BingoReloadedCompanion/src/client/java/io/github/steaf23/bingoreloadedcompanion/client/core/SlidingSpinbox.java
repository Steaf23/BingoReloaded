package io.github.steaf23.bingoreloadedcompanion.client.core;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;

import java.util.function.Consumer;

public class SlidingSpinbox extends AbstractWidget implements DeferredExtraction {

	private final Identifier TOOLTIP_BACKGROUND = Identifier.withDefaultNamespace("tooltip/background");
	private final Identifier TOOLTIP_FRAME = Identifier.withDefaultNamespace("tooltip/frame");

	private final CustomSliderWidget slider = new CustomSliderWidget(0, 0, 80, false);
	private final Font font;

	private final int min;
	private final int max;
	private final Consumer<Integer> valueChanged;

	private int value;
	private boolean dragging = false;

	public SlidingSpinbox(int min, int max, Font font, Consumer<Integer> valueChanged) {
		super(0, 0, 28, 20, Component.empty());
		this.font = font;
		this.min = min;
		this.max = max;
		this.valueChanged = valueChanged;
		value = min;
		slider.visible = false;
	}

	public SlidingSpinbox withValue(int value) {
		this.value = Math.clamp(value, min, max);
		return this;
	}

	public int valueFromSliderPos(double sliderValue) {
		double exponent = 1.5;

		double value = 1.0 + Math.pow(sliderValue, exponent) * (max - 1);
		return (int)Math.round(value);
	}

	public double getSliderPercentageFromValue(int value) {
		double exponent = 1.5;
		return Math.pow(
				(value - 1.0) / (max - 1.0),
				1.0 / exponent
		);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {

		if (doubleClick) {
			return super.mouseClicked(event,true);
		}

		if (isMouseOver(event.x(), event.y())) {
			double percentage = getSliderPercentageFromValue(value);
			slider.show(percentage, (int)event.x());
			dragging = true;
		}

		return super.mouseClicked(event, false);
	}

	@Override
	public void onRelease(MouseButtonEvent event) {
		slider.visible = false;
		if (dragging) {
			dragging = false;
			value = valueFromSliderPos(slider.currentValue());
			valueChanged.accept(value);
		}
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		return super.mouseDragged(event, dx, dy);
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (!slider.visible) {
			int bgWidth = getWidth() + 14;
			int bgHeight = getHeight() + 14;
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TOOLTIP_BACKGROUND, getX() - 7, getY() - 7, bgWidth, bgHeight);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TOOLTIP_FRAME, getX() - 7, getY() - 7, bgWidth, bgHeight);

			if (isMouseOver(mouseX, mouseY)) {
				graphics.requestCursor(CursorTypes.POINTING_HAND);
			}
			String valueStr = String.valueOf(value);
			int centerX = (getX() + getWidth() / 2) - font.width(valueStr) / 2;
			int centerY = (getY() + getHeight() / 2) - font.lineHeight / 2;
			graphics.text(font, valueStr, centerX, centerY, CommonColors.WHITE);
		}
	}

	@Override
	public void extractAtTheEnd(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (slider.visible) {
			int bgWidth = getWidth() + 14;
			int bgHeight = getHeight() + 14;
			int bgX = slider.sliderX() - getWidth() / 2;
			int bgY = getY() - getHeight() + 10;
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TOOLTIP_BACKGROUND, bgX - 7, bgY - 7, bgWidth, bgHeight - 4);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TOOLTIP_FRAME, bgX - 7, bgY - 7, bgWidth, bgHeight - 4);

			int value = valueFromSliderPos(slider.currentValue());
			String valueText = String.valueOf(value);
			int textStartY = slider.getY() - font.lineHeight;
			int textStartX = slider.sliderX() - font.width(valueText) / 2;
			graphics.text(font, valueText, textStartX, textStartY, CommonColors.WHITE);
			slider.extractRenderState(graphics, mouseX, mouseY, a);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}

	@Override
	public void setX(int x) {
		int diff = Math.abs(slider.getWidth() - getWidth()) / 2;
		slider.setX(x - diff);
		super.setX(x);
	}

	@Override
	public void setY(int y) {
		int diff = Math.abs(slider.getHeight() - getHeight()) / 2;
		slider.setY(y + 4);
		super.setY(y);
	}
}
