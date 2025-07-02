package io.github.steaf23.bingoreloaded.lib.event;

public class Event {
	private boolean isCancelled = false;

	public boolean isCancelled() {
		return isCancelled;
	}

	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}
}
