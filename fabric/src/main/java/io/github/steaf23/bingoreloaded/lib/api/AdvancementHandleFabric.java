package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypeFabric;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AdvancementHandleFabric implements AdvancementHandle {

	private final AdvancementHolder advancement;

	public AdvancementHandleFabric(Identifier id, MinecraftServer server) {
		this.advancement = server.getAdvancements().get(id);
	}

	public AdvancementHandleFabric(AdvancementHolder holder) {
		this.advancement = holder;
	}

	@Override
	public ItemType displayIcon() {
		if (advancement.value().display().isEmpty()) {
			return ItemType.AIR;
		}
		return new ItemTypeFabric(advancement.value().display().get().getIcon().item().value());
	}

	@Override
	public @NotNull Key key() {
		return FabricTypes.keyFromId(advancement.id());
	}

	public AdvancementHolder handle() {
		return advancement;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AdvancementHandleFabric other) {
			return key().equals(other.key());
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(advancement);
	}
}
