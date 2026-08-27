package io.github.steaf23.bingoreloadedcompanion.client.creator;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskSlot;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class TaskWidget extends AbstractWidget {

	private static final Identifier TASK_ADD = Identifier.parse("bingoreloadedcompanion:task_add");
	private static final Identifier TASK_REMOVE = Identifier.parse("bingoreloadedcompanion:task_remove");

	private static final int TASK_WIDTH = 24;
	private static final int TASK_HEIGHT = 24;

	private TaskSlot task;
	private final Font font;
	private final ScreenRectangle drawRect;

	private final Consumer<TaskWidget> selectionChangedCallback;

	public TaskWidget(TaskSlot task, Font font, Consumer<TaskWidget> selectionChangedCallback) {
		super(0, 0, TASK_WIDTH, TASK_HEIGHT, Component.empty());
		this.task = task;
		this.font = font;
		this.selectionChangedCallback = selectionChangedCallback;
		drawRect = new ScreenRectangle(getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4);
	}

	public TaskSlot task() {
		return task;
	}

	@Override
	protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (isSelected()) {
			ScreenHelper.extractRoundedRectBackground(graphics, getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4, 0x773d6fe3);
		}

		if (isMouseOver(mouseX, mouseY)) {
			Identifier sprite = TASK_ADD;
			if (task.completeCount() != 0) {
				sprite = TASK_REMOVE;
			}
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), TASK_WIDTH, TASK_HEIGHT);
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		} else {
			ItemStack stack = task.createStack();
			graphics.item(task.createStack(), getX() + 4, getY() + 4);
			graphics.itemDecorations(font, stack, getX() + 4, getY() + 4, task.completeCount() == 0 ? null : "" + task.completeCount());
		}
	}

	@Override
	protected void updateWidgetNarration(@NonNull NarrationElementOutput output) {
		output.add(NarratedElementType.TITLE, task.name());
	}

	@Override
	protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
		return buttonInfo.button() == 0 || buttonInfo.button() == 1;
	}

	@Override
	public void onClick(@NonNull MouseButtonEvent event, boolean doubleClick) {

		if (isSelected()) {
			task = task.copyWithCount(0);
		}
		else {
			task = task.copyWithCount(1);
		}

		selectionChangedCallback.accept(this);

		super.onClick(event, false);
	}

	public boolean isSelected() {
		return task.completeCount() > 0;
	}
}
