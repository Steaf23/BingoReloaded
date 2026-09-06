package io.github.steaf23.bingoreloadedcompanion.client.creator;

import io.github.steaf23.bingoreloaded.protocol.data.CreatorTaskSupplier;
import io.github.steaf23.bingoreloaded.protocol.data.task.AdvancementNode;
import io.github.steaf23.bingoreloaded.protocol.data.task.StatisticCategory;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.TaskTooltipComponent;
import io.github.steaf23.bingoreloadedcompanion.client.core.CollapsibleTreeLayout;
import io.github.steaf23.bingoreloadedcompanion.client.core.CustomScrollableLayout;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.kyori.adventure.key.Key;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	private LinearLayout topLayout;

	private final Map<CreativeModeTab, Set<TaskDefinition>> itemsPerTab = new HashMap<>();
	private final List<TaskDefinition> allStatistics = new ArrayList<>();

	private final CreatorTaskSupplier taskSupplier;

	private final List<TaskWidget> visibleTasks = new ArrayList<>();
	private final Map<TaskId, Integer> selectedTasks = new HashMap<>();

	String currentFilter = "";

	public BingoCardTaskListScreen(Component title, CreatorTaskSupplier tasks) {
		super(title);

		taskSupplier = tasks;
		BuiltInRegistries.CREATIVE_MODE_TAB.stream().forEach(tab -> {
			if (tab.getType() == CreativeModeTab.Type.SEARCH) {
				return;
			}
			tab.buildContents(new CreativeModeTab.ItemDisplayParameters(
					Minecraft.getInstance().player.connection.enabledFeatures(),
					Minecraft.getInstance().player.canUseGameMasterBlocks(),
					Minecraft.getInstance().player.level().registryAccess()));
			Set<TaskDefinition> items = tab.getDisplayItems().stream()
					.map(stack -> {
						Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
						return itemTask(Key.key(itemId.getNamespace(), itemId.value()), tab.getDisplayName().getString());
					})
					.collect(Collectors.toSet());
			itemsPerTab.put(tab, items);
		});

		allStatistics.addAll(tasks.statistics().stream()
				.flatMap(task -> {
					TaskId.Statistic stat = task.statistic();
					return switch (task.statistic().category().type) {
						case CUSTOM -> Stream.of(new TaskDefinition(
								stat,
								task.name(),
								"",
								task.customIcon(),
								stat.category().name(),
								64));
						case ITEM -> taskSupplier.items().stream()
								.map(item -> itemTaskFromStatistic(stat.category(), item, stat.category().name()));
						case BLOCK -> taskSupplier.validBlocks().stream()
								.map(item -> itemTaskFromStatistic(stat.category(), item, stat.category().name()));
						case ENTITY -> taskSupplier.validEntityTypes().stream()
								.map(item -> entityTaskFromStatistic(stat.category(), item, stat.category().name()));
					};
				}).toList());

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

		treeLayout = new CollapsibleTreeLayout(font);
	}

	@Override
	protected void init() {
		int heightLeft = height - TAB_HEIGHT - 30;
		int widthLeft = SLOT_WIDTH * 10 + SCROLLER_WIDTH + 3;

		int tasksStartX = (width - widthLeft) / 2;

		applyFilter(currentFilter);

		AbstractScrollArea.ScrollbarSettings settings = new AbstractScrollArea.ScrollbarSettings(SCROLLER, SCROLLER_DISABLED, SCROLLER_BACKGROUND, SCROLLER_WIDTH, SCROLLER_HEIGHT, 24, false);
		scrollLayout = new CustomScrollableLayout(tasksStartX,
				TAB_HEIGHT + 15,
				SLOT_WIDTH * 10 + SCROLLER_WIDTH + 8, 3 + SCROLLER_WIDTH, heightLeft, treeLayout, settings);
		scrollLayout.arrangeElements();
		scrollLayout.refreshScrollAmount();
		this.addRenderableWidget(scrollLayout);

		tabName.setWidth(scrollLayout.getWidth() - topLayout.getWidth() + tabName.getWidth() + 5);
		topLayout.arrangeElements();

		topLayout.setY(11);
		topLayout.visitWidgets(this::addRenderableWidget);

		topLayout.setX((width - scrollLayout.getWidth()) / 2);
	}

	public void tabChanged(int newIndex, TabSelectionButton.TaskTab newTab) {
		applyFilter("");
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
		for (CreativeModeTab tab : itemsPerTab.keySet()) {
			widgets.addAll(itemsPerTab.get(tab).stream()
					.map(this::createWidget).toList());
		}

		visibleTasks.addAll(widgets);
		standardLayout(widgets, colCount);
	}

	public void buildStatisticWidgets(int colCount) {
		visibleTasks.clear();
		List<TaskWidget> widgets = allStatistics.stream()
				.map(this::createWidget).toList();
		visibleTasks.addAll(widgets);
		standardLayout(widgets, colCount);
	}

	public void buildAdvancementWidgets(int colCount) {
		visibleTasks.clear();
		List<TaskWidget> widgets = new ArrayList<>();
		for (Key key : taskSupplier.advancements().keySet()) {
			AdvancementNode node = taskSupplier.advancements().get(key);
			if (!node.hasDisplay()) {
				continue;
			}
			String category = findAdvancementRoot(key);
			TaskDefinition def = new TaskDefinition(new TaskId.Advancement(key), node.displayName(), node.displayDescription(), node.displayIcon(), category,1);
			widgets.add(this.createWidget(def));
		}

		visibleTasks.addAll(widgets);
		standardLayout(widgets, colCount);
	}

	public @Nullable String findAdvancementRoot(Key advancement) {
		return advancement.asString().split("/")[0];
	}

	public TaskWidget createWidget(TaskDefinition definition) {
		return new TaskWidget(definition, font, widget -> {
			TaskId id = widget.task().id();
			if (widget.isSelected() && !selectedTasks.containsKey(id)) {
				selectedTasks.put(id, 1);
			} else if (!widget.isSelected()) {
				selectedTasks.remove(id);
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

	@Override
	public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
		ScreenHelper.extractInnerInventoryBackground(graphics, topLayout.getRectangle());

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
				count = selectedTasks.get(hoveredTask.task().id());
			}
			TaskTooltipComponent tooltipComponent = new TaskTooltipComponent(new TaskWithCount(hoveredTask.task(), count));

			int slotX = hoveredTask.getX();
			int slotY = hoveredTask.getY();
			graphics.tooltip(font, List.of(tooltipComponent), slotX - tooltipComponent.getWidth(font) / 2, slotY - tooltipComponent.getHeight(font) + 9, DefaultTooltipPositioner.INSTANCE, null);
		}
	}

	public TaskDefinition itemTask(Key key, String category) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(key.namespace(), key.value()));
		return new TaskDefinition(
				new TaskId.Item(key),
				"x1 " + item.getDescriptionId(),
				"item description",
				key,
				category,
				64);
	}


	public TaskDefinition itemTaskFromStatistic(StatisticCategory statCategory, Key itemKey, String category) {
		Item item = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(itemKey.namespace(), itemKey.value()));
		return new TaskDefinition(
				new TaskId.Statistic(Key.key("custom"), itemKey, statCategory),
				"STAT: " + item.getDescriptionId(),
				"stat description",
				itemKey,
				category,
				64
		);
	}

	public TaskDefinition entityTaskFromStatistic(StatisticCategory statCategory, Key entityKey, String category) {
		EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath(entityKey.namespace(), entityKey.value()));
		return new TaskDefinition(
				new TaskId.Statistic(Key.key("custom"), entityKey, statCategory),
				"STAT: " + entity.getDescriptionId(),
				"stat description",
				Key.key(entityKey.namespace(), entityKey.value() + "_spawn_egg"),
				category,
				64
		);
	}
}
