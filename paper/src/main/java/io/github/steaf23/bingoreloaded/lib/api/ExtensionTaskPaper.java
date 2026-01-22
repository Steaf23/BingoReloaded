package io.github.steaf23.bingoreloaded.lib.api;

import org.bukkit.scheduler.BukkitTask;

public class ExtensionTaskPaper implements ExtensionTask {

	private BukkitTask task;

	public ExtensionTaskPaper() {
	}

	public void setTask(BukkitTask task) {
		this.task = task;
	}

	@Override
	public boolean isCancelled() {
		return task.isCancelled();
	}

	@Override
	public void cancel() {
		task.cancel();
	}
}
