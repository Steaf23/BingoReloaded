package io.github.steaf23.bingoreloaded.lib.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {

	private final Map<Class<?>, List<EventMethod<?>>> registeredMethods = new HashMap<>();

	public synchronized <T> EventBus registerMethod(Class<T> eventType, EventMethod<T> listener) {
		registeredMethods.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> void callEvent(T event) {
		List<EventMethod<?>> list = registeredMethods.getOrDefault(event.getClass(), List.of());
		for (EventMethod<?> listener : list) {
			((EventMethod<T>) listener).handleEvent(event);
		}
	}

	@FunctionalInterface
	public interface EventMethod<T> {
		void handleEvent(T event);
	}
}
