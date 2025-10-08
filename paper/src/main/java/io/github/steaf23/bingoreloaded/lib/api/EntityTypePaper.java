package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityTypePaper implements EntityType {

	private final org.bukkit.entity.EntityType type;

	public EntityTypePaper(org.bukkit.entity.EntityType type) {
		this.type = type;
	}

	@Override
	public @NotNull Key key() {
		return type.key();
	}

	public org.bukkit.entity.EntityType handle() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityTypePaper other) {
			return type.equals(other.type);
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type);
	}
}
