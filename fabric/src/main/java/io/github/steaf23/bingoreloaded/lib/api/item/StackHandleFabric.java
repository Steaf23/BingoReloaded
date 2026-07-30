package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StackHandleFabric implements StackHandle {

	private final ItemStack stack;

	public StackHandleFabric(ItemStack stack) {
		this.stack = stack;
	}

	public ItemStack handle() {
		return stack;
	}

	@Override
	public ItemType type() {
		return null;
	}

	@Override
	public int amount() {
		return 0;
	}

	@Override
	public Component customName() {
		return null;
	}

	@Override
	public List<Component> lore() {
		return List.of();
	}

	@Override
	public String compareKey() {
		return "";
	}

	@Override
	public boolean isTool() {
		return false;
	}

	@Override
	public boolean isArmor() {
		return false;
	}

	@Override
	public void setAmount(int newAmount) {

	}

	@Override
	public StackHandle clone() {
		return null;
	}

	@Override
	public void setStorage(TagDataStorage newStorage) {

	}

	@Override
	public @NotNull TagDataStorage getStorage() {
		return null;
	}

	@Override
	public void setCooldown(Key cooldownGroup, double cooldownTimeSeconds) {

	}
}
