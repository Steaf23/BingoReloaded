package io.github.steaf23.bingoreloaded.adapter;

import com.hypixel.hytale.protocol.MaybeBool;
import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class MessageHelper {
	public static Message fromComponentLike(ComponentLike componentLike) {
		Component component = componentLike.asComponent();

		Message root;
		if (component instanceof TextComponent text) {
			root = Message.raw(text.content());
		} else {
			root = Message.empty();
		}
		root.bold(component.hasDecoration(TextDecoration.BOLD));
		root.italic(component.hasDecoration(TextDecoration.ITALIC));
		TextColor color = component.color();
		if (color != null) {
			root.color(color.asHexString());
		}

		if (component.hasDecoration(TextDecoration.UNDERLINED)) {
			root.getFormattedMessage().underlined = MaybeBool.True;
		}


		for (Component child : component.children()) {
			root.insert(fromComponentLike(child));
		}
		return root;
	}
}
