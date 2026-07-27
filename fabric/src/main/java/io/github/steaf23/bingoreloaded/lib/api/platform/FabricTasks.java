package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.ExtensionTask;
import io.github.steaf23.bingoreloaded.lib.api.ExtensionTaskFabric;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FabricTasks implements PlatformTasks {

	private final List<Task> tasks = new ArrayList<>();

	public void tick(int currentTick) {
		List<Task> tasksToRemove = new ArrayList<>();

		for (Task t : tasks) {
			if (t.tryRunAndCancel(currentTick)) {
				tasksToRemove.add(t);
			}
		}

		for (Task t : tasksToRemove) {
			tasks.remove(t);
		}
	}

	@Override
	public ExtensionTask runTaskTimer(long repeatTicks, long startDelayTicks, Consumer<ExtensionTask> consumer) {
		ExtensionTaskFabric extensionTask = new ExtensionTaskFabric();
		tasks.add(new Task(extensionTask, startDelayTicks, consumer, repeatTicks));
		return extensionTask;
	}

	@Override
	public ExtensionTask runTask(Consumer<ExtensionTask> consumer) {
		ExtensionTaskFabric extensionTask = new ExtensionTaskFabric();
		tasks.add(new Task(extensionTask, 0, consumer));
		return extensionTask;
	}

	@Override
	public ExtensionTask runTask(long startDelayTicks, Consumer<ExtensionTask> consumer) {
		ExtensionTaskFabric extensionTask = new ExtensionTaskFabric();
		tasks.add(new Task(extensionTask, startDelayTicks, consumer));
		return extensionTask;
	}

	private static class Task {

		private final ExtensionTask outerTask;
		private final Consumer<ExtensionTask> task;
		private final boolean repeatable;
		private final long repeatInterval;
		private final long startDelay;
		private int lastRun;
		private long startAtTick;
		private boolean firstTick;

		private Task(ExtensionTask outerTask, long startDelay, Consumer<ExtensionTask> task, boolean repeatable, long repeatInterval) {
			this.outerTask = outerTask;
			this.startDelay = startDelay;
			this.task = task;
			this.repeatable = repeatable;
			this.repeatInterval = repeatInterval;

			this.lastRun = -1;
			this.firstTick = true;
			this.startAtTick = -1;
		}

		private Task(ExtensionTask outerTask, long startAtTick, Consumer<ExtensionTask> task) {
			this(outerTask, startAtTick, task, false, 0);
		}

		private Task(ExtensionTask outerTask, long startAtTick, Consumer<ExtensionTask> task, long repeatInterval) {
			this(outerTask, startAtTick, task, true, repeatInterval);
		}

		boolean isStarted(int currentTick) {
			return currentTick > startAtTick;
		}

		boolean shouldStartNow(int currentTick) {
			return startAtTick == currentTick;
		}

		// returns true if it should be canceled after.
		boolean tryRunAndCancel(int currentTick) {
			if (firstTick) {
				startAtTick = currentTick + startDelay;
				firstTick = false;
			}

			if (isStarted(currentTick)) {
				boolean continueNow = currentTick > lastRun + repeatInterval;
				lastRun = currentTick;
				task.accept(outerTask);
				return outerTask.isCancelled();
			}
			else if (shouldStartNow(currentTick)) {
				lastRun = currentTick;
				task.accept(outerTask);
				return !repeatable || outerTask.isCancelled();
			} else {
				return false;
			}
		}

		ExtensionTask outerTask() {
			return outerTask;
		}
	}
}
