package io.github.steaf23.bingoreloaded.lib.api;

import io.github.steaf23.bingoreloaded.lib.data.core.DataStorage;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;

public interface StackHandle {

	ItemType type();

	int amount();

	Component customName();

	List<Component> lore();

	String compareKey();

	boolean isTool();

	boolean isArmor();

	void setAmount(int newAmount);

	StackHandle clone();

	void addStorage(String key, DataStorage storage);

	@Nullable DataStorage getStorage(String key);

	static StackHandle createFromTemplate(ItemTemplate template, boolean hideAttributes) {
		return PlatformResolver.get().createStackFromTemplate(template, hideAttributes);
	}

	static StackHandle create(ItemType type, int amount) {
		return PlatformResolver.get().createStack(type, amount);
	}

	static StackHandle create(ItemType type) {
		return PlatformResolver.get().createStack(type, 1);
	}

	static StackHandle empty() {
		return PlatformResolver.get().createStack(ItemType.AIR, 1);
	}

	static StackHandle deserializeBytes(byte[] bytes) {
		return PlatformResolver.get().createStackFromBytes(bytes);
	}

	static byte[] serializeBytes(StackHandle stack) {
		return PlatformResolver.get().createBytesFromStack(stack);
	}
}
