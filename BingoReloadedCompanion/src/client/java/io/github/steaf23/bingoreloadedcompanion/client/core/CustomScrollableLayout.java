package io.github.steaf23.bingoreloadedcompanion.client.core;

import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class CustomScrollableLayout extends AbstractScrollArea implements Layout {

	public static final int SCROLLER_WIDTH = 12;
	public static final int SCROLLER_HEIGHT = 15;

	private static final Identifier SCROLLER = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
	private static final Identifier SCROLLER_DISABLED = Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");
	private static final Identifier SCROLLER_BACKGROUND = Identifier.parse("bingoreloadedcompanion:empty");

	public static final ScrollbarSettings DEFAULT_SETTINGS = new AbstractScrollArea.ScrollbarSettings(
			SCROLLER, SCROLLER_DISABLED, SCROLLER_BACKGROUND,
			SCROLLER_WIDTH, SCROLLER_HEIGHT, 24, false);

	private final LayoutElement innerLayout;

	private final int scrollRate;
	private final int scrollerWidth;

	public CustomScrollableLayout(int width, int height, LayoutElement innerLayout) {
		super(0, 0, width, height, Component.empty(), DEFAULT_SETTINGS);
		this.innerLayout = innerLayout;
		this.scrollRate = DEFAULT_SETTINGS.scrollRate();
		this.scrollerWidth = SCROLLER_WIDTH;
	}

	public InnerTooltipPositioner tooltipPositioner() {
		return new InnerTooltipPositioner(getRectangle());
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
			return true;
		}
	}

	@Override
	public void onRelease(MouseButtonEvent event) {
		innerLayout.visitWidgets(w -> w.onRelease(event));
	}

	@Override
	public void setScrollAmount(double scrollAmount) {
		super.setScrollAmount(scrollAmount);

		innerLayout.setY(this.getRectangle().top() - (int)this.scrollAmount());
	}

	@Override
	public void arrangeElements() {
		Layout.super.arrangeElements();
		setScrollAmount(scrollAmount());
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		innerLayout.setX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		setScrollAmount(scrollAmount());
	}

	@Override
	public void removeChildren() {
		if (innerLayout instanceof Layout layout) {
			layout.removeChildren();
		}
	}

	public static class InnerTooltipPositioner implements ClientTooltipPositioner {
		private static final int MARGIN = 5;
		private static final int MOUSE_OFFSET_X = 12;
		public static final int MAX_OVERLAP_WITH_WIDGET = 3;
		public static final int MAX_DISTANCE_TO_WIDGET = 5;
		private final ScreenRectangle innerRectangle;

		public InnerTooltipPositioner(final ScreenRectangle innerRectangle) {
			this.innerRectangle = innerRectangle;
		}

		@Override
		public @NonNull Vector2ic positionTooltip(int screenWidth, int screenHeight, int x, int y, int tooltipWidth, int tooltipHeight) {
//			Vector2i result = new Vector2i(x + 12, y);
//			if (result.x + tooltipWidth > screenWidth - 5) {
//				result.x = Math.max(x - 12 - tooltipWidth, 9);
//			}
//
//			result.y += 3;
//			int paddedHeight = tooltipHeight + 3 + 3;
//			int lowestPossibleY = this.innerRectangle.bottom() + 3 + getOffset(0, 0, this.innerRectangle.height());
//			int maxY = screenHeight - 5;
//			if (lowestPossibleY + paddedHeight <= maxY) {
//				result.y = result.y + getOffset(result.y, this.innerRectangle.top(), this.innerRectangle.height());
//			} else {
//				result.y = result.y - (paddedHeight + getOffset(result.y, this.innerRectangle.bottom(), this.innerRectangle.height()));
//			}
//
//			return result;

			return new Vector2i(x + 12, y);
		}

		private static int getOffset(final int mouseY, final int widgetY, final int widgetHeight) {
			int distance = Math.min(Math.abs(mouseY - widgetY), widgetHeight);
			return Math.round(Mth.lerp((float)distance / widgetHeight, widgetHeight - 3, 5.0F));
		}
	}
}
