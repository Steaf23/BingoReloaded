package io.github.steaf23.bingoreloaded.platform;

import io.github.steaf23.bingoreloaded.lib.api.ExtensionTask;

import java.util.UUID;
import java.util.function.Consumer;

public class TickingTask implements ExtensionTask {

	private final TaskTicker ticker;

	private final double intervalSeconds;

	private final double startDelay;

	private final boolean oneShot;

	private final Consumer<ExtensionTask> runner;

	private final UUID worldId;

	double current = 0.0;
	boolean started = false;
	boolean cancelled = false;

	private TickingTask(TaskTicker ticker, Consumer<ExtensionTask> runner, double startDelay, double intervalSeconds, boolean oneShot, UUID worldId) {
		this.ticker = ticker;
		this.intervalSeconds = intervalSeconds;
		this.runner = runner;
		this.startDelay = startDelay;
		this.oneShot = oneShot;
		this.worldId = worldId;
	}

	public static TickingTask delayedTask(TaskTicker ticker, Consumer<ExtensionTask> runner, double startDelay, UUID worldId) {
		return new TickingTask(ticker, runner, startDelay, 0, true, worldId);
	}

	public static TickingTask timerTask(TaskTicker ticker, Consumer<ExtensionTask> runner, double startDelay, double interval, UUID worldId) {
		return new TickingTask(ticker, runner, startDelay, interval, false, worldId);
	}

	public UUID world() {
		return worldId;
	}

	public void tick(float dt) {
		current += dt;

		if (oneShot && current >= startDelay) {
			runner.accept(this);
			cancel();
		} else if (current >= startDelay && !started) {
			started = true;
			current = 0;
			runner.accept(this);
		}

		if (started && current >= intervalSeconds) {
			current = 0;
			runner.accept(this);
		}
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void cancel() {
		cancelled = true;
		ticker.cancelTask(this);
	}
}
