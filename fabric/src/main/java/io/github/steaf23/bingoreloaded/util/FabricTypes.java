package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.lib.api.platform.FabricServer;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.modcommon.MinecraftAudiences;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
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
		return MinecraftServerAudiences.of(null).asAdventure(chatComponent);
	}

	public static net.minecraft.network.chat.Component toNativeComponent(PlatformServer server, Component component) {
		return MinecraftServerAudiences.of(((FabricServer)server).handle()).asNative(component);
	}
}
