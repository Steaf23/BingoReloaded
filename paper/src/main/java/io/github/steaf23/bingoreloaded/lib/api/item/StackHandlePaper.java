package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.lib.api.ItemTypePaper;
import io.github.steaf23.bingoreloaded.lib.api.PlatformResolver;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagTree;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.lib.util.PDCHelper;
import io.github.steaf23.bingoreloaded.util.ItemHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class StackHandlePaper implements StackHandle {

	private final @NotNull ItemStack stack;

	private static final NamespacedKey CUSTOM_DATA_KEY = new NamespacedKey(
			PlatformResolver.get().getExtensionInfo().name().toLowerCase(),
			"custom");

	public StackHandlePaper(@Nullable ItemStack stack) {
		if (stack != null) {
			this.stack = stack;
		} else {
			this.stack = new ItemStack(Material.AIR);
		}
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
		if (stack.getItemMeta() == null) {
			return "";
		}
		return PDCHelper.getStringFromPdc(stack.getItemMeta().getPersistentDataContainer(), "compare_key");
	}

	@Override
	public boolean isTool() {
		return ItemHelper.isTool(stack.getType());
	}

	@Override
	public boolean isArmor() {
		return ItemHelper.isArmor(stack.getType());
	}

	@Override
	public void setAmount(int newAmount) {
		stack.setAmount(newAmount);
	}

	@Override
	public StackHandle clone() {
		return new StackHandlePaper(stack.clone());
	}

	@Override
	public void setStorage(TagDataStorage newStorage) {
		stack.editPersistentDataContainer(container -> {
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				newStorage.getTree().getPayload(out);
				byte[] bytes = out.toByteArray();

				container.set(CUSTOM_DATA_KEY, PersistentDataType.BYTE_ARRAY, bytes);
			} catch (IOException e) {
				ConsoleMessenger.bug("Custom Data (in setStorage()) exception", this);
				e.printStackTrace(); // You can log or rethrow this if needed
			}
		});
	}

	@Override
	public @Nullable TagDataStorage getStorage() {
		byte[] bytes = stack.getItemMeta().getPersistentDataContainer()
				.get(CUSTOM_DATA_KEY, PersistentDataType.BYTE_ARRAY);

		if (bytes == null) {
			return null;
		}

		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			TagTree tree = TagTree.fromPayload(in);
			return new TagDataStorage(tree);
		} catch (IOException e) {
			ConsoleMessenger.bug("Custom Data (in getStorage()) exception", this);
			e.printStackTrace();
			return null;
		}
	}

	public ItemStack handle() {
		return stack;
	}
}
