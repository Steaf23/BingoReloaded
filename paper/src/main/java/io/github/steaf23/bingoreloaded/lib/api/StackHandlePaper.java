package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.util.PDCHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StackHandlePaper implements StackHandle {

	private final ItemStack stack;

	public StackHandlePaper(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public ItemType type() {
		return new ItemTypePaper(stack.getType());
	}

	@Override
	public int amount() {
		return stack.getAmount();
	}

	@Override
	public Component customName() {
		return stack.getItemMeta().displayName();
	}

	@Override
	public List<Component> lore() {
		return stack.getItemMeta().lore();
	}

	@Override
	public String compareKey() {
		return PDCHelper.getStringFromPdc(stack.getItemMeta().getPersistentDataContainer(), "compare_key");
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
		return new StackHandlePaper(stack.clone());
	}

	@Override
	public void addStorage(String key, DataStorage storage) {
		//FIXME: REFACTOR implement
	}

	@Override
	public @Nullable DataStorage getStorage(String key) {
		return null;
	}

	public ItemStack handle() {
		return stack;
	}
}
