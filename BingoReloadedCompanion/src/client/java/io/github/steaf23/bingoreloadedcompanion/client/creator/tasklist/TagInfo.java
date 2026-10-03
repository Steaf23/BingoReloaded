package io.github.steaf23.bingoreloadedcompanion.client.creator.tasklist;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface TagInfo {

	record Tag(String name, TextColor color) {}

	Map<String, TextColor> allTags();

	@Nullable Tag selectedTag();

	default Tag tagByName(String name) {
		return new Tag(name, allTags().getOrDefault(name, NamedTextColor.WHITE));
	}

}
