package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.selection;

import io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.TagInfo;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TagBarWidget extends AbstractWidget {

	private final int spacing;
	private List<TagInfo.Tag> tags;

	public TagBarWidget(List<TagInfo.Tag> tags, int spacing) {
		super(0, 0, ((spacing * (tags.size() - 1)) + tags.size() * 6), tags.isEmpty() ? 0 : 6, Component.empty());
		this.spacing = spacing;
		this.tags = tags;
	}

	public void setTags(List<TagInfo.Tag> tags) {
		this.tags = tags;
	}

	@Override
	public int getWidth() {
		return ((spacing * (tags.size() - 1)) + tags.size() * 6);
	}

	@Override
	public int getHeight() {
		return tags.isEmpty() ? 0 : 6;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		int i = 0;
		for (TagInfo.Tag tag : tags) {
			ScreenHelper.extractRoundedRectBackground(graphics, getX() + (i * (8 + spacing)) + 2, getY() + 1, 6, 6, ScreenHelper.addAlphaToColor(tag.color().value(), 255));
			i++;
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}
}
