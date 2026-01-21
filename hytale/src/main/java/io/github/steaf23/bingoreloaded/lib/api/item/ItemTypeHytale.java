package io.github.steaf23.bingoreloaded.lib.api.item;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class ItemTypeHytale implements ItemType {

	private final String itemId;

	public ItemTypeHytale(String itemId) {
		this.itemId = itemId;
	}

	@Override
	public boolean isBlock() {
		return false;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public @NotNull Key key() {
		return Key.key(itemId);
	}

	public String itemId() {
		return itemId;
	}
}
