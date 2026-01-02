package io.github.steaf23.bingoreloaded.lib.event;

import org.jetbrains.annotations.Nullable;

public record EventResult<EventData>(boolean consume, @Nullable EventData data) {

	public static final EventResult<?> IGNORE = new EventResult<>(false, null);
	public static final EventResult<?> CONSUME = new EventResult<>(true, null);
}
