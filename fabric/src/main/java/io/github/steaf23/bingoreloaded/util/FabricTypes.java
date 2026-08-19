package io.github.steaf23.bingoreloaded.util;

import io.github.steaf23.bingoreloaded.lib.api.GlobalPosition;
import io.github.steaf23.bingoreloaded.lib.api.platform.PlatformServer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.modcommon.MinecraftAudiences;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

public class FabricTypes {

	public static MinecraftServer SERVER = null;

	public static Identifier idFromKey(Key key) {
		return MinecraftAudiences.asNative(key);
	}

	public static Key keyFromId(Identifier id) {
		return MinecraftAudiences.asAdventure(id);
	}

	public static Component toAdventureComponent(net.minecraft.network.chat.Component chatComponent) {
		return MinecraftServerAudiences.of(SERVER).asAdventure(chatComponent);
	}

	public static net.minecraft.network.chat.Component toNativeComponent(PlatformServer server, Component component) {
		return MinecraftServerAudiences.of(SERVER).asNative(component);
	}

	public static GlobalPosition fromBlockPos(Key dimension, BlockPos pos) {
		return new GlobalPosition(dimension, pos.getX(), pos.getY(), pos.getZ());
	}

	public static BlockPos toBlockPos(GlobalPosition pos) {
		return new BlockPos(pos.blockX(), pos.blockY(), pos.blockZ());
	}
}
