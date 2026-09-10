package io.github.steaf23.bingoreloaded.protocol.data.card;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;

public record ListReference(String name, int min, int max) {
	public static final ByteCodec<ListReference> CODEC = ByteCodec.create(
			(buf, data) -> {
				ByteCodec.STRING.encode(buf, data.name);
				ByteCodec.INT.encode(buf, data.min);
				ByteCodec.INT.encode(buf, data.max);
			}, (buf) -> new ListReference(
					ByteCodec.STRING.decode(buf),
					ByteCodec.INT.decode(buf),
					ByteCodec.INT.decode(buf)
			));
}
