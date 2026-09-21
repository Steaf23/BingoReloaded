package io.github.steaf23.bingoreloadedcompanion.client.creator;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class TagButton extends AbstractWidget {

	private static final Identifier ADD_TAG = Identifier.parse("bingoreloadedcompanion:tag_add");
	private static final Identifier REMOVE_TAG = Identifier.parse("bingoreloadedcompanion:tag_remove");
	private static final Identifier DROP_TAG = Identifier.parse("bingoreloadedcompanion:tag_drop");

	private final SelectedTaskComponent selectedTask;

	public TagButton(SelectedTaskComponent selectedTask) {
		super(0, 0, 28, 20, Component.empty());
		this.selectedTask = selectedTask;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		Identifier sprite;
		if (isMouseOver(mouseX, mouseY)) {
			TagBarWidget.Tag tag = selectedTask.taskScreen().selectedTag();
			if (selectedTask.task().tags().contains(tag.name())) {
				sprite = REMOVE_TAG;
			} else {
				sprite = ADD_TAG;
			}
		} else {
			sprite = DROP_TAG;
		}

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), 28, 20);

	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {

	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (!isMouseOver(event.x(), event.y()) || event.button() != 0) {
			return super.mouseClicked(event, doubleClick);
		}

		TagBarWidget.Tag tag = selectedTask.taskScreen().selectedTag();
		selectedTask.tagChanged(tag);

		return false;
	}
}
