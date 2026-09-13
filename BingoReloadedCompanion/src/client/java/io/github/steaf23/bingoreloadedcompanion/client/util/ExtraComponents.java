package io.github.steaf23.bingoreloadedcompanion.client.util;


import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class ExtraComponents {
	public static final Component INPUT_LEFT_CLICK = inputButtonText(Component.keybind("key.attack"));
	public static final Component INPUT_RIGHT_CLICK = inputButtonText(Component.keybind("key.use"));
	// tutorial.punch_tree.description resolves to "Hold down %1" in English.
	public static final Component INPUT_SHIFT_CLICK = inputButtonText(Component.translatable("tutorial.punch_tree.description", Component.translatable("key.keyboard.left.shift")));

	static Component inputButtonText(Component buttonText) {
		return Component.empty()
				.append(Component.literal("<").withColor(TextColor.DARK_GRAY))
				.append(buttonText.copy().withColor(TextColor.GRAY))
				.append(Component.literal(">").withColor(TextColor.DARK_GRAY))
				.append(Component.literal(": ").withColor(TextColor.WHITE));
	}
}
