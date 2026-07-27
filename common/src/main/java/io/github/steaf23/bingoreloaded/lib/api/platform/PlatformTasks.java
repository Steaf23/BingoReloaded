package io.github.steaf23.bingoreloaded.lib.api.platform;

import io.github.steaf23.bingoreloaded.lib.api.ExtensionTask;

import java.util.function.Consumer;

public interface PlatformTasks {
	ExtensionTask runTaskTimer(long repeatTicks, long startDelayTicks, Consumer<ExtensionTask> consumer);
	ExtensionTask runTask(Consumer<ExtensionTask> consumer);
	ExtensionTask runTask(long startDelayTicks, Consumer<ExtensionTask> consumer);
}
