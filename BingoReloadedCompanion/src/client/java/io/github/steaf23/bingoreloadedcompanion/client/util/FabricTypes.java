package io.github.steaf23.bingoreloadedcompanion.client.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.modcommon.MinecraftAudiences;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.minecraft.resources.Identifier;

public class FabricTypes {

	public static Identifier idFromKey(Key key) {
		return MinecraftAudiences.asNative(key);
	}

	public static Key keyFromId(Identifier id) {
		return MinecraftAudiences.asAdventure(id);
	}

	public static Component toAdventureComponent(net.minecraft.network.chat.Component chatComponent) {
		return MinecraftClientAudiences.of().asAdventure(chatComponent);
	}

	public static net.minecraft.network.chat.Component toNativeComponent(Component component) {
		return MinecraftClientAudiences.of().asNative(component);
	}
}
