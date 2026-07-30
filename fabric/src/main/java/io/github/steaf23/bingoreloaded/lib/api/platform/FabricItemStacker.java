package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.format.TextColor;

public class FabricItemStacker implements PlatformItemStacker {

	@Override
	public StackHandle createStack(ItemType type, int amount) {
		return null;
	}

	@Override
	public StackHandle createStackFromBytes(byte[] bytes) {
		return null;
	}

	@Override
	public StackHandle createStackFromTemplate(ItemTemplate template, boolean hideAttributes) {
		return null;
	}

	@Override
	public byte[] createBytesFromStack(StackHandle stack) {
		return new byte[0];
	}

	@Override
	public StackHandle colorItemStack(StackHandle stack, TextColor color) {
		return null;
	}

	@Override
	public ItemTemplate createPlayerHeadTemplate(PlayerHandle player) {
		return null;
	}
}
