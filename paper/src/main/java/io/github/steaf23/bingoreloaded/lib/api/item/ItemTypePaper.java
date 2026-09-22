package io.github.steaf23.bingoreloaded.lib.api.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ItemTypePaper implements ItemType {
	private final Material type;

	public ItemTypePaper(@Nullable Material type) {
		if (type == null) {
			type = Material.AIR;
		}
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
	public boolean isPreferredTool(StackHandle tool) {
		ItemStack stack = ((StackHandlePaper)tool).handle();

		Tool toolComp = stack.getData(DataComponentTypes.TOOL);
		if (toolComp == null) {
			return false;
		}

		BlockData block = type.createBlockData();
		return block.getDestroySpeed(stack) > toolComp.defaultMiningSpeed();
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

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type);
	}
}
