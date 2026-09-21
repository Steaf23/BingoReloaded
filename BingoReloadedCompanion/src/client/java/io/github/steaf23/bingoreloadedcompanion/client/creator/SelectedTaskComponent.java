package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.core.SpinBoxWidget;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SelectedTaskComponent extends LinearLayout {

	private static final Identifier DIVIDER = Identifier.parse("bingoreloadedcompanion:horizontal_divider");
	private static final Identifier CLEAR_BTN = Identifier.parse("bingoreloadedcompanion:clear");
	private static final Identifier CLEAR_HOVER_BTN = Identifier.parse("bingoreloadedcompanion:clear_hover");
	private final Font font;
	private TaskWithCount task;
	private final CreatorTaskScreen taskScreen;
	private final SpinBoxWidget countEdit;
	private final TagButton tagButton;
	private final TagBarWidget tagBar;
	private boolean initialized;
	private ItemDisplayWidget display;

	public SelectedTaskComponent(TaskWithCount task, Font font, CreatorTaskScreen taskScreen) {
		super(0, 0, Orientation.VERTICAL);
		this.task = task;
		this.font = font;
		this.taskScreen = taskScreen;

		FrameLayout frame = new FrameLayout(92, 10);
		addChild(frame);

		display = new ItemDisplayWidget(Minecraft.getInstance(),0, 0, 16, 16, Component.empty(), task.createStack(), false, false) {
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
		frame.addChild(display, LayoutSettings.defaults().padding(2).alignHorizontallyLeft());

		countEdit = SpinBoxWidget.defaultIntegerBox(font, this::countChanged)
				.minValue(1.0)
				.maxValue(64.0)
				.startValue(task.count());
		frame.addChild(countEdit, LayoutSettings.defaults().paddingVertical(3).alignHorizontallyCenter());

		tagButton = new TagButton(this);
		frame.addChild(tagButton, LayoutSettings.defaults().alignHorizontallyCenter());

		ImageButton removeBtn = new ImageButton(0, 0, 16, 16, new WidgetSprites(CLEAR_BTN, CLEAR_HOVER_BTN),
				_ -> {
			countChanged(0);
			taskScreen.updateSelectedTasksLater();
			}, Component.literal("Create New Card"));

		frame.addChild(removeBtn, LayoutSettings.defaults().paddingHorizontal(3).alignHorizontallyRight());

		List<TagBarWidget.Tag> tagsToDraw = getTagsToDraw();

		tagBar = new TagBarWidget(tagsToDraw, 2);
		addChild(tagBar);

		frame.arrangeElements();
		addChild(ImageWidget.sprite(frame.getWidth(), 6, DIVIDER));
		initialized = true;
	}

	public CreatorTaskScreen taskScreen() {
		return taskScreen;
	}

	public TaskWithCount task() {
		return task;
	}

	public void countChanged(int newValue) {
		if (!initialized) {
			return;
		}

		task = task.copy(newValue);
		taskScreen.updateSelectedTask(task);
	}

	public void tagChanged(TagBarWidget.Tag tag) {
		Set<String> tags = new HashSet<>(task.tags());
		if (tags.contains(tag.name())) {
			tags.remove(tag.name());
		} else {
			tags.add(tag.name());
		}
		task = task.copy(tags);
		tagBar.setTags(getTagsToDraw());
		taskScreen.updateSelectedTask(task);
		taskScreen.arrangeSelectedTasks();
	}

	public void setEditMode(TaskEditMode editMode) {
		switch (editMode) {
			case COUNT -> {
				countEdit.setVisible(task.task().maxCount() > 1);
				tagButton.visible = false;
			}
			case TAG -> {
				countEdit.setVisible(false);
				tagButton.visible = true;
			}
		}
	}

	public List<TagBarWidget.Tag> getTagsToDraw() {
		Map<String, TextColor> existingTags = taskScreen.creatorSuite().allTags;
		List<TagBarWidget.Tag> tags = new ArrayList<>();
		for (String tag : task.tags()) {
			tags.add(new TagBarWidget.Tag(tag, existingTags.getOrDefault(tag, NamedTextColor.WHITE)));
		}
		return tags;
	}
}
