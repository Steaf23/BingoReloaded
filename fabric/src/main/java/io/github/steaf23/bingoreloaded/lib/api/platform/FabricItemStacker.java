package io.github.steaf23.bingoreloaded.lib.api.platform;

import com.mojang.serialization.DataResult;
import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandleFabric;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

//FIXME: Check if maybe just the item builder is enough for what we need to do/ reduce code duplication across platform implementations.
public class FabricItemStacker implements PlatformItemStacker {

	@Override
	public StackHandle createStack(ItemType type, int amount) {
		return new StackHandleFabric(ItemStack.EMPTY);
	}

	//FIXME: implement catch
	@Override
	public StackHandle createStackFromBytes(byte[] bytes) {
		var stream = new ByteArrayInputStream(bytes);

		CompoundTag tag = new CompoundTag();

		try {
			tag = NbtIo.readCompressed(stream, NbtAccounter.defaultQuota());
		} catch (IOException e) {

		}

		var pair = ItemStack.CODEC.decode(NbtOps.INSTANCE, tag);
		ItemStack itemStack = pair.getOrThrow().getFirst();
		return new StackHandleFabric(itemStack);
	}

	//FIXME: implement catch
	@Override
	public byte[] createBytesFromStack(StackHandle stack) {
		ItemStack itemStack = stackOf(stack);
		DataResult<Tag> result = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, itemStack);

		var	stream = new ByteArrayOutputStream();

		try {
			NbtIo.writeCompressed((CompoundTag) result.getOrThrow(), stream);
		} catch (IOException e) {

		}
		return stream.toByteArray();
	}

	@Override
	public StackHandle colorItemStack(StackHandle stack, TextColor color) {
		ItemStack item = stackOf(stack);
		item.set(DataComponents.DYED_COLOR, new DyedItemColor(color.value()));
		return stack;
	}

	@Override
	public ItemTemplate createPlayerHeadTemplate(PlayerHandle player) {
		return null;
	}

	private ItemStack stackOf(StackHandle handle) {
		return ((StackHandleFabric)handle).handle();
	}
}
