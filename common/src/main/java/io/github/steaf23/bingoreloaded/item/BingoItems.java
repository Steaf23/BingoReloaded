package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import io.github.steaf23.bingoreloaded.player.BingoParticipant;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BingoItems {

	Map<Key, GameItem> items = new HashMap<>();

	public BingoItems() {
		addItem(new GoUpWand());
		addItem(new TeamPouch());
	}

	private void addItem(GameItem item) {
		items.put(item.key(), item);
	}

	public @Nullable StackHandle createStack(Key itemKey, @Nullable BingoParticipant participant) {
		if (!items.containsKey(itemKey)) {
			return null;
		}

		GameItem item = items.get(itemKey);
		ItemTemplate template = item.createForParticipant(participant)
				.setCompareKey(itemKey.asString());
		return template == null ? null : template.buildItem();
	}

	public @Nullable StackHandle createStack(GameItem item, @Nullable BingoParticipant participant) {
		return createStack(item.key(), participant);
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
