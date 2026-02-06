package io.github.steaf23.bingoreloaded.lib.api.item;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagTree;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			newStorage.getTree().getPayload(out);
			byte[] bytes = out.toByteArray();

			stack.withMetadata("custom_key", Codec.BYTE_ARRAY, bytes);
		} catch (IOException e) {
			ConsoleMessenger.bug("Custom Data (in setStorage()) exception", this);
			e.printStackTrace(); // You can log or rethrow this if needed
		}
	}

	@Override
	public @NotNull TagDataStorage getStorage() {
		byte[] bytes = stack.getFromMetadataOrNull("custom_data", Codec.BYTE_ARRAY);

		if (bytes == null) {
			return new TagDataStorage();
		}

		try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			TagTree tree = TagTree.fromPayload(in);
			return new TagDataStorage(tree);
		} catch (IOException e) {
			ConsoleMessenger.bug("Custom Data (in getStorage()) exception", this);
			e.printStackTrace();
			return new TagDataStorage();
		}
	}

	@Override
	public void setCooldown(Key cooldownGroup, double cooldownTimeSeconds) {

	}

	public ItemStack handle() {
		return stack;
	}
}
