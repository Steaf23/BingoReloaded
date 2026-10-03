package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import io.github.steaf23.bingoreloaded.protocol.data.card.CustomList;
import io.github.steaf23.bingoreloaded.protocol.data.task.ConfiguredTask;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloadedcompanion.card.taskdata.TaskWithCount;
import io.github.steaf23.bingoreloadedcompanion.client.core.CollapsibleTreeLayout;
import io.github.steaf23.bingoreloadedcompanion.client.core.CustomScrollableLayout;
import io.github.steaf23.bingoreloadedcompanion.client.core.DeferredExtraction;
import io.github.steaf23.bingoreloadedcompanion.client.creator.CreatorSuite;
import io.github.steaf23.bingoreloadedcompanion.client.core.ElementPanel;
import io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.selection.SelectedTaskComponent;
import io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.selection.TaskSelection;
import io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist.selection.TaskSelectionList;
import io.github.steaf23.bingoreloadedcompanion.client.util.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaskPickerScreen extends Screen {

	public enum DisplayMode {
		ITEMS(Component.literal("Items").withStyle(ScreenHelper.INVENTORY_STYLE)),
		ADVANCEMENTS(Component.literal("Advancements").withStyle(ScreenHelper.INVENTORY_STYLE)),
		STATISTICS(Component.literal("Statistics").withStyle(ScreenHelper.INVENTORY_STYLE)),
		TAGS(Component.literal("Tags").withStyle(ScreenHelper.INVENTORY_STYLE)),
		;

		public final Component title;

		DisplayMode(Component title) {
			this.title = title;
		}
	}

	private static final Identifier SEARCH_ICON = Identifier.withDefaultNamespace("icon/search");
	private static final Identifier CURSOR_ICON = Identifier.parse("bingoreloadedcompanion:tag");

	private static final int TAB_HEIGHT = 20;
	private static final int SLOT_WIDTH = 24;
	private static final int TASKS_PER_ROW = 10;
	private static final int BUTTONS_HEIGHT = 55;

	// data
	private String listName = "";
	private final CreatorSuite suite;
	private final TaskSelection selection = new TaskSelection(this::taskSelectionUpdated);
	private final PickerContent lookup;

	private DisplayMode itemDisplayMode = DisplayMode.ITEMS;

	//display
	public List<ElementPanel> panels = new ArrayList<>();
	private final StringWidget tabName;
	private final EditBox filterField;

	private TaskSelectionList selectionList;
	private CollapsibleTreeLayout tasksTreeLayout;
	private boolean updateSelectedTasks = false;
	private CustomScrollableLayout pickerLayout = null;
	private CustomScrollableLayout selectedLayout = null;
	private TaskTypeOptionButton.TaskTab selectedTaskTab = TaskTypeOptionButton.firstTab();

	public TaskPickerScreen(CreatorSuite suite) {
		super(Component.empty());
		this.suite = suite;

		Map<TaskId, PickerContent.WidgetFactory> allWidgets = new HashMap<>();

		suite.iterateAllTasks((id, namedTask) -> {
			allWidgets.put(id, () -> new TaskWidget(this, namedTask.def(), font, namedTask.name(), this::taskClicked));
		});

		this.lookup = new PickerContent(allWidgets, suite.allTags);

		this.filterField = new EditBox(Minecraft.getInstance().font, 0, 0, 85, 14, Component.nullToEmpty(""));
		filterField.setBordered(true);
		filterField.setVisible(true);
		filterField.setCanLoseFocus(false);
		filterField.setFocused(true);
		filterField.setResponder(this::filterTextChanged);

		this.tabName = new StringWidget(itemDisplayMode.title, font);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		updateSelectedTasks = false;

		boolean success = super.mouseClicked(event, doubleClick);
		if (updateSelectedTasks) {
			populateSelectedTasks();
		}
		return success;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		updateSelectedTasks = false;

		boolean success = super.mouseReleased(event);
		if (updateSelectedTasks) {
			populateSelectedTasks();
		}
		return success;
	}

	@Override
	protected void init() {
		super.init();

		panels.clear();

		FrameLayout layout = new FrameLayout(width, height);
		LinearLayout mainLayout = LinearLayout.vertical();
		GridLayout contents = new GridLayout().rowSpacing(4).columnSpacing(6);
		LayoutElement taskHeader = initTasksHeader();
		LayoutElement selectionHeader = initSelectionHeader();
		LayoutElement visibleTasks = initVisibleTasks();
		LayoutElement selectedTasks = initSelectedTasks();
		contents.addChild(taskHeader, 0, 0);
		contents.addChild(selectionHeader, 0, 1);
		contents.addChild(visibleTasks, 1, 0);
		contents.addChild(selectedTasks, 1, 1);
		mainLayout.addChild(contents, LayoutSettings.defaults().alignHorizontallyCenter().alignVerticallyMiddle());
		mainLayout.addChild(initButtons());

		layout.addChild(mainLayout, LayoutSettings.defaults().alignHorizontallyCenter().alignVerticallyMiddle());

		layout.arrangeElements();
		layout.visitWidgets(this::addRenderableWidget);
	}

	public void populateVisibleTasks(DisplayMode type) {
		TaskListFiller filler = new TaskListFiller(suite, lookup, font, TASKS_PER_ROW);
		switch (type) {
			case ITEMS -> filler.createItemLayout(tasksTreeLayout);
			case ADVANCEMENTS -> filler.createAdvancementLayout(tasksTreeLayout);
			case STATISTICS -> filler.createStatisticLayout(tasksTreeLayout);
			case TAGS -> {
				filler.createTagsLayout(tasksTreeLayout, this::tagClicked);
				lookup.selectTag(lookup.getFirstTag().orElse(null));
			}
		}

		for (TaskId id : selection.keySet()) {
			updateTask(id);
		}
	}

	public void populateSelectedTasks() {
		selectionList.clear();
		if (selectedLayout != null) {
			selectedLayout.setScrollAmount(0.0D);
		}
		for (TaskId id : selection.keySet().stream()
				.filter(id -> {
					var def = suite.getTaskById(id);
					return def.def().passesFilter(lookup.nameFilter) && !TaskListFiller.filterOutCategory(lookup.categoryFilter, def.def().category());
				})
				.sorted(Comparator.comparingInt(suite::taskIndex))
				.toList()) {
			TaskWithCount countable = selection.get(id);
			SelectedTaskComponent task = new SelectedTaskComponent(countable, font, lookup, this::selectedTaskUIUpdated);
			task.setEditMode(itemDisplayMode == DisplayMode.TAGS ? TaskEditMode.TAG : TaskEditMode.COUNT);
			selectionList.addComp(task);
		}
		selectionList.arrangeElements();
	}

	public void refreshVisibleTasksOrTags(DisplayMode type) {
		tasksTreeLayout.clear();
		pickerLayout.setScrollAmount(0.0D);
		populateVisibleTasks(type);
	}

	public LayoutElement initTasksHeader() {
		FrameLayout headerLayout = new FrameLayout(SLOT_WIDTH * TASKS_PER_ROW + CustomScrollableLayout.SCROLLER_WIDTH + 8, TAB_HEIGHT);
		panels.add((graphics) ->
				ScreenHelper.extractInnerInventoryBackground(graphics, headerLayout.getRectangle()));

		headerLayout.addChild(tabName, LayoutSettings.defaults().padding(3).alignVerticallyMiddle().alignHorizontallyLeft());

		LinearLayout inner = LinearLayout.horizontal();
		inner.addChild(ImageWidget.sprite(12, 12, SEARCH_ICON), LayoutSettings.defaults().padding(2).paddingRight(1));
		inner.addChild(filterField, LayoutSettings.defaults().alignVerticallyMiddle().padding(3));

		TaskTypeOptionButton tabSelection = new TaskTypeOptionButton(selectedTaskTab == null ? 0 : selectedTaskTab.index(), this::taskTabChanged);
		inner.addChild(tabSelection, LayoutSettings.defaults().paddingRight(3));
		headerLayout.addChild(inner, LayoutSettings.defaults().alignHorizontallyRight());
		return headerLayout;
	}

	public LayoutElement initSelectionHeader() {
		FrameLayout headerLayout = new FrameLayout(106, TAB_HEIGHT);
		panels.add((graphics) ->
				ScreenHelper.extractInnerInventoryBackground(graphics, headerLayout.getRectangle()));

		headerLayout.addChild(new StringWidget(Component.literal("Selected").withStyle(ScreenHelper.INVENTORY_STYLE), font), LayoutSettings.defaults().padding(3).alignVerticallyMiddle().alignHorizontallyLeft());

		EditModeOptionButton tabSelection = new EditModeOptionButton(itemDisplayMode == DisplayMode.TAGS ? 1 : 0, this::modeTabChanged);
		headerLayout.addChild(tabSelection, LayoutSettings.defaults().alignHorizontallyRight().paddingRight(2));
		return headerLayout;
	}

	public LayoutElement initVisibleTasks() {
		tasksTreeLayout = new CollapsibleTreeLayout(font);
		populateVisibleTasks(itemDisplayMode);
		pickerLayout = new CustomScrollableLayout(SLOT_WIDTH * TASKS_PER_ROW + CustomScrollableLayout.SCROLLER_WIDTH + 8, height - TAB_HEIGHT - BUTTONS_HEIGHT, tasksTreeLayout);
		return pickerLayout;
	}

	public LayoutElement initSelectedTasks() {
		selectionList = new TaskSelectionList();
		populateSelectedTasks();
		selectedLayout = new CustomScrollableLayout(106, height - TAB_HEIGHT - BUTTONS_HEIGHT, selectionList);
		return selectedLayout;
	}

	public LayoutElement initButtons() {
		FrameLayout layout = new FrameLayout(width, BUTTONS_HEIGHT - 15);
		LinearLayout buttonLayout = LinearLayout.horizontal().spacing(40);
		buttonLayout.addChild(Button.builder(Component.literal("Save & Exit"), this::savePressed).build());
		buttonLayout.addChild(Button.builder(Component.literal("Cancel"), this::cancelPressed).build());
		layout.addChild(buttonLayout, LayoutSettings.defaults().alignVerticallyMiddle().alignHorizontallyCenter());
		return layout;
	}

	public void loadList(CustomList list) {
		listName = list.name();
		for (ConfiguredTask item : list.tasks()) {
			selection.putNewTask(suite.getTaskById(item.id()), item.count());
		}
		populateSelectedTasks();

		for (TaskId id : selection.keySet()) {
			updateTask(id);
		}
	}

	private void filterTextChanged(String newText) {
		String cleaned = newText.toLowerCase().replace("_", " ");
		// Filter by category when starting with @
		if (cleaned.startsWith("@")) {
			lookup.nameFilter = "";
			lookup.categoryFilter = cleaned.substring(1);
		} else {
			lookup.nameFilter = cleaned.toLowerCase().replace("_", " ");
			lookup.categoryFilter = "";
		}

		populateSelectedTasks();
		refreshVisibleTasksOrTags(itemDisplayMode);
	}

	private void taskTabChanged(int newIdx, TaskTypeOptionButton.TaskTab newTab) {
		selectedTaskTab = newTab;
		if (itemDisplayMode == DisplayMode.TAGS) {
			return;
		}

		changeDisplayMode(switch (newTab.type()) {
			case ITEM -> DisplayMode.ITEMS;
			case ADVANCEMENT -> DisplayMode.ADVANCEMENTS;
			case STATISTIC -> DisplayMode.STATISTICS;
		});
	}

	private void changeDisplayMode(DisplayMode displayMode) {
		filterField.setValue("");
		tabName.setMessage(displayMode.title);

		if (displayMode == DisplayMode.TAGS && itemDisplayMode != DisplayMode.TAGS) {
			// switch to tags
			for (SelectedTaskComponent component : selectionList.selectedTasks) {
				component.setEditMode(TaskEditMode.TAG);
			}
		} else if (displayMode != DisplayMode.TAGS && itemDisplayMode == DisplayMode.TAGS) {
			// switch to tasks
			for (SelectedTaskComponent component : selectionList.selectedTasks) {
				component.setEditMode(TaskEditMode.COUNT);
			}
		}

		itemDisplayMode = displayMode;
		refreshVisibleTasksOrTags(itemDisplayMode);
	}

	private void modeTabChanged(int newIdx, EditModeOptionButton.TaskTab newTab) {
		if (newIdx == 0) {
			changeDisplayMode(switch (selectedTaskTab.type()) {
				case ITEM -> DisplayMode.ITEMS;
				case ADVANCEMENT -> DisplayMode.ADVANCEMENTS;
				case STATISTIC -> DisplayMode.STATISTICS;
			});
		} else {
			changeDisplayMode(DisplayMode.TAGS);
		}
	}

	private boolean taskClicked(TaskWidget widget, boolean increment) {
		return selection.updateForTask(suite, widget.task().id(), increment);
	}

	private void tagClicked(TagInfo.Tag tag) {
		lookup.selectTag(tag);
	}

	private void selectedTaskUIUpdated(SelectedTaskComponent task) {
		TaskId id = task.task().task().id();
		boolean removed = task.task().count() <= 0;
		if (removed) {
			selection.remove(id);
		} else {
			selection.updateSelected(task.task());
		}
		updateSelectedTasks = true;

		for (TaskWidget widget : lookup.getTasksWithId(id)) {
			widget.select(!removed);
		}
	}

	private void taskSelectionUpdated(TaskId id) {
		populateSelectedTasks();
	}

	private void savePressed(Button btn) {
		suite.closeScreen(this);
		suite.saveList(new CustomList(listName, selection.keySet().stream()
				.map(key -> new ConfiguredTask(key, selection.get(key).count(), selection.get(key).tags()))
				.toList(), false));
	}

	private void cancelPressed(Button btn) {
		suite.closeScreen(this);
	}

	public void updateTask(TaskId id) {
		lookup.getTasksWithId(id).forEach(task -> {
			task.select(selection.containsKey(id));
		});
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		for (ElementPanel panel : panels) {
			panel.extract(graphics);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);

		if (itemDisplayMode == DisplayMode.TAGS && lookup.selectedTag() != null) {
			graphics.requestCursor(CursorTypes.POINTING_HAND);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CURSOR_ICON, mouseX - 3, mouseY - 7, 24, 16, ScreenHelper.addAlphaToColor(lookup.selectedTag().color().value(), 255));
		}

		selectionList.visitWidgets(widget -> {
			if (widget instanceof DeferredExtraction deferred) {
				deferred.extractAtTheEnd(graphics, mouseX, mouseY, a);
			}
		});
	}

	public void extractTaskTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, TaskWidget widget) {
		int count = 0;
		Set<String> tags = Set.of();

		if (selection.containsKey(widget.task().id())) {
			TaskWithCount selected = selection.get(widget.task().id());
			count = selected.count();
			tags = selected.tags();
		}
		TaskWithCount countable = new TaskWithCount(widget.task(), count, widget::getName, tags);
		TaskTooltipComponent tooltipComponent = new TaskTooltipComponent(countable, countable.getName());

		ScreenHelper.extractTooltipComponent(graphics, font, tooltipComponent, mouseX, mouseY, pickerLayout.tooltipPositioner());
	}
}
