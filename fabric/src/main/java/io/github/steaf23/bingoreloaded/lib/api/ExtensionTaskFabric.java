package io.github.steaf23.bingoreloaded.lib.api;

public class ExtensionTaskFabric implements ExtensionTask {

	boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isSync() {
		return true;
	}

	@Override
	public void cancel() {
		cancelled = true;
	}
}
