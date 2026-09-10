package io.github.steaf23.bingoreloadedcompanion.client.core;

import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class Popup<L extends Layout> extends AbstractWidget {

	private final L layout;
	private final Layout siblingLayout;

	public Popup(int width, int height, L layout, Layout siblingLayout) {
		super(0, 0, width, height, Component.empty());
		this.layout = layout;
		this.siblingLayout = siblingLayout;
		layout.arrangeElements();
	}

	public L layout() {
		return layout;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (!visible) {
			return;
		}

		ScreenHelper.extractRoundedRectBackground(graphics, getRectangle(), 0xAA000000);

		layout.visitWidgets(w -> w.extractRenderState(graphics, mouseX, mouseY, a));
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (!visible) {
			return false;
		}

		layout.visitWidgets(w -> {
			if (!w.isMouseOver(event.x(), event.y())) {
				return;
			}
			w.mouseClicked(event, doubleClick);
		});
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (!visible) {
			return false;
		}

		layout.visitWidgets(w -> {
			w.mouseReleased(event);
		});
		return true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (!visible) {
			return false;
		}

		layout.visitWidgets(w -> {
			w.mouseDragged(event, dx, dy);
		});
		return true;
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		if (!visible) {
			return false;
		}

		layout.visitWidgets(w -> {
			if (!w.isMouseOver(x, y)) {
				return;
			}
			w.mouseScrolled(x, y, scrollX, scrollY);
		});
		return true;
	}

	public void show() {
		visible = true;
		siblingLayout.visitWidgets(w -> w.visible = false);
	}

	public void hide() {
		visible = false;
		siblingLayout.visitWidgets(w -> w.visible = true);
	}
}
