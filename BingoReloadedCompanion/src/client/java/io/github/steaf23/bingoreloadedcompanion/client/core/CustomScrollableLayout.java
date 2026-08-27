package io.github.steaf23.bingoreloadedcompanion.client.core;

import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class CustomScrollableLayout extends AbstractScrollArea implements Layout {

	private final Layout innerLayout;

	private final int scrollRate;
	private final int scrollerWidth;

	public CustomScrollableLayout(int x, int y, int scrollerWidth, int height, Layout innerLayout, ScrollbarSettings scrollbarSettings) {
		super(x, y, scrollerWidth, height, Component.empty(), scrollbarSettings);
		this.innerLayout = innerLayout;
		this.innerLayout.setPosition(x, y);
		this.scrollRate = scrollbarSettings.scrollRate();
		this.scrollerWidth = scrollerWidth;
	}

	@Override
	protected int contentHeight() {
		return innerLayout.getHeight();
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		ScreenHelper.extractRoundedRectBackground(graphics, getRectangle(), 0xAA000000);
		ScreenHelper.extractScrollAreaBackground(graphics, new ScreenRectangle(getRectangle().right() - scrollbarWidth() - 1, getY(), scrollbarWidth() + 1, getRectangle().height()));

		graphics.enableScissor(getX(), getY(), getX() + width - scrollbarWidth(), getY() + height);
		innerLayout.visitWidgets(widget -> widget.extractRenderState(graphics, mouseX, mouseY, a));

		graphics.disableScissor();
		extractScrollbar(graphics, mouseX, mouseY);
	}

	@Override
	protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
		innerLayout.visitWidgets(widget -> widget.updateNarration(output));
	}

	@Override
	public void visitChildren(@NonNull Consumer<LayoutElement> layoutElementVisitor) {
		layoutElementVisitor.accept(innerLayout);
	}

	@Override
	protected int scrollerHeight() {
		return 15;
	}

	@Override
	public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
		updateScrolling(event);

		if (event.x() > getRectangle().right() - scrollbarWidth()) {
			return super.mouseClicked(event, doubleClick);
		} else {
			innerLayout.visitWidgets(w-> w.mouseClicked(event, doubleClick));
		}
		return false;
	}

	@Override
	public void setScrollAmount(double scrollAmount) {
		super.setScrollAmount(scrollAmount);

		innerLayout.setY(this.getRectangle().top() - (int)this.scrollAmount());
	}

	@Override
	public void arrangeElements() {
		Layout.super.arrangeElements();
		setWidth(innerLayout.getWidth() + scrollerWidth);
	}
}
