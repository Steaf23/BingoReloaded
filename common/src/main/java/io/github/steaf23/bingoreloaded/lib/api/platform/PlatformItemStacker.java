package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.item.ItemType;
import io.github.steaf23.bingoreloaded.lib.api.item.StackHandle;
import io.github.steaf23.bingoreloaded.lib.api.player.PlayerHandle;
import io.github.steaf23.bingoreloaded.lib.item.ItemTemplate;
import net.kyori.adventure.text.format.TextColor;

public interface PlatformItemStacker {

	StackHandle createStack(ItemType type, int amount);
	StackHandle createStackFromBytes(byte[] bytes);
	StackHandle createStackFromTemplate(ItemTemplate template, boolean hideAttributes);
	byte[] createBytesFromStack(StackHandle stack);
	StackHandle colorItemStack(StackHandle stack, TextColor color);

	ItemTemplate createPlayerHeadTemplate(PlayerHandle player);
}
