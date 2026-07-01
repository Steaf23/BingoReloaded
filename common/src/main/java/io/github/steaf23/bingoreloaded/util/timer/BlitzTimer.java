package io.github.steaf23.bingoreloaded.util.timer;

import org.jetbrains.annotations.Nullable;

public class BlitzTimer extends CountdownTimer {

	int bonusTime;

	public BlitzTimer(int startTime, int bonusTime, @Nullable Runnable onTimeoutCallback) {
		super(startTime, 30, 10, onTimeoutCallback);
		this.bonusTime = bonusTime;
	}

	public void reset() {
		updateTime(getTime() + bonusTime);
	}
}
