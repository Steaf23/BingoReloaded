package io.github.steaf23.bingoreloadedcompanion.client.core;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

public class LayoutBackground extends AbstractWidget {

	private final Layout layout;
	private final BiConsumer<GuiGraphicsExtractor, ScreenRectangle> extractBackground;

	public LayoutBackground(Layout layout, BiConsumer<GuiGraphicsExtractor, ScreenRectangle> extractBackground) {
		super(0, 0, 0, 0, Component.empty());
		this.layout = layout;
		this.extractBackground = extractBackground;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		extractBackground.accept(graphics, layout.getRectangle());
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}
}
