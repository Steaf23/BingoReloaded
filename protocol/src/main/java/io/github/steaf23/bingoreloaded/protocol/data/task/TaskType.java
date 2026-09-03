package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;

public enum TaskType {
	ITEM,
	ADVANCEMENT,
	STATISTIC,
	;

	public static final ByteCodec<TaskType> CODEC = ByteCodec.STRING.map(TaskType::valueOf, Enum::name);
}
