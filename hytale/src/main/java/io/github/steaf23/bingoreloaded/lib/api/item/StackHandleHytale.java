package io.github.steaf23.bingoreloaded.lib.api.item;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StackHandleHytale implements StackHandle {

	ItemStack stack;

	public StackHandleHytale(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public ItemType type() {
		return new ItemTypeHytale(stack.getItemId());
	}

	@Override
	public int amount() {
		return stack.getQuantity();
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
		return stack.getItemId();
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
