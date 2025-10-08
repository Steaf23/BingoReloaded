package io.github.steaf23.bingoreloaded.lib.api;

public interface ExtensionTask {
	boolean isCancelled();
	boolean isSync();

	void cancel();
}
