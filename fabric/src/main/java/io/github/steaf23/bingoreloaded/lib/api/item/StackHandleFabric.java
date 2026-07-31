package io.github.steaf23.bingoreloaded.lib.api.item;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagDataStorage;
import io.github.steaf23.bingoreloaded.lib.data.core.tag.TagTree;
import io.github.steaf23.bingoreloaded.lib.util.ConsoleMessenger;
import io.github.steaf23.bingoreloaded.util.FabricTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.UseCooldown;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
		return new ItemTypeFabric(stack.getItem());
	}

	@Override
	public int amount() {
		return stack.count();
	}

	@Override
	public Component customName() {
		return FabricTypes.toAdventureComponent(stack.getDisplayName());
	}

	@Override
	public List<Component> lore() {
		ItemLore lore = stack.get(DataComponents.LORE);
		if (lore == null)
		{
			return List.of();
		}

		return lore.lines().stream()
				.map(FabricTypes::toAdventureComponent)
				.toList();
	}

	@Override
	public String compareKey() {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data == null)
		{
			return "";
		}

		return data.copyTag().getString(BingoReloaded.resourceKey("compare_key").asString()).orElse("");
	}

	@Override
	public boolean isTool() {
		return stack.has(DataComponents.TOOL) || stack.has(DataComponents.WEAPON);
	}

	@Override
	public boolean isArmor() {
		return stack.has(DataComponents.EQUIPPABLE) && stack.get(DataComponents.EQUIPPABLE)
				.canBeEquippedBy(Holder.direct(EntityTypes.PLAYER));
	}

	@Override
	public void setAmount(int newAmount) {
		stack.setCount(newAmount);
	}

	@Override
	public StackHandle clone() {
		return new StackHandleFabric(stack.copy());
	}

	@Override
	public void setStorage(TagDataStorage newStorage) {
		stack.get(DataComponents.CUSTOM_DATA).update(tag -> {
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				newStorage.getTree().getPayload(out);
				byte[] bytes = out.toByteArray();

				tag.putByteArray(BingoReloaded.resourceKey("custom").asString(), bytes);
			} catch (IOException e) {
				ConsoleMessenger.bug("Custom Data (in setStorage()) exception", this);
				e.printStackTrace(); // You can log or rethrow this if needed
			}
		});
	}

	@Override
	public @NotNull TagDataStorage getStorage() {
		byte[] bytes = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
				.getByteArray(BingoReloaded.resourceKey("custom").asString())
				.orElse(null);

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
		stack.set(DataComponents.USE_COOLDOWN, new UseCooldown((float)cooldownTimeSeconds, Optional.of(FabricTypes.idFromKey(cooldownGroup))));
	}
}
