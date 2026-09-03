package io.github.steaf23.bingoreloadedcompanion.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class CustomCheckbox extends AbstractButton {

	private final Identifier defaultSprite;
	private final Identifier selectedSprite;
	private boolean selected;
	private final CustomCheckbox.OnValueChange onValueChange;
	private final MultiLineTextWidget textWidget;

	public CustomCheckbox(
			final int maxWidth,
			final Component message,
			final Font font,
			final boolean selected,
			Identifier defaultSprite,
			Identifier selectedSprite,
			final CustomCheckbox.OnValueChange onValueChange
	) {
		super(0, 0, 0, 0, message);
		this.textWidget = new MultiLineTextWidget(message, font);
		this.textWidget.setMaxRows(2);
		this.width = this.adjustWidth(maxWidth, font);
		this.height = this.getAdjustedHeight(font);
		this.selected = selected;
		this.onValueChange = onValueChange;
		this.defaultSprite = defaultSprite;
		this.selectedSprite = selectedSprite;
	}

	public int adjustWidth(final int maxWidth, final Font font) {
		this.width = this.getAdjustedWidth(maxWidth, this.getMessage(), font);
		this.textWidget.setMaxWidth(this.width);
		return this.width;
	}

	private int getAdjustedWidth(final int maxWidth, final Component message, final Font font) {
		return Math.min(getDefaultWidth(message, font), maxWidth);
	}

	private int getAdjustedHeight(final Font font) {
		return Math.max(getBoxSize(font), this.textWidget.getHeight());
	}

	public static int getDefaultWidth(final Component message, final Font font) {
		return getBoxSize(font) + 4 + font.width(message);
	}

	private boolean overflowsRowLimit(final Font font) {
		return font.getSplitter().splitLines(this.textWidget.getMessage(), this.width, Style.EMPTY).size() > 2;
	}

	public static int getBoxSize(final Font font) {
		return 12;
	}

	@Override
	public void onPress(final InputWithModifiers input) {
		this.selected = !this.selected;
		this.onValueChange.onValueChange(this, this.selected);
	}

	public boolean selected() {
		return this.selected;
	}

	public void updateWidgetNarration(final NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, this.createNarrationMessage());
		if (this.active) {
			if (this.isFocused()) {
				output.add(
						NarratedElementType.USAGE,
						Component.translatable(this.selected ? "narration.checkbox.usage.focused.uncheck" : "narration.checkbox.usage.focused.check")
				);
			} else {
				output.add(
						NarratedElementType.USAGE,
						Component.translatable(this.selected ? "narration.checkbox.usage.hovered.uncheck" : "narration.checkbox.usage.hovered.check")
				);
			}
		}
	}

	@Override
	public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		Identifier sprite;
		if (this.selected) {
			sprite = selectedSprite;
		} else {
			sprite = defaultSprite;
		}

		int boxSize = getBoxSize(font);
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.getX(), this.getY(), boxSize, boxSize, ARGB.white(this.alpha));
		int textX = this.getX() + boxSize + 4;
		int textY = this.getY() + boxSize / 2 - this.textWidget.getHeight() / 2;
		this.textWidget.setPosition(textX, textY);
		this.textWidget.visitLines(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.notClickable(this.isHovered())));
	}

	@FunctionalInterface
	public interface OnValueChange {
		void onValueChange(CustomCheckbox checkbox, boolean newValue);
	}
}
