package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist;

import io.github.steaf23.bingoreloaded.protocol.data.task.AdvancementNode;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskDefinition;
import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import io.github.steaf23.bingoreloadedcompanion.client.core.CollapsibleTreeLayout;
import io.github.steaf23.bingoreloadedcompanion.client.creator.CreatorSuite;
import net.kyori.adventure.key.Key;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class TaskListFiller {

	public final CreatorSuite suite;
	public final PickerContent widgetLookup;
	public final Font font;
	public final int columnCount;

	public TaskListFiller(CreatorSuite suite, PickerContent widgetLookup, Font font, int columnCount) {
		this.suite = suite;
		this.widgetLookup = widgetLookup;
		this.font = font;
		this.columnCount = columnCount;
		widgetLookup.clearCreatedWidgets();
	}

	public void createItemLayout(CollapsibleTreeLayout layout) {
		CreatorSuite.ORDERED_TABS.stream().map(BuiltInRegistries.CREATIVE_MODE_TAB::getValueOrThrow)
				.filter(t -> t.getType() != CreativeModeTab.Type.SEARCH)
				.forEach(tab -> {
					LinearLayout padding = LinearLayout.vertical();
					GridLayout contents = new GridLayout();
					padding.addChild(contents, LayoutSettings.defaults().padding(3));

					String category = tab.getDisplayName().getString();
					if (filterOutCategory(widgetLookup.categoryFilter, category)) {
						return;
					}

					int currentIndex = 0;
					for (TaskId id : suite.itemsPerTab.get(tab)) {
						int col = currentIndex % columnCount;
						int row = currentIndex / columnCount;

						TaskWidget widget = widgetLookup.createTask(id).orElse(null);
						if (widget == null || !widget.task().passesFilter(widgetLookup.nameFilter)) {
							continue;
						}
						contents.addChild(widget, row, col);
						currentIndex++;
					}

					if (currentIndex == 0) {
						return;
					}

					layout.addTopLevelNode(category.isEmpty() ? Component.empty() : Component.literal(category), padding);
				});
	}

	public void createAdvancementLayout(CollapsibleTreeLayout layout) {
		List<TaskWidget> widgets = new ArrayList<>();
		for (Key key : suite.taskSupplier().advancements().keySet()) {
			AdvancementNode node = suite.taskSupplier().advancements().get(key);
			if (!node.hasDisplay()) {
				continue;
			}
			String category = suite.findAdvancementRoot(key);
			TaskDefinition def = new TaskDefinition(new TaskId.Advancement(key), node.displayName(), node.displayIcon(), category,1);
			widgetLookup.createTask(def.id()).ifPresent(widgets::add);
		}

		standardLayout(widgets, layout);
	}

	public void createStatisticLayout(CollapsibleTreeLayout layout) {
		List<TaskWidget> widgets = suite.allStatistics.keySet().stream()
				.map(widgetLookup::createTask)
				.flatMap(Optional::stream)
				.sorted(Comparator.comparing(w -> w.task().name()))
				.toList();
		standardLayout(widgets, layout);
	}

	public void createTagsLayout(CollapsibleTreeLayout layout, Consumer<TagInfo.Tag> tagClicked) {
		LinearLayout tagLayout = LinearLayout.vertical().spacing(3);
		for (String tagName : widgetLookup.allTags().keySet()) {
			TagInfo.Tag tag = widgetLookup.tagByName(tagName);
			tagLayout.addChild(new TagComboButton(tag, font, tagClicked));
		}
		layout.addTopLevelChild(tagLayout, LayoutSettings.defaults());
	}

	public void standardLayout(List<TaskWidget> widgets, CollapsibleTreeLayout layout) {
		Map<String, List<TaskWidget>> widgetByCategory = new HashMap<>();
		for (TaskWidget w : widgets) {
			if (!w.task().passesFilter(widgetLookup.nameFilter)) {
				continue;
			}

			String cat = w.task().category();
			if (filterOutCategory(widgetLookup.categoryFilter, cat)) {
				continue;
			}

			widgetByCategory.computeIfAbsent(cat, category -> new ArrayList<>());
			widgetByCategory.get(cat).add(w);
		}

		for (String category : widgetByCategory.keySet()) {
			LinearLayout padding = LinearLayout.vertical();
			GridLayout contents = new GridLayout();
			padding.addChild(contents, LayoutSettings.defaults().padding(3));

			for (int i = 0; i < widgetByCategory.get(category).size(); i++) {
				int col = i % columnCount;
				int row = i / columnCount;
				TaskWidget widget = widgetByCategory.get(category).get(i);
				contents.addChild(widget, row, col);
			}

			layout.addTopLevelNode(category.isEmpty() ? Component.empty() : Component.literal(category), padding);
		}
	}

	public static boolean filterOutCategory(String filter, String category) {
		return !filter.isEmpty() && !category.toLowerCase().replace("_", " ").contains(filter);
	}

}
