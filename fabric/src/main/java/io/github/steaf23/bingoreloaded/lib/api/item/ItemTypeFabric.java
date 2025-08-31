package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.NotNull;

public class ItemTypeFabric implements ItemType {

	private final Item item;

	public ItemTypeFabric(Item item) {
		this.item = item;
	}

	@Override
	public boolean isBlock() {
		return item instanceof BlockItem;
	}

	@Override
	public boolean isSolid() {
		if (item instanceof BlockItem block) {
			return block.getBlock().getDefaultState().isOpaque();
		}
		return false;
	}

	@Override
	public @NotNull Key key() {
		return FabricTypes.keyFromId(Registries.ITEM.getId(item));
	}
}
