package io.github.steaf23.bingoreloadedcompanion.network;

import net.minecraft.network.RegistryByteBuf;

public class PayloadHelper {

	public static String readString(RegistryByteBuf buf) {
		short strSize = buf.readShort();
		byte[] bytes = new byte[strSize];
		for (int j = 0; j < strSize; j++) {
			bytes[j] = buf.readByte();
		}
		return new String(bytes);
	}

	static void writeString(String text, RegistryByteBuf stream) {
		byte[] bytes = text.getBytes();
		stream.writeShort(bytes.length);
		stream.writeBytes(bytes);
	}
}
