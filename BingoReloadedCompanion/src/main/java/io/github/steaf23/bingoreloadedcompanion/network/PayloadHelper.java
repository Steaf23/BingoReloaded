package io.github.steaf23.bingoreloadedcompanion.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.nio.charset.Charset;

public class PayloadHelper {

	public static final StreamCodec<ByteBuf, String> STRING_CODEC = StreamCodec.of(
			(buf, str) -> {
				buf.writeShort(str.length());
				buf.writeBytes(str.getBytes(Charset.defaultCharset()));
			}, (buf) -> {
				int len = buf.readShort();
				return buf.readBytes(len).toString(Charset.defaultCharset());
			}
	);

	public static final StreamCodec<ByteBuf, Identifier> ID_CODEC = STRING_CODEC.map(Identifier::parse, Identifier::toString);
}
