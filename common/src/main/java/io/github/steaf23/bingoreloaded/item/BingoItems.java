package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.api.BingoReloadedRuntime;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BingoItems {

	Map<Key, GameItem> items = new HashMap<>();
	private final BingoReloadedRuntime runtime;

	public BingoItems(BingoReloadedRuntime runtime) {
		this.runtime = runtime;
		addItem(new GoUpWand());
		addItem(new TeamShulker());
	}

	private void addItem(GameItem item) {
		items.put(item.key(), item);
	}

	public @Nullable StackHandle createStack(GameItem item) {
		return runtime.defaultStack(item);
	}

	public @Nullable StackHandle createStack(Key itemKey) {
		return runtime.defaultStack(getItem(itemKey));
	}

	@SuppressWarnings("PatternValidation")
	public @Nullable GameItem getItem(StackHandle stack) {
		return items.getOrDefault(BingoReloaded.resourceKey(stack.compareKey()), null);
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
