package io.github.steaf23.bingoreloaded.lib.item;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public interface ItemComponents {

	interface ItemComponent extends Keyed {}
	record DyedColor(TextColor color) implements ItemComponent {

		@Override
		public @NotNull Key key() {
			return Key.key("minecraft", "dyed_color");
		}
	};
}
