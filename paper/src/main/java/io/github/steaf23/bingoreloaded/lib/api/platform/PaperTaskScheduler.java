package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.ExtensionTask;
import io.github.steaf23.bingoreloaded.lib.api.ExtensionTaskPaper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class PaperTaskScheduler implements PlatformTaskScheduler {

	private final JavaPlugin plugin;

	public PaperTaskScheduler(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public ExtensionTask runTaskTimer(long repeatTicks, long startDelayTicks, Consumer<ExtensionTask> consumer) {
		ExtensionTaskPaper wrapper = new ExtensionTaskPaper();

		Bukkit.getScheduler().runTaskTimer(plugin, (BukkitTask task) -> {
			wrapper.setTask(task);
			consumer.accept(wrapper);
		}, startDelayTicks, repeatTicks);

		return wrapper;
	}

	@Override
	public ExtensionTask runTask(Consumer<ExtensionTask> consumer) {
		ExtensionTaskPaper wrapper = new ExtensionTaskPaper();

		Bukkit.getScheduler().runTask(plugin, (BukkitTask task) -> {
			wrapper.setTask(task);
			consumer.accept(wrapper);
		});

		return wrapper;
	}

	@Override
	public ExtensionTask runTask(long startDelayTicks, Consumer<ExtensionTask> consumer) {
		if (startDelayTicks <= 0) {
			return runTask(consumer);
		}
		else {
			ExtensionTaskPaper wrapper = new ExtensionTaskPaper();

			Bukkit.getScheduler().runTaskLater(plugin, (BukkitTask task) -> {
				wrapper.setTask(task);
				consumer.accept(wrapper);
			}, startDelayTicks);

			return wrapper;
		}
	}
}
