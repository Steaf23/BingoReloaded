package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityTypeFabric implements EntityType {

	private final net.minecraft.world.entity.EntityType<?> type;

	public EntityTypeFabric(net.minecraft.world.entity.EntityType<?> type) {
		this.type = type;
	}

	@Override
	public @NotNull Key key() {
		return FabricTypes.keyFromId(BuiltInRegistries.ENTITY_TYPE.getKey(type));
	}

	public net.minecraft.world.entity.EntityType<?> handle() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityTypeFabric other) {
			return type.equals(other.type);
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type);
	}
}
