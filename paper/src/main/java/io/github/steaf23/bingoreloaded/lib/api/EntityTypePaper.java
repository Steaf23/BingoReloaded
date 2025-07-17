package io.github.steaf23.bingoreloaded.lib.api;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class EntityTypePaper implements EntityType {

	private final org.bukkit.entity.EntityType type;

	public EntityTypePaper(org.bukkit.entity.EntityType type) {
		this.type = type;
	}

	@Override
	public @NotNull Key key() {
		return type.key();
	}
}
