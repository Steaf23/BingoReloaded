package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.selection;

import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.core.SlidingSpinbox;
import io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.TaskEditMode;
import io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.TaskTooltipComponent;
import io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.TagInfo;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
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
import java.util.Set;
import java.util.function.Consumer;

public class SelectedTaskComponent extends LinearLayout {

	private static final Identifier DIVIDER = Identifier.parse("bingoreloadedcompanion:horizontal_divider");
	private static final Identifier CLEAR_BTN = Identifier.parse("bingoreloadedcompanion:clear");
	private static final Identifier CLEAR_HOVER_BTN = Identifier.parse("bingoreloadedcompanion:clear_hover");
	private final Font font;
	private TaskWithCount task;
//	private final SpinBoxWidget countEdit;
	private final TagButton tagButton;
	private final SlidingSpinbox countSlider;
	private final TagBarWidget tagBar;
	private final Consumer<SelectedTaskComponent> updatedCallback;
	private final TagInfo tagInfo;

	private boolean initialized;
	private ItemDisplayWidget display;

	public SelectedTaskComponent(TaskWithCount task, Font font, TagInfo tags, Consumer<SelectedTaskComponent> updatedCallback) {
		super(0, 0, Orientation.VERTICAL);
		this.task = task;
		this.font = font;
		this.updatedCallback = updatedCallback;
		this.tagInfo = tags;

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

//		countEdit = SpinBoxWidget.defaultIntegerBox(font, this::countChanged)
//				.minValue(1.0)
//				.maxValue(64.0)
//				.startValue(task.count());
//		frame.addChild(countEdit, LayoutSettings.defaults().paddingVertical(3).alignHorizontallyCenter());

		countSlider = new SlidingSpinbox(1, 64, font, this::countChanged).withValue(task().count());
		frame.addChild(countSlider);

		tagButton = new TagButton(this, tagInfo);
		frame.addChild(tagButton, LayoutSettings.defaults().alignHorizontallyCenter());

		ImageButton removeBtn = new ImageButton(0, 0, 16, 16, new WidgetSprites(CLEAR_BTN, CLEAR_HOVER_BTN),
				_ -> {
			countChanged(0);
			updatedCallback.accept(this);
			}, Component.literal("Create New Card"));

		frame.addChild(removeBtn, LayoutSettings.defaults().paddingHorizontal(3).alignHorizontallyRight());

		List<TagInfo.Tag> tagsToDraw = getTagsToDraw();

		tagBar = new TagBarWidget(tagsToDraw, 2);
		addChild(tagBar);

		frame.arrangeElements();
		addChild(ImageWidget.sprite(frame.getWidth(), 6, DIVIDER));
		initialized = true;
	}

	public TaskWithCount task() {
		return task;
	}

	public void updateTask(TaskWithCount newTask) {
		task = newTask;
		countSlider.withValue(task().count());
		tagBar.setTags(getTagsToDraw());
	}

	private void countChanged(int newValue) {
		if (!initialized) {
			return;
		}

		task = task.copy(newValue);
		updatedCallback.accept(this);
	}

	public void tagChanged(TagInfo.Tag tag) {
		Set<String> tags = new HashSet<>(task.tags());
		if (tags.contains(tag.name())) {
			tags.remove(tag.name());
		} else {
			tags.add(tag.name());
		}
		task = task.copy(tags);
		tagBar.setTags(getTagsToDraw());
		updatedCallback.accept(this);
	}

	public void setEditMode(TaskEditMode editMode) {
		switch (editMode) {
			case COUNT -> {
				boolean showCount = task.task().maxCount() > 1;
				countSlider.visible = showCount;
				tagButton.visible = false;
			}
			case TAG -> {
				countSlider.visible = false;
				tagButton.visible = true;
			}
		}
	}

	public List<TagInfo.Tag> getTagsToDraw() {
		List<TagInfo.Tag> tags = new ArrayList<>();
		for (String tag : task.tags()) {
			tags.add(tagInfo.tagByName(tag));
		}
		return tags;
	}
}
