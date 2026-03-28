package io.github.steaf23.bingoreloaded.util.timer;

import org.jetbrains.annotations.Nullable;

public class BlitzTimer extends CountdownTimer {

	int time;

	public BlitzTimer(int startTime, int refreshTime, @Nullable Runnable onTimeoutCallback) {
		super(startTime, 30, 10, onTimeoutCallback);
		this.time = refreshTime;
	}

	public void reset() {
		updateTime(time);
	}
}
