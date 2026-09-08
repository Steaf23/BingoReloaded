package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;

public record ConfiguredTask(TaskId id, int count) {
	public static final ByteCodec<ConfiguredTask> CODEC = ByteCodec.create(
			(buf, task) -> {
				TaskId.CODEC.encode(buf, task.id);
				ByteCodec.INT.encode(buf, task.count);
			}, (buf) -> new ConfiguredTask(
					TaskId.CODEC.decode(buf),
					ByteCodec.INT.decode(buf)
			)
	);
}
