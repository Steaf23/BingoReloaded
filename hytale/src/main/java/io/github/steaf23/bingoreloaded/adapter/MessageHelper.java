package io.github.steaf23.bingoreloaded.adapter;

import com.hypixel.hytale.server.core.Message;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class MessageHelper {
	public static Message fromComponentLike(ComponentLike componentLike) {
		return Message.raw(PlainTextComponentSerializer.plainText().serialize(componentLike.asComponent()));
	}
}
