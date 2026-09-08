package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloaded.protocol.data.card.CustomList;
import io.github.steaf23.bingoreloaded.protocol.data.task.AdvancementNode;
import io.github.steaf23.bingoreloaded.protocol.data.task.ConfiguredTask;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticCategory;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.core.CollapsibleTreeLayout;
import io.github.steaf23.bingoreloadedcompanion.client.core.CustomScrollableLayout;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.kyori.adventure.key.Key;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BingoCardTaskListScreen extends Screen {

	private static final Identifier SEARCH_ICON = Identifier.withDefaultNamespace("icon/search");
	private static final Identifier SCROLLER = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
	private static final Identifier SCROLLER_DISABLED = Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");
	private static final Identifier SCROLLER_BACKGROUND = Identifier.parse("bingoreloadedcompanion:empty");

	private static final int TAB_HEIGHT = 20;

	private static final int SCROLLER_WIDTH = 12;
	private static final int SCROLLER_HEIGHT = 15;
	private static final int SLOT_WIDTH = 24;

	private final TabSelectionButton tabSelection = new TabSelectionButton(this::tabChanged);
	private EditBox filterField;
	private StringWidget tabName;
	private CollapsibleTreeLayout treeLayout;
	private CustomScrollableLayout scrollLayout;
	private CustomScrollableLayout selectedScroll;
	private LinearLayout topLayout;
	private LinearLayout topRightLayout;
	private LinearLayout selectedTasksLayout = LinearLayout.vertical().spacing(4);

	private final CreatorSuite creatorSuite;

	private final List<TaskWidget> visibleTasks = new ArrayList<>();

	private final Map<TaskId, TaskWithCount> selectedTasks = new HashMap<>();

	String currentFilter = "";

	public BingoCardTaskListScreen(CreatorSuite creatorSuite) {
		super(Component.empty());

		this.creatorSuite = creatorSuite;

		// ui setup
		this.filterField = new EditBox(Minecraft.getInstance().font, 0, 0, 85, 14, Component.nullToEmpty(""));
		filterField.setBordered(true);
		filterField.setVisible(true);
		filterField.setCanLoseFocus(false);
		filterField.setFocused(true);
		filterField.setResponder(this::applyFilter);

		this.tabName = new StringWidget(tabSelection.getSelectedTab().name().copy().withStyle(ScreenHelper.INVENTORY_STYLE), font);

		topLayout = LinearLayout.horizontal();
		topLayout.addChild(tabName, LayoutSettings.defaults().padding(3).alignVerticallyMiddle());
		topLayout.addChild(ImageWidget.sprite(12, 12, SEARCH_ICON), LayoutSettings.defaults().padding(2).paddingRight(1));
		topLayout.addChild(filterField, LayoutSettings.defaults().alignVerticallyMiddle().padding(3));
		topLayout.addChild(tabSelection, LayoutSettings.defaults().paddingRight(3));
		topLayout.arrangeElements();

		topRightLayout = LinearLayout.horizontal();
		topRightLayout.addChild(new StringWidget(Component.literal("Selected").withStyle(ScreenHelper.INVENTORY_STYLE), font),
				LayoutSettings.defaults().paddingTop(5).paddingBottom(6).paddingHorizontal(24));

		treeLayout = new CollapsibleTreeLayout(font);
	}

	@Override
	protected void init() {
		int heightLeft = height - TAB_HEIGHT - 55;

		applyFilter(currentFilter);

		LinearLayout mainLayout = LinearLayout.vertical().spacing(3);
		LinearLayout centerLayout = LinearLayout.horizontal().spacing(6);

		AbstractScrollArea.ScrollbarSettings settings = new AbstractScrollArea.ScrollbarSettings(SCROLLER, SCROLLER_DISABLED, SCROLLER_BACKGROUND, SCROLLER_WIDTH, SCROLLER_HEIGHT, 24, false);
		scrollLayout = new CustomScrollableLayout(0,
				0,
				SLOT_WIDTH * 10 + SCROLLER_WIDTH + 8, 3 + SCROLLER_WIDTH, heightLeft, treeLayout, settings);
		scrollLayout.arrangeElements();
		scrollLayout.refreshScrollAmount();

		LinearLayout leftLayout = LinearLayout.vertical();

		tabName.setWidth(scrollLayout.getWidth() - topLayout.getWidth() + tabName.getWidth());
		topLayout.arrangeElements();
		leftLayout.addChild(topLayout, LayoutSettings.defaults().paddingVertical(4));
		leftLayout.addChild(scrollLayout);

		LinearLayout rightLayout = LinearLayout.vertical();
		selectedScroll = new CustomScrollableLayout(((width - scrollLayout.getWidth()) / 2) + scrollLayout.getWidth() + 8, scrollLayout.getY(),
				90, SCROLLER_WIDTH, heightLeft, selectedTasksLayout, settings);
		rightLayout.addChild(topRightLayout, LayoutSettings.defaults().paddingVertical(4));
		rightLayout.addChild(selectedScroll);

		centerLayout.addChild(leftLayout);
		centerLayout.addChild(rightLayout);
		mainLayout.addChild(centerLayout);

		LinearLayout buttonLayout = LinearLayout.horizontal().spacing(55);
		buttonLayout.addChild(Button.builder(Component.literal("Save & Exit"), this::savePressed).build());
		buttonLayout.addChild(Button.builder(Component.literal("Cancel"), this::cancelPressed).build(), LayoutSettings.defaults().alignHorizontallyCenter());
		mainLayout.addChild(buttonLayout, LayoutSettings.defaults().paddingTop(10));
		mainLayout.arrangeElements();

		mainLayout.setX((width - mainLayout.getWidth()) / 2);
		mainLayout.setY((height - mainLayout.getHeight()) / 2);
		mainLayout.visitWidgets(this::addRenderableWidget);
	}

	public void tabChanged(int newIndex, TabSelectionButton.TaskTab newTab) {
		applyFilter("");
		tabName.setMessage(newTab.name().copy().withStyle(ScreenHelper.INVENTORY_STYLE));
	}

	public void standardLayout(List<TaskWidget> widgets, int columns) {
		treeLayout.clear();

		Map<String, List<TaskWidget>> widgetByCategory = new HashMap<>();
		for (TaskWidget w : widgets) {
			if (!w.task().passesFilter(currentFilter)) {
				continue;
			}

			String cat = w.task().category();
			widgetByCategory.computeIfAbsent(cat, category -> new ArrayList<>());
			widgetByCategory.get(cat).add(w);
		}

		for (String category : widgetByCategory.keySet()) {
			LinearLayout padding = LinearLayout.vertical();
			GridLayout contents = new GridLayout();
			padding.addChild(contents, LayoutSettings.defaults().padding(3));

			for (int i = 0; i < widgetByCategory.get(category).size(); i++) {
				int col = i % columns;
				int row = i / columns;
				TaskWidget widget = widgetByCategory.get(category).get(i);
				widget.select(selectedTasks.containsKey(widget.task().id()));
				contents.addChild(widgetByCategory.get(category).get(i), row, col);
			}

			treeLayout.addTopLevelNode(category.isEmpty() ? Component.empty() : Component.literal(category), padding);
		}
	}

	public void buildItemWidgets(int colCount) {
		visibleTasks.clear();
		List<TaskWidget> widgets = new ArrayList<>();
		for (CreativeModeTab tab : creatorSuite.itemsPerTab.keySet()) {
			widgets.addAll(creatorSuite.itemsPerTab.get(tab).stream()
					.map(this::createWidget).toList());
		}

		visibleTasks.addAll(widgets);
		standardLayout(widgets, colCount);
	}

	public void buildStatisticWidgets(int colCount) {
		visibleTasks.clear();
		List<TaskWidget> widgets = creatorSuite.allStatistics.values().stream()
				.map(this::createWidget).toList();
		visibleTasks.addAll(widgets);
		standardLayout(widgets, colCount);
	}

	public void buildAdvancementWidgets(int colCount) {
		visibleTasks.clear();
		List<TaskWidget> widgets = new ArrayList<>();
		for (Key key : creatorSuite.taskSupplier().advancements().keySet()) {
			AdvancementNode node = creatorSuite.taskSupplier().advancements().get(key);
			if (!node.hasDisplay()) {
				continue;
			}
			String category = creatorSuite.findAdvancementRoot(key);
			TaskDefinition def = new TaskDefinition(new TaskId.Advancement(key), node.displayName(), node.displayDescription(), node.displayIcon(), category,1);
			widgets.add(this.createWidget(def));
		}

		visibleTasks.addAll(widgets);
		standardLayout(widgets, colCount);
	}

	public TaskWidget createWidget(TaskId id) {
		return createWidget(creatorSuite.getTaskById(id));
	}

	public TaskWidget createWidget(TaskDefinition definition) {
		return new TaskWidget(definition, font, widget -> {
			TaskId id = widget.task().id();
			if (widget.isSelected() && !selectedTasks.containsKey(id)) {
				selectedTasks.put(id, new TaskWithCount(widget.task(), 1));
				updateSelectedTasks();
			} else if (!widget.isSelected()) {
				selectedTasks.remove(id);
				updateSelectedTasks();
			}
		});
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public void applyFilter(String filter) {
		currentFilter = filter;

		int colCount = 10;
		switch (tabSelection.getSelectedTab().type()) {
			case ITEM -> buildItemWidgets(colCount);
			case ADVANCEMENT -> buildAdvancementWidgets(colCount);
			case STATISTIC -> buildStatisticWidgets(colCount);
		};

		if (scrollLayout != null) {
			scrollLayout.setScrollAmount(0.0);
		}
	}

	public void updateSelectedTasks() {
		selectedScroll.visitWidgets(this::removeWidget);
		selectedTasksLayout.removeChildren();
		for (TaskId id : selectedTasks.keySet()) {
			selectedTasksLayout.addChild(new SelectedTaskComponent(selectedTasks.get(id), font));
		}

		selectedScroll.arrangeElements();
		selectedScroll.visitWidgets(this::addRenderableWidget);
	}

	public void loadList(CustomList list) {
		selectedTasks.clear();
		for (ConfiguredTask task : list.tasks()) {
			TaskDefinition def = creatorSuite.getTaskById(task.id());
			selectedTasks.put(task.id(), new TaskWithCount(def, task.count()));
		}
		updateSelectedTasks();
		applyFilter("");
	}

	public void savePressed(Button btn) {
		Minecraft.getInstance().gui.setScreen(null);
	}

	public void cancelPressed(Button btn) {
		Minecraft.getInstance().gui.setScreen(null);
	}

	@Override
	public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		ScreenHelper.extractInnerInventoryBackground(graphics, topLayout.getRectangle());
		ScreenHelper.extractInnerInventoryBackground(graphics, topRightLayout.getRectangle());

		super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);

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
		for (TaskWidget visibleTask : visibleTasks) {
			if (visibleTask.isHovered()) {
				hoveredIndex = index;
				hoveredTask = visibleTask;
				break;
			}
			index++;
		}

		if (hoveredIndex != -1) {
			int count = 0;
			if (selectedTasks.containsKey(hoveredTask.task().id())) {
				count = selectedTasks.get(hoveredTask.task().id()).count();
			}
			TaskTooltipComponent tooltipComponent = new TaskTooltipComponent(new TaskWithCount(hoveredTask.task(), count));

			int slotX = hoveredTask.getX();
			int slotY = hoveredTask.getY();
			graphics.tooltip(font, List.of(tooltipComponent), slotX - tooltipComponent.getWidth(font) / 2, slotY - tooltipComponent.getHeight(font) + 9, DefaultTooltipPositioner.INSTANCE, null);
		}
	}
}
