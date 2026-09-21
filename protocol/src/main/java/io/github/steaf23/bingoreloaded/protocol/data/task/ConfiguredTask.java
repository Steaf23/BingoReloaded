package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;

import java.util.Set;

public record ConfiguredTask(TaskId id, int count, Set<String> tags) {
	public static final ByteCodec<ConfiguredTask> CODEC = ByteCodec.create(
			(buf, task) -> {
				TaskId.CODEC.encode(buf, task.id);
				ByteCodec.INT.encode(buf, task.count);
				ByteCodec.STRING.hashSet().encode(buf, task.tags);
			}, (buf) -> new ConfiguredTask(
					TaskId.CODEC.decode(buf),
					ByteCodec.INT.decode(buf),
					ByteCodec.STRING.hashSet().decode(buf)
			)
	);
}
