package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskDefinition;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskType;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.TaskTooltipComponent;
import io.github.steaf23.bingoreloadedcompanion.client.core.CustomScrollableLayout;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BingoCardTaskListScreen extends Screen {

	private record TaskTab(int index, Item icon, Component name, TaskType type) {}

	private static final TaskTab[] TABS = new TaskTab[]{
			new TaskTab(0, Items.APPLE, Component.nullToEmpty("Items"), TaskType.ITEM),
			new TaskTab(1, Items.ENDER_EYE, Component.nullToEmpty("Advancements"), TaskType.ADVANCEMENT),
			new TaskTab(2, Items.GLOBE_BANNER_PATTERN, Component.nullToEmpty("Statistics"), TaskType.STATISTIC)
	};

	private static final Identifier TAB_SELECTED = Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_2");
	private static final Identifier TAB_UNSELECTED = Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_2");
	private static final Identifier SCROLLER = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
	private static final Identifier SCROLLER_DISABLED = Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");
	private static final Identifier SCROLLER_BACKGROUND = Identifier.parse("bingoreloadedcompanion:empty");

	private static final int MENU_WIDTH = 222;
	private static final int MENU_HEIGHT = 148;

	private static final int TAB_WIDTH = 26;
	private static final int TAB_HEIGHT = 32;

	private static final int SCROLLER_WIDTH = 12;
	private static final int SCROLLER_HEIGHT = 15;
	private static final int SLOT_WIDTH = 24;


	private TaskTab selectedTab;
	private EditBox filterField;
	private CustomScrollableLayout taskListLayout = null;

	private List<TaskDefinition> allTasks;
	private List<TaskWidget> visibleWidgets;

	public BingoCardTaskListScreen(Component title, List<TaskDefinition> tasks) {
		super(title);
		selectedTab = TABS[0];

		allTasks = tasks;
		visibleWidgets = new ArrayList<>();
	}

	@Override
	protected void init() {
		int startX = menuStartX();
		int startY = menuStartY();

		filterField = new EditBox(Minecraft.getInstance().font, startX + 20, startY + 4, 85, 14, Component.nullToEmpty(""));
		filterField.setBordered(true);
		filterField.setVisible(true);
		filterField.setCanLoseFocus(false);
		filterField.setFocused(true);
		this.addRenderableWidget(filterField);

		AbstractScrollArea.ScrollbarSettings settings = new AbstractScrollArea.ScrollbarSettings(SCROLLER, SCROLLER_DISABLED, SCROLLER_BACKGROUND, SCROLLER_WIDTH, SCROLLER_HEIGHT, 24, false);

		int heightLeft = height - TAB_HEIGHT - 10;
		int widthLeft = SLOT_WIDTH * 10 + SCROLLER_WIDTH + 3;

		LinearLayout padding = LinearLayout.vertical();
		GridLayout contents = new GridLayout();
		padding.addChild(contents, LayoutSettings.defaults().padding(3));

		int colCount = (widthLeft - (3 + SCROLLER_WIDTH)) / SLOT_WIDTH;
		int tasksStartX = (width - widthLeft) / 2;

		visibleWidgets = allTasks.stream()
				.filter(def -> def.type() == selectedTab.type())
				.map(def -> new TaskWidget(new TaskWithCount(def, 0), font, _ -> {}, (event, widget) -> {
					widget.updateCount(widget.task().count() + 1);
					int idx = visibleWidgets.indexOf(widget);
				}))
				.toList();
		for (int i = 0; i < visibleWidgets.size(); i++) {
			int col = i % colCount;
			int row = i / colCount;
			contents.addChild(visibleWidgets.get(i), row, col);
		}

		taskListLayout = new CustomScrollableLayout(tasksStartX,
				TAB_HEIGHT,
				3 + SCROLLER_WIDTH, heightLeft, padding, settings);
		taskListLayout.arrangeElements();

		this.addRenderableWidget(taskListLayout);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private int getOverflowRows() {
		return Math.max(0, (allTasks.size() / 8) - 4);
	}

	private void switchTab(TaskTab newTab) {
		selectedTab = newTab;
		filterField.setValue("");

		rebuildWidgets();
	}
//
//	@Override
//	public boolean keyPressed(KeyEvent key) {
//		String oldFilter = filterField.getValue();
//
//		boolean result = filterField.keyPressed(key);
//		if (!oldFilter.equals(filterField.getValue())) {
//			applyFilter();
//		}
//
//		if (super.keyPressed(key)) {
//			return true;
//		}
//
//		return result;
//	}
//
//	@Override
//	public boolean charTyped(CharacterEvent charInput) {
//		String oldFilter = filterField.getValue();
//
//		boolean result = filterField.charTyped(charInput);
//		if (!oldFilter.equals(filterField.getValue())) {
//			applyFilter();
//		}
//
//		if (super.charTyped(charInput)) {
//			return true;
//		}
//
//		return result;
//	}

	@Override
	public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {

		int startX = menuStartX();
		int startY = menuStartY();

		// Tab and menu textures
		int firstTabX = firstTabX();
		int tabStartY = tabStartY();

		for (TaskTab tab : TABS) {
			if (tab.index != selectedTab.index) {
				graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TAB_UNSELECTED, firstTabX + getTabStartX(tab), tabStartY, TAB_WIDTH, TAB_HEIGHT);
			}
		}

//		if (taskListLayout != null) {
//			ScreenHelper.extractInventoryBackground(graphics, taskListLayout.getRectangle());
//		}

//		context.blit(RenderPipelines.GUI_TEXTURED, MENU, startX, startY, 0, 0, 256, 256, 256, 256);
		super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TAB_SELECTED, firstTabX + getTabStartX(selectedTab), tabStartY, TAB_WIDTH, TAB_HEIGHT);

		for (TaskTab tab : TABS) {
			graphics.item(tab.icon.getDefaultInstance(), firstTabX + getTabStartX(tab) + (TAB_WIDTH - 16) / 2, tabStartY + 8);
		}

		graphics.text(Minecraft.getInstance().font, selectedTab.name(), startX + 0, startY + 6, CommonColors.DARK_GRAY, false);

//		if (filteredItemTasks.size() > 40) {
//			int scrollRange = SCROLL_HEIGHT - SCROLLER_HEIGHT;
//			int scrollerHeight = scrollStep * scrollRange / getOverflowRows();
//
//			context.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER, startX + SCROLL_X, startY + SCROLL_Y + scrollerHeight, SCROLLER_WIDTH, SCROLLER_HEIGHT);
//		} else {
//			context.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_DISABLED, startX + SCROLL_X, startY + SCROLL_Y, SCROLLER_WIDTH, SCROLLER_HEIGHT);
//		}

		// Item slots

		int hoveredIndex = -1;
		TaskWidget hoveredTask = null;
		int index = 0;
		for (TaskWidget visibleTask : visibleWidgets) {
			if (visibleTask.isHovered()) {
				hoveredIndex = index;
				hoveredTask = visibleTask;
				break;
			}
			index++;
		}

		if (hoveredIndex != -1) {
//			if (isMouseOnIncreaseCountButton(mouseX, mouseY, xIndex, yIndex)) {
//				context.blitSprite(RenderPipelines.GUI_TEXTURED, HIGHER_COUNT_BUTTON, slotX - 1, slotY - 1, BUTTON_WIDTH, BUTTON_HEIGHT);
//			} else {
//				context.blitSprite(RenderPipelines.GUI_TEXTURED, LOWER_COUNT_BUTTON, slotX - 1, slotY - 1, BUTTON_WIDTH, BUTTON_HEIGHT);
//			}

			TaskTooltipComponent tooltipComponent = new TaskTooltipComponent(hoveredTask.task());

			int slotX = hoveredTask.getX();
			int slotY = hoveredTask.getY();
			graphics.tooltip(font, List.of(tooltipComponent), slotX - tooltipComponent.getWidth(font) / 2, slotY - tooltipComponent.getHeight(font) + 9, DefaultTooltipPositioner.INSTANCE, null);
		}

		// Tooltips

		for (TaskTab tab : TABS) {
			if (isMouseOverTab(mouseX, mouseY, tab))
			{
				graphics.setTooltipForNextFrame(tab.name, mouseX, mouseY);
			}
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		if (super.mouseClicked(click, doubled)) {
			return true;
		}

		if (doubled) return true;

		int button = click.button();
		double mouseX = click.x();
		double mouseY = click.y();

		if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			return false;
		}

		for (TaskTab tab : TABS) {
			if (isMouseOverTab((int) mouseX, (int) mouseY, tab)) {
				if (tab.index != selectedTab.index) {
					switchTab(tab);
				}
				return true;
			}
		}

		return false;
	}

	private int getTabStartX(TaskTab tab) {
		return TAB_WIDTH * tab.index + tab.index - 1;
	}

	private boolean isMouseOverTab(int mouseX, int mouseY, TaskTab tab) {
		return ScreenHelper.isPointWithinBounds(firstTabX() + getTabStartX(tab) + 4, tabStartY() + 4, TAB_WIDTH - 8, TAB_HEIGHT - 8, mouseX, mouseY);
	}

	private int menuStartX() {
		return (width / 2 - (MENU_WIDTH + 200) / 2);
	}

	private int menuStartY() {
		return (height / 2 - (MENU_HEIGHT + 200) / 2);
	}

	private int firstTabX() {
		return width / 2 - (TABS.length * TAB_WIDTH + (TABS.length - 1)) / 2;
	}

	private int tabStartY() {
		return 0;
	}
}
