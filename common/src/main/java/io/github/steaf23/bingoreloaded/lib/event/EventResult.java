package io.github.steaf23.bingoreloaded.lib.event;

import org.jetbrains.annotations.Nullable;

public record EventResult<EventData>(boolean cancel, @Nullable EventData data) {

	public static final EventResult<?> PASS = new EventResult<>(false, null);
	public static final EventResult<?> CANCEL = new EventResult<>(true, null);
}
