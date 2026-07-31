package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class ItemTypeFabric implements ItemType {

	private final Item item;

	public ItemTypeFabric(Item item) {
		this.item = item;
	}

	public Item handle() {
		return item;
	}

	@Override
	public boolean isBlock() {
		return item instanceof BlockItem;
	}

	@Override
	public boolean isSolid() {
		if (item instanceof BlockItem block) {
			return !block.getBlock().defaultBlockState().canBeReplaced();
		}
		return false;
	}

	@Override
	public @NotNull Key key() {
		return FabricTypes.keyFromId(BuiltInRegistries.ITEM.getKey(item));
	}
}
