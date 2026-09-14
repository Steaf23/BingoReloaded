package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.core.SpinBoxWidget;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SelectedTaskComponent extends LinearLayout {

	private static final Identifier CLEAR_BTN = Identifier.parse("bingoreloadedcompanion:clear");
	private static final Identifier CLEAR_HOVER_BTN = Identifier.parse("bingoreloadedcompanion:clear_hover");
	private final Font font;
	private TaskWithCount task;
	private final CreatorTaskScreen taskScreen;
	private boolean initialized;

	ItemDisplayWidget display;

	public SelectedTaskComponent(TaskWithCount task, Font font, CreatorTaskScreen taskScreen) {
		super(0, 0, Orientation.HORIZONTAL);
		this.task = task;
		this.font = font;
		this.taskScreen = taskScreen;

		ItemDisplayWidget display = new ItemDisplayWidget(Minecraft.getInstance(),0, 0, 16, 16, Component.empty(), task.createStack(), false, false) {
			@Override
			public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
				return false;
			}

			@Override
			protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
				super.extractWidgetRenderState(graphics, mouseX, mouseY, a);

				if (this.isHovered()) {
					ScreenHelper.extractTooltipComponent(graphics, font, new TaskTooltipComponent(task, task.getName(), false), mouseX, mouseY, new MenuTooltipPositioner(getRectangle()));
				}
			}
		};
		addChild(display, LayoutSettings.defaults().padding(2));

		if (task.task().maxCount() > 1) {
			addChild(SpinBoxWidget.defaultIntegerBox(font, this::valueChanged)
					.minValue(1.0)
					.maxValue(64.0)
					.startValue(task.count()), LayoutSettings.defaults().paddingVertical(3).paddingLeft(6));
		}

		ImageButton removeBtn = new ImageButton(0, 0, 16, 16, new WidgetSprites(CLEAR_BTN, CLEAR_HOVER_BTN),
				_ -> {
			valueChanged(0);
			taskScreen.updateSelectedTasksLater();
			}, Component.literal("Create New Card"));

		addChild(removeBtn, LayoutSettings.defaults().paddingHorizontal(3));
		initialized = true;
	}

	public void valueChanged(int newValue) {
		if (!initialized) {
			return;
		}

		task = task.copy(newValue);
		taskScreen.updateSelectedTask(task);
	}
}
