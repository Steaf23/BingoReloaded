package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BingoItems {

	Map<Key, GameItem> items = new HashMap<>();
	Map<Key, ItemTemplate> templates = new HashMap<>();

	public BingoItems() {
		addItem(new GoUpWand());
	}

	private void addItem(GameItem item) {
		items.put(item.key(), item);
		templates.put(item.key(), item.defaultTemplate().setCompareKey(item.key()));
	}

	public @Nullable StackHandle createStack(Key itemKey) {
		ItemTemplate template = templates.getOrDefault(itemKey, null);

		return template == null ? null : template.buildItem();
	}

	public @Nullable StackHandle createStack(GameItem item) {
		return createStack(item.key());
	}

	@SuppressWarnings("PatternValidation")
	public @Nullable GameItem getItem(StackHandle stack) {
		return items.getOrDefault(Key.key(stack.compareKey()), null);
	}

	public @Nullable GameItem getItem(Key key) {
		return items.getOrDefault(key, null);
	}

	public boolean canItemLeaveInventory(StackHandle stack) {
		GameItem item = getItem(stack);

		if (item == null) {
			return true;
		}

		return item.canLeaveInventory();
	}
}
