package io.github.steaf23.bingoreloaded.lib.api.item;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ItemTypePaper implements ItemType {
	private final Material type;

	public ItemTypePaper(Material type) {
		this.type = type;
	}

	public static ItemTypePaper of(Material type) {
		return new ItemTypePaper(type);
	}

	@Override
	public boolean isBlock() {
		return type.isBlock();
	}

	@Override
	public boolean isSolid() {
		return type.isSolid();
	}

	@Override
	public @NotNull Key key() {
		return type.key();
	}

	public Material handle() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemTypePaper other) {
			return type.equals(other.type);
		}

		return super.equals(obj);
	}
}
