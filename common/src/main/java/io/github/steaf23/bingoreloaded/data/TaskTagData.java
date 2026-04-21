package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TaskTagData {

	public record TaskTag(TextColor color) {};

	private final DataAccessor data = BingoReloaded.getDataAccessor("data/tags");
	private final DataAccessor defaultData = BingoReloaded.getDataAccessor("data/default_tags");

	public Map<String, TaskTag> getDefaultTags() {
		Map<String, TaskTag> tags = new HashMap<>();
		for (String key : defaultData.getKeys()) {
			tags.put(key, defaultData.getSerializable(key, TaskTag.class));
		}
		return tags;
	}

	public Map<String, TaskTag> getAllTags() {
		var defaultTags = getDefaultTags();
		var customTags = getCustomTags();
		Map<String, TaskTag> allTags = new HashMap<>(defaultTags);
		for (String key : customTags.keySet()) {
			allTags.putIfAbsent(key, customTags.get(key));
		}
		return allTags;
	}

	public Map<String, TaskTag> getCustomTags() {
		Map<String, TaskTag> tags = new HashMap<>();
		for (String key : data.getKeys()) {
			tags.put(key, data.getSerializable(key, TaskTag.class));
		}
		return tags;
	}

	public void removeTag(String name) {
		data.erase(name);
		data.saveChanges();
	}

	public void addTag(@NotNull String tagKey, TaskTag tag) {
		data.setSerializable(tagKey, TaskTag.class, tag);
		data.saveChanges();
	}
}
