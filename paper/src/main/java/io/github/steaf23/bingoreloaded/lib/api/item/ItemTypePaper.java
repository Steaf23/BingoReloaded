package io.github.steaf23.bingoreloaded.lib.api.item;

import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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
		BlockData block = type.createBlockData();

		// NMS BEGIN
		net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
		net.minecraft.world.level.block.state.BlockState nmsState = ((CraftBlockData) block).getState();

		return nmsStack.isCorrectToolForDrops(nmsState);
		// NMS END
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
