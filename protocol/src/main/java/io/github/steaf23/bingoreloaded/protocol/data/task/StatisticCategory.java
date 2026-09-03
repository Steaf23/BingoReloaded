package io.github.steaf23.bingoreloaded.protocol.data.task;

import io.github.steaf23.bingoreloaded.protocol.codec.ByteCodec;

public enum StatisticCategory {
	TRAVEL(Type.CUSTOM),
	BLOCK_INTERACT(Type.CUSTOM),
	CONTAINER_INTERACT(Type.CUSTOM),
	DAMAGE(Type.CUSTOM),
	MISC(Type.CUSTOM),
	USED(Type.ITEM),
	BROKEN(Type.ITEM),
	CRAFTED(Type.ITEM),
	DROPPED(Type.ITEM),
	PICKED_UP(Type.ITEM),
	MINED(Type.BLOCK),
	ENTITY_KILLED(Type.ENTITY),
	KILLED_BY(Type.ENTITY),
	;

	public final Type type;

	StatisticCategory(Type type) {
		this.type = type;
	}

	public enum Type {
		CUSTOM,
		ITEM,
		BLOCK,
		ENTITY,
	}

	public static final ByteCodec<StatisticCategory> CODEC = ByteCodec.STRING.map(StatisticCategory::valueOf, Enum::name);
}
