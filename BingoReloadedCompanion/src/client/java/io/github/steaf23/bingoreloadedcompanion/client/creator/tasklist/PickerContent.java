package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist;

import io.github.steaf23.bingoreloaded.protocol.data.task.TaskId;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PickerContent implements TagInfo {

	@FunctionalInterface
	public interface WidgetFactory {
		TaskWidget create();
	}

	public String nameFilter = "";
	public String categoryFilter = "";
	public Tag selected = null;

	private final Map<TaskId, WidgetFactory> widgetFactories;
	private final Map<TaskId, List<TaskWidget>> createdWidgets = new HashMap<>();
	private final Map<String, TextColor> tags;

	PickerContent(Map<TaskId, WidgetFactory> factories, Map<String, TextColor> allTags) {
		this.widgetFactories = factories;
		this.tags = allTags;
	}

	public Optional<TaskWidget> createTask(TaskId id) {
		Optional<TaskWidget> opt = Optional.of(widgetFactories.get(id)).map(WidgetFactory::create);
		createdWidgets.putIfAbsent(id, new ArrayList<>());
		opt.ifPresent(t -> createdWidgets.get(id).add(t));
		return opt;
	}

	public List<TaskWidget> getTasksWithId(TaskId id) {
		return createdWidgets.getOrDefault(id, List.of());
	}

	public void clearCreatedWidgets() {
		createdWidgets.clear();
	}

	public void selectTag(Tag tag) {
		selected = tag;
	}

	public Optional<Tag> getFirstTag() {
		return tags.keySet().stream().findFirst().map( s -> new Tag(s, tags.get(s)));
	}

	@Override
	public Map<String, TextColor> allTags() {
		return tags;
	}

	@Override
	public Tag selectedTag() {
		return selected;
	}
}
