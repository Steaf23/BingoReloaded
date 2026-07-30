package io.github.steaf23.bingoreloaded.lib.api.inventory;

import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class InventoryTemplate {

	private StackHandle[] stacks;

	private final ArrayList<InventoryListener> listeners = new ArrayList<>();

	public InventoryTemplate(int slots) {
		this.stacks = new StackHandle[slots];
	}

	public InventoryTemplate(StackHandle[] stacks) {
		this.stacks = stacks;
	}

	public InventoryTemplate addListener(InventoryListener listener) {
		listeners.add(listener);
		return this;
	}

	public <T> @Nullable T firstListenerOfType(Class<T> type) {
		for (InventoryListener listener : listeners) {
			if (type.isInstance(listener)) {
				return type.cast(listener);
			}
		}
		return null;
	}

	public int size() {
		return stacks.length;
	}

	public void setItem(int index, StackHandle stack) {
		if (index >= stacks.length) {
			return;
		}

		StackHandle oldStack = stacks[index];
		stacks[index] = stack;
		for (InventoryListener listener : listeners) {
			listener.itemChanged(this, index, stack, oldStack);
		}
	}

	public @NotNull StackHandle getItem(int index) {
		if (index >= stacks.length) {
			return StackHandle.empty();
		}

		return stacks[index] == null ? StackHandle.empty() : stacks[index];
	}

	public void removeItem(StackHandle stack) {
		for (int i = 0; i < stacks.length; i++) {
			if (stack.equals(stacks[i])) {
				stacks[i] = StackHandle.empty();

				for (InventoryListener listener : listeners) {
					listener.itemChanged(this, i, stacks[i], stack);
				}
			}
		}
	}

	public void clearContents() {
		for (int i = 0; i < stacks.length; i++) {
			stacks[i] = StackHandle.empty();
		}

		for (InventoryListener listener : listeners) {
			listener.cleared(this);
		}
	}

	public StackHandle[] contents() {
		return Arrays.copyOf(stacks, stacks.length);
	}

	public void setContents(StackHandle[] stacks) {
		StackHandle[] oldStacks = Arrays.copyOf(this.stacks, this.stacks.length);
		this.stacks = stacks;

		for (InventoryListener listener : listeners) {
			listener.contentsReplaced(this, this.stacks, oldStacks);
		}
	}
}
