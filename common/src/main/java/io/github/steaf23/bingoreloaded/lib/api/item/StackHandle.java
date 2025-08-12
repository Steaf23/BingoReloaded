package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

	/**
	 * Clears and adds the newStorage to the stack using the custom_data component.
	 * @param newStorage Source of data to copy over.
	 */
	void setStorage(TagDataStorage newStorage);

	/**
	 * @return existing store containing custom item data
	 * 		   or a new store when the stack does not currently have any custom data assigned.
	 * 		   Could be an expensive operation in some implementations!
	 */
	@NotNull TagDataStorage getStorage();

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
