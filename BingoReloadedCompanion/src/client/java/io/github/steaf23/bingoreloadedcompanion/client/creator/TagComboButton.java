package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class TagComboButton extends LinearLayout {

	private static final Identifier TAG_ICON = Identifier.parse("bingoreloadedcompanion:tag");

	private final TagBarWidget.Tag tag;

	public TagComboButton(TagBarWidget.Tag tag, Font font, Consumer<TagBarWidget.Tag> clicked) {
		super(0, 0, Orientation.HORIZONTAL);
		this.tag = tag;

		FrameLayout inner = new FrameLayout(245, 16);
		addChild(inner);

		AbstractWidget background = new AbstractWidget(0, 0, 245, 16, Component.empty()) {
			@Override
			protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
				ScreenHelper.extractRoundedRectBackground(graphics, TagComboButton.this.getRectangle(), ScreenHelper.addAlphaToColor(tag.color().value(), 128));
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TAG_ICON, getX() + 210, getY(), 24, 16, ScreenHelper.addAlphaToColor(tag.color().value(), 255));
			}

			@Override
			protected void updateWidgetNarration(NarrationElementOutput output) {

			}

			@Override
			public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
				if (isMouseOver(event.x(), event.y())) {
					clicked.accept(tag);
				}
				return super.mouseClicked(event, doubleClick);
			}
		};
		inner.addChild(background);
		inner.addChild(new StringWidget(Component.literal(tag.name()), font) {
			@Override
			public boolean isMouseOver(double mouseX, double mouseY) {
				return false;
			}
		}, LayoutSettings.defaults().paddingVertical(4).paddingHorizontal(10).alignHorizontallyLeft());
	}

	public TagBarWidget.Tag tag() {
		return tag;
	}
}
