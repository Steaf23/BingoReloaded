package io.github.steaf23.bingoreloaded.protocol.data;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;

public record TaskSlot(long totalTimeSeconds, long currentTimeSeconds, boolean recovering, boolean expires) {

	public static final ByteCodec<TaskSlot> CODEC = ByteCodec.create(
			(buf, slot) -> {
				buf.writeLong(slot.totalTimeSeconds());
				buf.writeLong(slot.currentTimeSeconds());
				ByteCodec.BOOL.encode(buf, slot.recovering);
				ByteCodec.BOOL.encode(buf, slot.expires);
			}, (buf) -> new TaskSlot(
					buf.readLong(),
					buf.readLong(),
					ByteCodec.BOOL.decode(buf),
					ByteCodec.BOOL.decode(buf)));
}
