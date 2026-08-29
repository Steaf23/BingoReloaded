package io.github.steaf23.bingoreloadedcompanion.card.taskslot;

import io.github.steaf23.bingoreloadedcompanion.network.PayloadHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum TaskType {
	ITEM,
	ADVANCEMENT,
	STATISTIC,
	;

	public static final StreamCodec<ByteBuf, TaskType> STREAM_CODEC = PayloadHelper.STRING_CODEC.map(TaskType::valueOf, Enum::name);
}
