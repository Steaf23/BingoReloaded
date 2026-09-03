package io.github.steaf23.bingoreloadedcompanion.network;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.io.IOException;

public class StreamCodecs {

	public static <T> StreamCodec<RegistryFriendlyByteBuf, T> fromByteCodec(ByteCodec<T> codec) {
		return StreamCodec.of((buf, value) -> {
			try {
				codec.encode(new ByteBufOutputStream(buf), value);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, (buf) -> {
			try {
				return codec.decode(new ByteBufInputStream(buf));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static <T> StreamCodec<RegistryFriendlyByteBuf, T> usingByteCodec(ByteCodec.ByteEncoder<T> encoder, ByteCodec.ByteDecoder<T> decoder) {
		return fromByteCodec(ByteCodec.create(encoder, decoder));
	}
}
