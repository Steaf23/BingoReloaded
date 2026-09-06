package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;
import net.kyori.adventure.key.Key;

public record TaskDefinition(
		TaskId id,
		String name,
		String description,
		Key iconItem,
		String category,
		int maxCount) {

	public static final ByteCodec<TaskDefinition> CODEC = ByteCodec.create(
			(output, def) -> {
				TaskId.CODEC.encode(output, def.id());
				output.writeUTF(def.name());
				output.writeUTF(def.description());
				ByteCodec.KEY.encode(output, def.iconItem());
				output.writeUTF(def.category());
				output.writeInt(def.maxCount());
			}, (input) -> new TaskDefinition(
					TaskId.CODEC.decode(input),
					input.readUTF(),
					input.readUTF(),
					ByteCodec.KEY.decode(input),
					input.readUTF(),
					input.readInt()));

	public TaskType type() {
		return id.type();
	}

	public boolean passesFilter(String filter) {
		if (filter.isBlank()) {
			return true;
		}

		return name.toLowerCase().contains(filter.toLowerCase());
	}
}
