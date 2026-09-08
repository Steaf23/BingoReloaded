package io.github.steaf23.bingoreloaded.protocol.data.card;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import io.github.steaf23.bingoreloaded.protocol.data.task.ConfiguredTask;

import java.util.List;

public record CustomList(String name, List<ConfiguredTask> tasks, boolean readOnly) {
	public static final ByteCodec<CustomList> CODEC = ByteCodec.create(
			(buf, list) -> {
				ByteCodec.STRING.encode(buf, list.name);
				ConfiguredTask.CODEC.list().encode(buf, list.tasks);
				ByteCodec.BOOL.encode(buf, list.readOnly);
			}, (buf) -> new CustomList(
					ByteCodec.STRING.decode(buf),
					ConfiguredTask.CODEC.list().decode(buf),
					ByteCodec.BOOL.decode(buf)
			));
}
