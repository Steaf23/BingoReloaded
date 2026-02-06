package io.github.steaf23.bingoreloaded.lib.api.item;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemTypeHytale implements ItemType {

	private final String itemId;

	public ItemTypeHytale(String itemId) {
		this.itemId = itemId.toLowerCase();
	}

	@Override
	public boolean isBlock() {
		return false;
	}

	@Override
	public boolean isAir() {
		return itemId.equals("Empty");
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemTypeHytale other) {
			return itemId.equals(other.itemId);
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(itemId);
	}
}
