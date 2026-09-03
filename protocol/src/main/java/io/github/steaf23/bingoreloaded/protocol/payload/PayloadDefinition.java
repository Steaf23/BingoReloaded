package io.github.steaf23.bingoreloaded.protocol.payload;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

public record PayloadDefinition<T>(Key key, ByteCodec<T> codec) implements Keyed {

	public PayloadDefinition(String id, ByteCodec<T> codec) {
		this(Key.key("bingoreloaded", id), codec);
	}
}
