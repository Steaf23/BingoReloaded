package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemTypeFabric;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AdvancementHandleFabric implements AdvancementHandle {

	private final AdvancementEntry advancement;

	public AdvancementHandleFabric(Identifier id, MinecraftServer server) {
		this.advancement = server.getAdvancementLoader().get(id);
	}

	@Override
	public ItemType displayIcon() {
		if (advancement.value().display().isEmpty()) {
			return ItemType.AIR;
		}
		return new ItemTypeFabric(advancement.value().display().get().getIcon().getItem());
	}

	@Override
	public @NotNull Key key() {
		return FabricTypes.keyFromId(advancement.id());
	}

	public AdvancementEntry handle() {
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
