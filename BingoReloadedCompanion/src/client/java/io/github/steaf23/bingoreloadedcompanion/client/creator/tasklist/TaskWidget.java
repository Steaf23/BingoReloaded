package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class TaskWidget extends AbstractWidget {

	private static final Identifier TASK_ADD = Identifier.parse("bingoreloadedcompanion:task_add");
	private static final Identifier TASK_OUTLINE = Identifier.parse("bingoreloadedcompanion:task_outline");
	private static final Identifier TASK_REMOVE = Identifier.parse("bingoreloadedcompanion:task_remove");

	private static final int TASK_WIDTH = 24;
	private static final int TASK_HEIGHT = 24;

	private final TaskDefinition task;
	private final Font font;
	private final ScreenRectangle drawRect;
	private final TaskWithCount.NameSupplier nameSupplier;

	private final TaskWidgetClicked clickedCallback;
	private final TaskPickerScreen screen;

	private boolean selected = false;

	public TaskWidget(TaskPickerScreen screen, TaskDefinition task, Font font, TaskWithCount.NameSupplier nameSupplier, TaskWidgetClicked clickedCallback) {
		super(0, 0, TASK_WIDTH, TASK_HEIGHT, Component.empty());
		this.screen = screen;
		this.task = task;
		this.font = font;
		this.clickedCallback = clickedCallback;
		this.nameSupplier = nameSupplier;

		drawRect = new ScreenRectangle(getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4);
	}

	public TaskDefinition task() {
		return task;
	}

	@Override
	protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {

		if (isHovered() && !isSelected()) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TASK_ADD, getX(), getY(), TASK_WIDTH, TASK_HEIGHT);
		} else {
			if (isSelected()) {
				ScreenHelper.extractRoundedRectBackground(graphics, getX() + 2, getY() + 2, getWidth() - 4, getHeight() - 4, 0x77309f14);
				if (isHovered()) {
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TASK_OUTLINE, getX(), getY(), TASK_WIDTH, TASK_HEIGHT);
				}
			}

			ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(task.iconItem().namespace(), task.iconItem().value())));
			graphics.item(stack, getX() + 4, getY() + 4);
		}

		if (isHovered()) {
			if (graphics.containsPointInScissor(mouseX, mouseY)) {
				graphics.requestCursor(CursorTypes.POINTING_HAND);
				if (screen != null) {
					screen.extractTaskTooltip(graphics, mouseX, mouseY, this);
				}
			}
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
		boolean increment;
		if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
			increment = true;
		} else if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT)  {
			increment = false;
		} else {
			return;
		}

		select(clickedCallback.handleClicked(this, increment));
	}

	public boolean isSelected() {
		return selected;
	}

	public void select(boolean select) {
		selected = select;
	}

	public Component getName(TaskWithCount task) {
		return nameSupplier.get(task);
	}

	@FunctionalInterface
	public interface TaskWidgetClicked {
		boolean handleClicked(TaskWidget widget, boolean increment);
	}
}
