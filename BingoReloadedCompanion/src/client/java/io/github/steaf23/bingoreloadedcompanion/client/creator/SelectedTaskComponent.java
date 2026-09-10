package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.core.SpinBoxWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class SelectedTaskComponent extends LinearLayout {

	private final Font font;
	private TaskWithCount task;
	private final CreatorTaskScreen taskScreen;

	public SelectedTaskComponent(TaskWithCount task, Font font, CreatorTaskScreen taskScreen) {
		super(0, 0, Orientation.HORIZONTAL);
		this.task = task;
		this.font = font;
		this.taskScreen = taskScreen;

		addChild(new ItemDisplayWidget(Minecraft.getInstance(),0, 0, 16, 16, Component.empty(), task.createStack(), false, false) {
			@Override
			public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
				return false;
			}
		}, LayoutSettings.defaults().padding(2));
		addChild(SpinBoxWidget.defaultIntegerBox(font, this::valueChanged)
				.minValue(1.0)
				.maxValue(64.0)
				.startValue(task.count()), LayoutSettings.defaults().paddingVertical(3).paddingLeft(6));
	}

	public void valueChanged(int newValue) {
		task = task.copy(newValue);
		taskScreen.updateSelectedTask(task);
	}
}
