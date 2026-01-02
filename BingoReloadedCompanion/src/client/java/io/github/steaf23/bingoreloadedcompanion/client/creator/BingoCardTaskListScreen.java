package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloadedcompanion.card.taskslot.AdvancementTask;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.ItemTask;
import io.github.steaf23.bingoreloadedcompanion.card.taskslot.TaskSlot;
import io.github.steaf23.bingoreloadedcompanion.client.TaskTooltipComponent;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingoCardTaskListScreen extends Screen {

	private record TaskTab(int index, Item icon, Component name) {}

	private static final TaskTab[] TABS = new TaskTab[]{
			new TaskTab(0, Items.APPLE, Component.nullToEmpty("Items")),
			new TaskTab(1, Items.ENDER_EYE, Component.nullToEmpty("Advancements")),
			new TaskTab(2, Items.GLOBE_BANNER_PATTERN, Component.nullToEmpty("Statistics"))
	};

	private static final Identifier MENU = Identifier.parse("bingoreloadedcompanion:textures/gui/task_list.png");
	private static final Identifier SELECTED_SLOT = Identifier.parse("bingoreloadedcompanion:selected_slot");
	private static final Identifier HIGHER_COUNT_BUTTON = Identifier.parse("bingoreloadedcompanion:higher_count");
	private static final Identifier LOWER_COUNT_BUTTON = Identifier.parse("bingoreloadedcompanion:lower_count");
	private static final Identifier FILTER_EMPTY = Identifier.parse("bingoreloadedcompanion:filter_empty");
	private static final Identifier TAB_SELECTED = Identifier.withDefaultNamespace("container/creative_inventory/tab_top_selected_2");
	private static final Identifier TAB_UNSELECTED = Identifier.withDefaultNamespace("container/creative_inventory/tab_top_unselected_2");
	private static final Identifier SCROLLER = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
	private static final Identifier SCROLLER_DISABLED = Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");

	private static final int MENU_WIDTH = 222;
	private static final int MENU_HEIGHT = 148;

	private static final int TAB_WIDTH = 26;
	private static final int TAB_HEIGHT = 32;

	private static final int SCROLLER_WIDTH = 12;
	private static final int SCROLLER_HEIGHT = 15;
	private static final int SCROLL_HEIGHT = 118;
	private static final int SCROLL_X = 202;
	private static final int SCROLL_Y = 20;

	private static final int SLOT_WIDTH = 24;
	private static final int SLOT_HEIGHT = 24;
	private static final int SLOT_START_X = 7;
	private static final int SLOT_START_Y = 19;

	private static final int BUTTON_WIDTH = 26;
	private static final int BUTTON_HEIGHT = 26;

	private TaskTab selectedTab;
	private EditBox filterField;
	private int scrollStep = -1;

	private final Map<Identifier, TaskSlot> selectedTasks = new HashMap<>();

	private List<? extends TaskSlot> filteredItemTasks;
	private List<? extends TaskSlot> visibleTasks;
	private List<ScreenTab> tabs;

	private boolean scrolling = false;

	public BingoCardTaskListScreen(Component title, List<? extends TaskSlot> tasks) {
		super(title);
		for (TaskSlot task : tasks) {
			selectedTasks.put(task.id(), task);
		}

		filteredItemTasks = new ArrayList<>();
		visibleTasks = new ArrayList<>();
	}

	@Override
	protected void init() {
		selectedTab = TABS[0];

		int startX = menuStartX();
		int startY = menuStartY();

		filterField = new EditBox(Minecraft.getInstance().font, startX + 114, startY + 4, 85, 14, Component.nullToEmpty(""));
		filterField.setBordered(true);
		filterField.setVisible(true);
		filterField.setCanLoseFocus(false);
		filterField.setFocused(true);
		this.addRenderableWidget(filterField);

		setScrollStep(0);
		applyFilter();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void setScrollStep(int newStep) {
		if (scrollStep == newStep) {
			return;
		}

		// subtract 4 out of 5 rows because the step acts like a sliding window, not as a collection of singular rows.
		scrollStep = Math.clamp(newStep, 0, getOverflowRows());

		updateVisibleTasks();
	}

	private void setTaskCount(TaskSlot task, int newCount) {
		Identifier id = task.id();

		if (newCount > 0) {
			selectedTasks.put(id, task.copyWithCount(newCount));
		} else {
			selectedTasks.remove(id);
		}
	}

	private void applyFilter() {
		String text = filterField.getValue().toLowerCase().replace(" ", "_");

		List<? extends TaskSlot> tasks = switch (selectedTab.index) {
			case 0 -> allItemTasks();
			case 1 -> allAdvancementTasks();
			case 2 -> allStatisticTasks();
			default -> List.of();
		};
		filteredItemTasks = tasks.stream()
				.filter(t -> t.id().toString().contains(text))
				.toList();

		updateVisibleTasks();
	}

	private void updateVisibleTasks() {
		int startIndex = scrollStep * 8;

		if (filteredItemTasks.isEmpty()) {
			visibleTasks = List.of();
		} else {
			visibleTasks = filteredItemTasks
					.subList(startIndex, Math.min(startIndex + 40, filteredItemTasks.size() - 1));
		}
	}

	private int getOverflowRows() {
		return Math.max(0, (filteredItemTasks.size() / 8) - 4);
	}

	private List<? extends TaskSlot> allItemTasks() {
		return BuiltInRegistries.ITEM.stream()
				.map(item -> new ItemTask(BuiltInRegistries.ITEM.getKey(item), 0)).toList()
				.subList(1, BuiltInRegistries.ITEM.size() - 1);
	}

	private List<? extends TaskSlot> allAdvancementTasks() {
		return Minecraft.getInstance().getConnection().getAdvancements().getTree().nodes()
				.stream().map(placedAdv -> new AdvancementTask(placedAdv.holder(), false))
				.toList();
	}

	private List<? extends TaskSlot> allStatisticTasks() {
		return List.of();
	}

	private void switchTab(TaskTab newTab) {
		selectedTab = newTab;
		filterField.setValue("");



		applyFilter();
	}

	@Override
	public boolean keyPressed(KeyEvent key) {
		String oldFilter = filterField.getValue();

		boolean result = filterField.keyPressed(key);
		if (!oldFilter.equals(filterField.getValue())) {
			applyFilter();
		}

		if (super.keyPressed(key)) {
			return true;
		}

		return result;
	}

	@Override
	public boolean charTyped(CharacterEvent charInput) {
		String oldFilter = filterField.getValue();

		boolean result = filterField.charTyped(charInput);
		if (!oldFilter.equals(filterField.getValue())) {
			applyFilter();
		}

		if (super.charTyped(charInput)) {
			return true;
		}

		return result;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
		super.render(context, mouseX, mouseY, deltaTicks);

		int startX = menuStartX();
		int startY = menuStartY();

		// Tab and menu textures
		int firstTabX = firstTabX();
		int tabStartY = tabStartY();

		for (TaskTab tab : TABS) {
			if (tab.index != selectedTab.index) {
				context.blitSprite(RenderPipelines.GUI_TEXTURED, TAB_UNSELECTED, firstTabX + getTabStartX(tab), tabStartY, TAB_WIDTH, TAB_HEIGHT);
			}
		}

		context.blit(RenderPipelines.GUI_TEXTURED, MENU, startX, startY, 0, 0, 256, 256, 256, 256);

		context.blitSprite(RenderPipelines.GUI_TEXTURED, TAB_SELECTED, firstTabX + getTabStartX(selectedTab), tabStartY, TAB_WIDTH, TAB_HEIGHT);

		for (TaskTab tab : TABS) {
			context.renderItem(tab.icon.getDefaultInstance(), firstTabX + getTabStartX(tab) + (TAB_WIDTH - 16) / 2, tabStartY + 8);
		}

		context.drawString(Minecraft.getInstance().font, selectedTab.name(), startX + 8, startY + 6, CommonColors.DARK_GRAY, false);

		if (filteredItemTasks.size() > 40) {
			int scrollRange = SCROLL_HEIGHT - SCROLLER_HEIGHT;
			int scrollerHeight = scrollStep * scrollRange / getOverflowRows();

			context.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER, startX + SCROLL_X, startY + SCROLL_Y + scrollerHeight, SCROLLER_WIDTH, SCROLLER_HEIGHT);
		} else {
			context.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_DISABLED, startX + SCROLL_X, startY + SCROLL_Y, SCROLLER_WIDTH, SCROLLER_HEIGHT);
		}

		// Item slots

		int slotStartX = startX + SLOT_START_X;
		int slotStartY = startY + SLOT_START_Y;

		int hoveredIndex = -1;
		TaskSlot hoveredTask = null;
		int index = 0;
		for (TaskSlot visibleTask : visibleTasks) {
			TaskSlot task = visibleTask;
			Identifier id = task.id();
			if (selectedTasks.containsKey(id)) {
				task = selectedTasks.get(id);
			}

			int slotX = index % 8;
			int slotY = index / 8;

			int x = slotStartX + slotX * SLOT_WIDTH + 4;
			int y = slotStartY + slotY * SLOT_HEIGHT + 4;

			if (task.completeCount() > 0) {
				context.blitSprite(RenderPipelines.GUI_TEXTURED, SELECTED_SLOT, x - 4, y - 4, SLOT_WIDTH, SLOT_HEIGHT);
			}

			if (isMouseOverSlot(mouseX, mouseY, slotX, slotY))
			{
				hoveredIndex = index;
				hoveredTask = task;
			}
			else {

				ItemStack stack = new ItemStack(task.item(), task.completeCount() == 0 ? 1 : task.completeCount());
				context.renderItem(stack, x, y);
				context.renderItemDecorations(font, stack, x, y, task.completeCount() == 1 ? "1" : null);
			}

			index++;
		}

		if (hoveredIndex != -1) {
			int xIndex = hoveredIndex % 8;
			int yIndex = hoveredIndex / 8;
			int slotX = slotStartX + SLOT_WIDTH * xIndex;
			int slotY = slotStartY + SLOT_HEIGHT * yIndex;

			if (isMouseOnIncreaseCountButton(mouseX, mouseY, xIndex, yIndex)) {
				context.blitSprite(RenderPipelines.GUI_TEXTURED, HIGHER_COUNT_BUTTON, slotX - 1, slotY - 1, BUTTON_WIDTH, BUTTON_HEIGHT);
			} else {
				context.blitSprite(RenderPipelines.GUI_TEXTURED, LOWER_COUNT_BUTTON, slotX - 1, slotY - 1, BUTTON_WIDTH, BUTTON_HEIGHT);
			}

			TaskTooltipComponent tooltipComponent = new TaskTooltipComponent(hoveredTask);

			context.renderTooltip(font, List.of(tooltipComponent), slotX - tooltipComponent.getWidth(font) / 2, slotY - tooltipComponent.getHeight(font) + 9, DefaultTooltipPositioner.INSTANCE, null);
		}

		// Tooltips

		for (TaskTab tab : TABS) {
			if (isMouseOverTab(mouseX, mouseY, tab))
			{
				context.setTooltipForNextFrame(tab.name, mouseX, mouseY);
			}
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
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

		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 8; x++) {
				if (!isMouseOverSlot((int) mouseX, (int) mouseY, x, y)) {
					continue;
				}

				TaskSlot task = visibleTasks.get(y * 8 + x);
				Identifier id = task.id();
				if (selectedTasks.containsKey(id)) {
					task = selectedTasks.get(id);
				}
				if (isMouseOnIncreaseCountButton((int) mouseX, (int) mouseY, x, y)) {
					setTaskCount(task, task.completeCount() + 1);
				} else {
					setTaskCount(task, task.completeCount() - 1);
				}
			}
		}

		if (isMouseOverScrollbar((int) mouseX, (int) mouseY)) {
			scrolling = true;
		}

		return false;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent click) {
		scrolling = false;
		return super.mouseReleased(click);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
		int button = click.button();
		double mouseX = click.x();
		double mouseY = click.y();

		if (!scrolling)
		{
			return super.mouseDragged(click, deltaX, deltaY);
		}
		int min = menuStartY() + SCROLL_Y + SCROLLER_HEIGHT / 2;
		int max = min + SCROLL_HEIGHT - SCROLLER_HEIGHT - SCROLLER_HEIGHT / 2;

		int targetPixel = (int) mouseY;
		targetPixel = Math.clamp(targetPixel, min, max);
		targetPixel -= min;

		setScrollStep(targetPixel * getOverflowRows() / (max - min));

		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
			return true;
		}

		setScrollStep(scrollStep - (int) Math.signum(verticalAmount));

		return true;
	}

	private int getTabStartX(TaskTab tab) {
		return TAB_WIDTH * tab.index + tab.index - 1;
	}

	private boolean isMouseOverTab(int mouseX, int mouseY, TaskTab tab) {
		return ScreenHelper.isPointWithinBounds(firstTabX() + getTabStartX(tab) + 4, tabStartY() + 4, TAB_WIDTH - 8, TAB_HEIGHT - 8, mouseX, mouseY);
	}

	private boolean isMouseOverSlot(int mouseX, int mouseY, int slotX, int slotY) {
		return ScreenHelper.isPointWithinBounds(
				menuStartX() + SLOT_START_X + SLOT_WIDTH * slotX + 1,
				menuStartY() + SLOT_START_Y + SLOT_HEIGHT * slotY + 1,
				SLOT_WIDTH - 2, SLOT_HEIGHT - 2, mouseX, mouseY);
	}

	private boolean isMouseOnIncreaseCountButton(int mouseX, int mouseY, int slotX, int slotY) {
		return ScreenHelper.isPointWithinBounds(
				menuStartX() + SLOT_START_X + SLOT_WIDTH * slotX + 1,
				menuStartY() + SLOT_START_Y + SLOT_HEIGHT * slotY,
				SLOT_WIDTH - 2, (SLOT_HEIGHT - 2) / 2, mouseX, mouseY);
	}

	private boolean isMouseOverScrollbar(int mouseX, int mouseY) {
		return ScreenHelper.isPointWithinBounds(menuStartX() + SCROLL_X, menuStartY() + SCROLL_Y, SCROLLER_WIDTH, SCROLL_HEIGHT, mouseX, mouseY);
	}

	private int menuStartX() {
		return (width / 2 - MENU_WIDTH / 2);
	}

	private int menuStartY() {
		return (height / 2 - MENU_HEIGHT / 2);
	}

	private int firstTabX() {
		return menuStartX() + MENU_WIDTH / 2 - (TABS.length * TAB_WIDTH + (TABS.length - 1)) / 2;
	}

	private int tabStartY() {
		return menuStartY() - TAB_HEIGHT + 4;
	}
}
